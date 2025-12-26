package com.simulation.reactgame.v2;

import com.simulation.reactgame.dto.RecordRequest;
import com.simulation.reactgame.dto.RecordResponse;
import com.simulation.reactgame.RecordRepository;
import com.simulation.reactgame.RecordService;
import com.simulation.reactgame.RedisRecordService;
import com.simulation.reactgame.entity.Record;
import com.simulation.reactgame.infra.RedisRecordKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@Qualifier("redis")
@RequiredArgsConstructor
public class RecordServiceRedisImpl implements RecordService, RedisRecordService {

    private final RecordRepository recordRepository;
    private final StringRedisTemplate redisTemplate;
    private static final String RECORD_KEY = RedisRecordKey.RECORD_KEY;

    @Override
    public com.simulation.reactgame.RecordRankingView registerRecord(RecordRequest.RegisterDto registerDto) {
        Record saved = recordRepository.save(toEntity(registerDto));

        redisTemplate.opsForZSet().add(RECORD_KEY, saved.getName(), saved.getAverageTime());

        return recordRepository.findRankingById(saved.getId())
                .orElseThrow(() -> new com.simulation.global.error.CustomException(
                        com.simulation.global.error.ErrorCode.ENTITY_NOT_FOUND));
    }

    @Override
    public RecordResponse.RankList getRecords() {
        List<String> nameList = redisTemplate.opsForZSet().reverseRangeWithScores(RECORD_KEY, 0, 9).stream()
                .map(ZSetOperations.TypedTuple::getValue)
                .toList();

        return RecordResponse.RankList.builder()
                .rankList(
                        recordRepository.findRecordsByNameIn(nameList).stream()
                                .sorted(Comparator.comparingDouble(Record::getAverageTime))
                                .map(this::toResponse)
                                .toList())
                .build();
    }

    @Override
    public RecordResponse.RankList getMyRecords(String name) {
        Long rank = redisTemplate.opsForZSet().reverseRank(RECORD_KEY, name);
        Long size = redisTemplate.opsForZSet().size(RECORD_KEY); // O(1)

        long start = Math.max(rank - 5, 0);
        long end = Math.min(rank + 5, size - 1);

        Set<String> nameSet = redisTemplate.opsForZSet().reverseRange(RECORD_KEY, start, end);

        // get record and sorting
        List<Record> recordsByNameIn = recordRepository.findRecordsByNameIn(nameSet);
        recordsByNameIn.sort(Comparator.comparingDouble(Record::getAverageTime));

        return RecordResponse.RankList.builder()
                .rankList(recordsByNameIn.stream()
                        .map(this::toResponse)
                        .toList())
                .build();
    }

    private Record toEntity(RecordRequest.RegisterDto registerDto) {
        return Record.builder()
                .name(registerDto.getName())
                .attempt1(registerDto.getAttempt1())
                .attempt2(registerDto.getAttempt2())
                .attempt3(registerDto.getAttempt3())
                .build();
    }

    private RecordResponse.RankDto toResponse(Record record) {
        return RecordResponse.RankDto.builder()
                .id(record.getId())
                .name(record.getName())
                .score(record.getAverageTime())
                .build();
    }

    @Override
    public void rebuildRanking() {
        int page = 0;
        int size = 1000;

        redisTemplate.delete(RECORD_KEY);

        log.info("redis rebuildRanking");
        while (true) {
            Page<Record> result = recordRepository.findAll(PageRequest.of(page, size));
            log.info("redis warm page: {}", page);
            if (result.isEmpty())
                break;

            redisTemplate.executePipelined(getObjectRedisCallback(result));

            page++;
        }
    }

    private RedisCallback<Object> getObjectRedisCallback(Page<Record> result) {
        return connection -> {
            byte[] key = serialize(RECORD_KEY);

            for (Record record : result) {
                connection.zAdd(
                        key,
                        record.getAverageTime(),
                        serialize(record.getName()));
            }
            return null;
        };
    }

    private byte[] serialize(String key) {
        return redisTemplate.getStringSerializer().serialize(key);
    }

}

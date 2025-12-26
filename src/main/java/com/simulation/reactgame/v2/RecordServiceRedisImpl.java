package com.simulation.reactgame.v2;

import com.simulation.reactgame.dto.RecordRequest;
import com.simulation.reactgame.dto.RecordResponse;
import com.simulation.reactgame.repository.RecordRepository;
import com.simulation.reactgame.service.RecordService;
import com.simulation.reactgame.service.RedisRecordService;
import com.simulation.reactgame.entity.Record;
import com.simulation.reactgame.infra.RedisRecordKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@Transactional
@Qualifier("redis")
@RequiredArgsConstructor
public class RecordServiceRedisImpl implements RecordService, RedisRecordService {

    private final RecordRepository recordRepository;
    private final StringRedisTemplate redisTemplate;
    private static final String RECORD_KEY = RedisRecordKey.RECORD_KEY;

    @Override
    public RecordResponse.RankDto registerRecord(RecordRequest.RegisterDto registerDto) {
        Record saved = recordRepository.save(toEntity(registerDto));

        redisTemplate.opsForZSet().add(RECORD_KEY, saved.getName(), saved.getAverageTime());
        Long rank = redisTemplate.opsForZSet().rank(RECORD_KEY, saved.getName());

        return toResponse(saved, rank + 1);
    }

    @Override
    public RecordResponse.RankList getRecords() {
        Set<String> names = redisTemplate.opsForZSet().range(RECORD_KEY, 0, 9);
        long[] currentRank = { 1 };

        return RecordResponse.RankList.builder()
                .rankList(
                        recordRepository.findRecordsByNameIn(names != null ? names : Collections.emptySet()).stream()
                                .sorted(Comparator.comparingDouble(Record::getAverageTime))
                                .map(record -> toResponse(record, currentRank[0]++))
                                .toList())
                .build();
    }

    @Override
    public RecordResponse.RankList getMyRecords(String name) {
        Long rank = redisTemplate.opsForZSet().rank(RECORD_KEY, name);
        Long size = redisTemplate.opsForZSet().size(RECORD_KEY); // O(1)

        long start = Math.max(rank - 5, 0);
        long end = Math.min(rank + 5, size - 1);

        Set<String> nameSet = redisTemplate.opsForZSet().range(RECORD_KEY, start, end);

        // get record and sorting
        List<Record> recordsByNameIn = recordRepository
                .findRecordsByNameIn(nameSet != null ? nameSet : Collections.emptySet());
        recordsByNameIn.sort(Comparator.comparingDouble(Record::getAverageTime));

        long[] currentRank = { start + 1 };

        return RecordResponse.RankList.builder()
                .rankList(recordsByNameIn.stream()
                        .map(record -> toResponse(record, currentRank[0]++))
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

    private RecordResponse.RankDto toResponse(Record record, long rank) {
        return RecordResponse.RankDto.builder()
                .id(record.getId())
                .name(record.getName())
                .score(record.getAverageTime())
                .rank(rank)
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

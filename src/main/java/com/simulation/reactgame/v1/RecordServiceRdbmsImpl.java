package com.simulation.reactgame.v1;

import com.simulation.global.error.CustomException;
import com.simulation.global.error.ErrorCode;
import com.simulation.reactgame.RecordRankingView;
import com.simulation.reactgame.dto.RecordRequest;
import com.simulation.reactgame.dto.RecordResponse;
import com.simulation.reactgame.RecordRepository;
import com.simulation.reactgame.RecordService;
import com.simulation.reactgame.entity.Record;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Qualifier("rdbms")
@RequiredArgsConstructor
public class RecordServiceRdbmsImpl implements RecordService {

    private final RecordRepository recordRepository;

    @Override
    public RecordRankingView registerRecord(RecordRequest.RegisterDto registerDto) {
        Record record = toEntity(registerDto);
        Record saved = recordRepository.save(record);

        recordRepository.flush();

        return recordRepository.findRankingById(saved.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND));
    }

    @Override
    public RecordResponse.RankList getRecords() {
        return RecordResponse.RankList.builder()
                .rankList(
                        recordRepository.findTop10Ranking()
                                .stream()
                                .map(this::toResponse)
                                .toList()
                )
                .build();
    }

    @Override
    public RecordResponse.RankList getMyRecords(String name) {
        return RecordResponse.RankList.builder()
                .rankList(
                        recordRepository.findRecordNearByMe10(name)
                                .stream()
                                .map(this::toResponse)
                                .toList()
                )
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

    private RecordResponse.RankDto toResponse(RecordRankingView record) {
        return RecordResponse.RankDto.builder()
                .id(record.getId())
                .name(record.getName())
                .score(record.getAverageTime())
                .rank(record.getRank())
                .build();
    }

}

package com.simulation.domain;

import com.simulation.dto.RecordRequest;
import com.simulation.dto.RecordResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecordService {

    private final RecordRepository recordRepository;

    public RecordResponse.RankDto registerRecord(RecordRequest.RegisterDto registerDto) {
        Record record = toEntity(registerDto);
        Record saved = recordRepository.save(record);

        return toResponse(saved);
    }

    private RecordResponse.RankDto toResponse(Record record) {
        return RecordResponse.RankDto.builder()
                .id(record.getId())
                .name(record.getName())
                .score(record.getAverageTime())
                .build();
    }

    public RecordResponse.RankList getRecords() {
        return RecordResponse.RankList.builder()
                .rankList(
                        recordRepository.findTop10ByOrderByAverageTime()
                                .stream()
                                .map(this::toResponse)
                                .toList()
                )
                .build();
    }

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
}

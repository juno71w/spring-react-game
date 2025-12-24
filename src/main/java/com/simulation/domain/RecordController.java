package com.simulation.domain;

import com.simulation.dto.RecordRequest;
import com.simulation.dto.RecordResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/records")
public class RecordController {

    private final RecordService recordService;

    @PostMapping
    public ResponseEntity<RecordResponse.RankDto> registerRecord(
            @RequestBody RecordRequest.RegisterDto registerDto
    ) {
        RecordResponse.RankDto rankDto = recordService.registerRecord(registerDto);
        return ResponseEntity.ofNullable(rankDto);

    }

    @GetMapping
    public ResponseEntity<RecordResponse.RankList> getRecords() {
        RecordResponse.RankList list = recordService.getRecords();
        return ResponseEntity.ofNullable(list);
    }

    @GetMapping("/me")
    public ResponseEntity<RecordResponse.RankList> getMyRecords(
            @RequestParam String name
    ) {
        RecordResponse.RankList list = recordService.getMyRecords(name);
        return ResponseEntity.ofNullable(list);
    }
}

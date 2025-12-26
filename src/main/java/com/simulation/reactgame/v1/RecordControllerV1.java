package com.simulation.reactgame.v1;

import com.simulation.reactgame.dto.RecordRequest;
import com.simulation.reactgame.dto.RecordResponse;
import com.simulation.reactgame.service.RecordService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/records")
public class RecordControllerV1 {

    private final RecordService recordService;

    public RecordControllerV1(@Qualifier("rdbms") RecordService recordService) {
        this.recordService = recordService;
    }

    @PostMapping
    public ResponseEntity<RecordResponse.RankDto> registerRecord(
            @RequestBody RecordRequest.RegisterDto registerDto) {
        RecordResponse.RankDto view = recordService.registerRecord(registerDto);
        return ResponseEntity.ofNullable(view);
    }

    @GetMapping
    public ResponseEntity<RecordResponse.RankList> getRecords() {
        RecordResponse.RankList list = recordService.getRecords();
        return ResponseEntity.ofNullable(list);
    }

    @GetMapping("/me")
    public ResponseEntity<RecordResponse.RankList> getMyRecords(
            @RequestParam String name) {
        RecordResponse.RankList list = recordService.getMyRecords(name);
        return ResponseEntity.ofNullable(list);
    }
}

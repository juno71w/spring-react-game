package com.simulation.reactgame;

import com.simulation.reactgame.dto.RecordRequest;
import com.simulation.reactgame.dto.RecordResponse;

public interface RecordService {

    RecordResponse.RankDto registerRecord(RecordRequest.RegisterDto registerDto);

    RecordResponse.RankList getRecords();

    RecordResponse.RankList getMyRecords(String name);

}

package com.simulation.reactgame;

import com.simulation.reactgame.dto.RecordRequest;
import com.simulation.reactgame.dto.RecordResponse;

public interface RecordService {

    RecordRankingView registerRecord(RecordRequest.RegisterDto registerDto);

    RecordResponse.RankList getRecords();

    RecordResponse.RankList getMyRecords(String name);

}

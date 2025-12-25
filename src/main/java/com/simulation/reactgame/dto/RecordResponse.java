package com.simulation.reactgame.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class RecordResponse {

    @Getter
    @Builder
    public static class RankList {
        private List<RankDto> rankList;
    }

    @Getter
    @Builder
    public static class RankDto {
        private Long id;
        private String name;
        private float score;
    }
}

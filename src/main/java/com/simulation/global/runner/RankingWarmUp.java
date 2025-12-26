package com.simulation.global.runner;

import com.simulation.reactgame.service.RedisRecordService;
import com.simulation.reactgame.infra.RedisRecordKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RankingWarmUp {

    private static final String RANKING_KEY = RedisRecordKey.RECORD_KEY;

    private final RedisTemplate<String, String> redisTemplate;
    private final RedisRecordService recordService;

    public RankingWarmUp(
            RedisTemplate<String, String> redisTemplate,
            RedisRecordService recordService
    ) {
        this.redisTemplate = redisTemplate;
        this.recordService = recordService;
    }

    public void warmUp() {
        redisTemplate.delete(RANKING_KEY);

        log.info("redis warm up start");
        recordService.rebuildRanking();

        log.info("redis warm up finish");
    }
}
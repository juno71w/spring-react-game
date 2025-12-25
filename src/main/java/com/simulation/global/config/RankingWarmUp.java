package com.simulation.global.config;

import com.simulation.reactgame.RecordService;
import com.simulation.reactgame.RedisRecordService;
import com.simulation.reactgame.infra.RedisRecordKey;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
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

    @PostConstruct
    public void warmUp() {
        log.info("redis check key");
        Boolean exists = redisTemplate.hasKey(RANKING_KEY);
        if (Boolean.FALSE.equals(exists)) {
            log.info("redis warm up start");
            recordService.rebuildRanking();
        }
    }
}
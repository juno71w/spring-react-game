package com.simulation.global.config.monitoring;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.resource.jdbc.spi.StatementInspector;

@Slf4j
public class QueryCountInspector implements StatementInspector {
    @Override
    public String inspect(String sql) {
        // HTTP 요청 컨텍스트
        RequestContext requestContext = RequestContextHolder.getContext();
        if (requestContext != null) {
            requestContext.incrementQueryCount(sql);
        }

        // 배치 컨텍스트
        BatchContext batchContext = BatchContextHolder.getContext();
        if (batchContext != null) {
            batchContext.incrementQueryCount(sql);
        }

        return sql;
    }
}

package com.simulation.global.runner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
@Component
@RequiredArgsConstructor
public class SqlRunner implements CommandLineRunner {

    private final DataSource dataSource;
    private final RankingWarmUp warmUp;

    @Override
    public void run(String... args) throws Exception {
        log.info("====================== data initiation start ======================");
        sqlRun("schema.sql");
        sqlRun("data.sql");
        redisRun();
        log.info("====================== data initiation finish ======================");
    }

    private void sqlRun(String filePath) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(conn, new ClassPathResource(filePath));
        }
    }

    private void redisRun() {
        warmUp.warmUp();
    }
}
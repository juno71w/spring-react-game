package com.simulation.reactgame.repository;

import com.simulation.reactgame.entity.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface RecordRepository extends JpaRepository<Record, Long> {

    @Query(value = """
            SELECT
                id,
                name,
                average_time,
                ROW_NUMBER() OVER (ORDER BY average_time ASC, name ASC) AS `rank`
            FROM records
            ORDER BY average_time ASC
            LIMIT 10
            """, nativeQuery = true)
    List<RecordRankingView> findTop10Ranking();

    @Query(value = """
        WITH RankedRecords AS (
            /* 1. 모든 레코드에 대해 먼저 순위를 매깁니다. */
            SELECT id, name, average_time, 
                   ROW_NUMBER() OVER (ORDER BY average_time ASC, name ASC) AS `rank`
            FROM records
        )
        SELECT id, name, average_time, `rank`
        FROM RankedRecords
        WHERE `rank` BETWEEN 
            (SELECT `rank` FROM RankedRecords WHERE name = :name) - 5 
            AND 
            (SELECT `rank` FROM RankedRecords WHERE name = :name) + 5
        ORDER BY `rank` ASC
    """, nativeQuery = true)
    List<RecordRankingView> findRecordNearByMe10(@Param("name") String name);

    List<Record> findRecordsByNameIn(Collection<String> names);

    @Query(value = """
            SELECT sub.id, sub.name, sub.average_time, sub.`rank`
            FROM (
                SELECT id, name, average_time, ROW_NUMBER() OVER (ORDER BY average_time ASC, name ASC) AS `rank`
                FROM records
            ) sub
            WHERE sub.id = :recordId
            """, nativeQuery = true)
    Optional<RecordRankingView> findRankingById(@Param("recordId") Long recordId);
}

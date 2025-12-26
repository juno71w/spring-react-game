package com.simulation.reactgame;

import com.simulation.reactgame.dto.RecordResponse;
import com.simulation.reactgame.entity.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface RecordRepository extends JpaRepository<com.simulation.reactgame.entity.Record, Long> {

    @Query(value = """
            SELECT
                id,
                name,
                average_time,
                RANK() OVER (ORDER BY average_time ASC, created_at DESC) AS `rank`
            FROM records
            ORDER BY average_time ASC
            LIMIT 10
            """, nativeQuery = true)
    List<RecordRankingView> findTop10Ranking();

    @Query(value = """
            SELECT sub.id, sub.name, sub.average_time, sub.`rank`
            FROM (
                SELECT id, name, average_time, RANK() OVER (ORDER BY average_time ASC, created_at DESC) as `rank`
                FROM records
            ) sub
            JOIN (
                (SELECT id FROM records
                 WHERE average_time > (SELECT average_time FROM records WHERE name = :name)
                 ORDER BY average_time ASC LIMIT 5)
                UNION
                (SELECT id FROM records
                 WHERE name = :name)
                UNION
                (SELECT id FROM records
                 WHERE average_time < (SELECT average_time FROM records WHERE name = :name)
                 ORDER BY average_time DESC LIMIT 5)
            ) targets ON sub.id = targets.id
            ORDER BY sub.average_time ASC
            """, nativeQuery = true)
    List<RecordRankingView> findRecordNearByMe10(@Param("name") String name);

    List<Record> findRecordsByNameIn(Collection<String> names);

    @Query(value = """
            SELECT sub.id, sub.name, sub.average_time, sub.`rank`
            FROM (
                SELECT id, name, average_time, RANK() OVER (ORDER BY average_time ASC, created_at DESC) as `rank`
                FROM records
            ) sub
            WHERE sub.id = :recordId
            """, nativeQuery = true)
    Optional<RecordRankingView> findRankingById(@Param("recordId") Long recordId);
}

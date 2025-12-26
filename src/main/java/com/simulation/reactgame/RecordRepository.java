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
                RANK() OVER (ORDER BY average_time ASC) AS rank
            FROM records
            ORDER BY average_time ASC
            LIMIT 10
            """, nativeQuery = true)
    List<RecordRankingView> findTop10Ranking();

    @Query(value = """
            SELECT *,
                   RANK() OVER (ORDER BY average_time ASC) AS rank
            FROM (
                (SELECT * FROM records
                 WHERE average_time > (SELECT average_time FROM records WHERE name = :name)
                 ORDER BY average_time ASC LIMIT 5)
                UNION ALL

                (SELECT * FROM records
                 WHERE name = :name)

                UNION ALL

                (SELECT *
            	FROM (
            	  SELECT *
            	  FROM records
            	  WHERE average_time < (SELECT average_time FROM records WHERE name = "박준호")
            	  ORDER BY average_time ASC
            	  LIMIT 5
            	) t
            	ORDER BY average_time DESC)

                ORDER BY average_time ASC
            ) u
            """, nativeQuery = true)
    List<RecordRankingView> findRecordNearByMe10(@Param("name") String name);

    List<Record> findRecordsByNameIn(Collection<String> names);

    @Query(value = """
            SELECT
                id,
                name,
                average_time,
                RANK() OVER (ORDER BY average_time ASC) AS rank
            FROM records
            WHERE id = :recordId
            """, nativeQuery = true)
    RecordResponse.RankDto findRankByRecordId(@Param("recordId") Long recordId);

    @Query(value = """
            SELECT sub.id, sub.name, sub.average_time, sub.rank
            FROM (
                SELECT id, name, average_time, RANK() OVER (ORDER BY average_time ASC) as rank
                FROM records
            ) sub
            WHERE sub.id = :recordId
            """, nativeQuery = true)
    Optional<RecordRankingView> findRankingById(@Param("recordId") Long recordId);
}

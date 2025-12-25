package com.simulation.reactgame;

import com.simulation.reactgame.entity.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface RecordRepository extends JpaRepository<com.simulation.reactgame.entity.Record, Long> {

    List<com.simulation.reactgame.entity.Record> findTop10ByOrderByAverageTime();

    @Query(value = """
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
    """, nativeQuery = true)
    List<Record> findRecordNearByMe10(@Param("name") String name);

    List<Record> findRecordsByNameIn(Collection<String> names);
}

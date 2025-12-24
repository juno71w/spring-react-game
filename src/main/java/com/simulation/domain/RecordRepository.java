package com.simulation.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecordRepository extends JpaRepository<Record, Long> {

    List<Record> findTop10ByOrderByAverageTime();

    @Query(value = """

    (SELECT * FROM records 
     WHERE average_time > (SELECT average_time FROM records WHERE name = :name)
     ORDER BY average_time ASC LIMIT 5)
    UNION ALL
    
    (SELECT * FROM records 
     WHERE name = :name)
    
    UNION ALL
    
    (SELECT * FROM records 
     WHERE average_time < (SELECT average_time FROM records WHERE name = :name)
     ORDER BY average_time DESC LIMIT 5)
    
    ORDER BY average_time ASC
    """, nativeQuery = true)
    List<Record> findRecordNearByMe10(@Param("name") String name);
}

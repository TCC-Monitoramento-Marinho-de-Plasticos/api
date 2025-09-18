package com.example.marine.monitoring.api.repository;

import com.example.marine.monitoring.api.entity.ClassificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassificationRepository extends JpaRepository<ClassificationEntity, Integer> {

    @Query("SELECT COUNT(c) FROM ClassificationEntity c")
    Long countTotalReports();

    @Query("SELECT COUNT(DISTINCT c.location) FROM ClassificationEntity c")
    Long countDistinctLocations();

    @Query("SELECT c.location, COUNT(r.id) " +
            "FROM ClassificationEntity c JOIN c.residues r " +
            "GROUP BY c.location " +
            "ORDER BY COUNT(r.id) DESC")
    java.util.List<Object[]> findCriticalArea();

    @Query("SELECT c FROM ClassificationEntity c")
    List<ClassificationEntity> findAllClassifications();

}

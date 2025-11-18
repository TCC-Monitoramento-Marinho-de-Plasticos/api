package com.example.marine.monitoring.api.repository;

import com.example.marine.monitoring.api.entity.ClassificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ClassificationRepository extends JpaRepository<ClassificationEntity, Integer> {

    @Query("SELECT COUNT(c) FROM ClassificationEntity c")
    Long countTotalReports();

    @Query("SELECT COUNT(DISTINCT c.location) FROM ClassificationEntity c")
    Long countDistinctLocations();

    @Query("""
           SELECT c.location, COUNT(c)
           FROM ClassificationEntity c
           WHERE c.hasResidue = 1
           GROUP BY c.location
           ORDER BY COUNT(c) DESC
    """)
    List<Object[]> findCriticalArea();

    @Query("SELECT COUNT(c) FROM ClassificationEntity c WHERE c.hasResidue = 1")
    Long countResidueReports();

    @Query("""
           SELECT c.location, COUNT(c)
           FROM ClassificationEntity c
           WHERE c.hasResidue = 0
           GROUP BY c.location
           ORDER BY COUNT(c) DESC
    """)
    List<Object[]> findCleanestArea();

    @Query("""
        SELECT c.classifiedAt, COUNT(c.id)
        FROM ClassificationEntity c
        WHERE c.classifiedAt >= :startDate
        GROUP BY c.classifiedAt
        ORDER BY c.classifiedAt
    """)
    List<Object[]> findLast7DaysTrend(@Param("startDate") LocalDate startDate);


    // ------------------  OPÇÃO 3 ------------------
    @Query("""
           SELECT COUNT(c)
           FROM ClassificationEntity c
           WHERE c.classifiedAt >= :startDate
    """)
    Long countFromDate(@Param("startDate") LocalDate startDate);

    @Query("""
           SELECT COUNT(c)
           FROM ClassificationEntity c
           WHERE c.classifiedAt BETWEEN :startDate AND :endDate
    """)
    Long countBetweenDates(@Param("startDate") LocalDate startDate,
                           @Param("endDate") LocalDate endDate);
}
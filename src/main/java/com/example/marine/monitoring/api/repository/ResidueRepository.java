package com.example.marine.monitoring.api.repository;

import com.example.marine.monitoring.api.entity.ResidueEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResidueRepository extends JpaRepository<ResidueEntity, Integer> {

    @Query("SELECT r.type, SUM(r.quantity) " +
            "FROM ResidueEntity r " +
            "GROUP BY r.type " +
            "ORDER BY SUM(r.quantity) DESC")
    List<Object[]> findMostCommonResidue();
}

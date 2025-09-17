package com.example.marine.monitoring.api.repository;

import com.example.marine.monitoring.api.entity.ResidueEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResidueRepository extends JpaRepository<ResidueEntity, Integer> {
}

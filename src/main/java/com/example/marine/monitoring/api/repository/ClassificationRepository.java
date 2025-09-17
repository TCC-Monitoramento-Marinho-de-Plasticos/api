package com.example.marine.monitoring.api.repository;

import com.example.marine.monitoring.api.entity.ClassificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassificationRepository extends JpaRepository<ClassificationEntity, Integer> {
}

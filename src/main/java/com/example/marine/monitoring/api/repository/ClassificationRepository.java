package com.example.marine.monitoring.api.repository;

import com.example.marine.monitoring.api.entity.ClassificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassificationRepository extends JpaRepository<ClassificationEntity, Integer> {
}

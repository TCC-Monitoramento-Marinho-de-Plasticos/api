package com.example.marine.monitoring.api.repository;

import com.example.marine.monitoring.api.entity.ClassificationEntity;
import com.example.marine.monitoring.api.entity.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<ImageEntity, Integer> {
}

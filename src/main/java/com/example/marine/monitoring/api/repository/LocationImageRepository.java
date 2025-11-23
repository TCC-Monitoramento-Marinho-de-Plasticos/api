package com.example.marine.monitoring.api.repository;

import com.example.marine.monitoring.api.entity.ClassificationEntity;
import com.example.marine.monitoring.api.entity.LocationImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LocationImageRepository extends JpaRepository<LocationImageEntity, Long> {

    List<LocationImageEntity> findByLocation(String location);
}
package com.example.marine.monitoring.api.dto;

import java.math.BigDecimal;
import java.util.Map;

public record LocationDto(
        String location,
        BigDecimal lat,
        BigDecimal lon,
        int totalReports,
        String mostCommonType,
        Map<String, Integer> typeDistribution
) {
}

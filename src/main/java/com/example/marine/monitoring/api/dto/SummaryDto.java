package com.example.marine.monitoring.api.dto;

import java.util.Map;

public record SummaryDto(
        Long totalReports,
        Long totalLocations,
        String criticalArea,
        Long reportsInCriticalArea,
        Double residueRate,
        String cleanestArea,
        Long cleanestAreaReports,
        Map<String, Long> trendMap,
        Double changeRate,
        Long totalCleanReports,
        Long totalDirtyReports
) {}

package com.example.marine.monitoring.api.dto;

import java.util.Map;

public record SummaryDto(
        Long totalReports,
        Long totalLocations,
        String criticalArea,
        Long reportsInCriticalArea,
        Double residueRate,          // % de relatos com lixo (hasResidue = 1)
        String cleanestArea,
        Long cleanestAreaReports,
        Map<String, Long> trendMap,
        Double changeRate,           // variação total de reports
        Long totalCleanReports,
        Long totalDirtyReports,
        Double dirtyChangeRate       // <-- NOVO: variação de relatos COM lixo
) {}

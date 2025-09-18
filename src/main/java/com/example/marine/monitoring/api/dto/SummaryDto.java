package com.example.marine.monitoring.api.dto;

public record SummaryDto(
        Long totalReports,
        Long totalLocations,
        String criticalArea,
        Long reportsInCriticalArea,
        String mostCommonResidueType,
        Long mostCommonResidueQuantity
) {
}

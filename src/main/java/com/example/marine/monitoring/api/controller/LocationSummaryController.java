package com.example.marine.monitoring.api.controller;

import com.example.marine.monitoring.api.entity.LocationImageEntity;
import com.example.marine.monitoring.api.repository.LocationImageRepository;
import com.example.marine.monitoring.api.service.ImageStorageService;
import com.example.marine.monitoring.api.service.LocationSummaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/location-summary")
@CrossOrigin(
        origins = "*",
        allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS, RequestMethod.HEAD, RequestMethod.PATCH},
        maxAge = 3600
)
public class LocationSummaryController {

    private final LocationSummaryService summaryService;

    public LocationSummaryController(LocationSummaryService summaryService) {
        this.summaryService = summaryService;
    }

    @GetMapping("/{location}")
    public ResponseEntity<?> getSummary(@PathVariable String location) {
        return ResponseEntity.ok(summaryService.getSummary(location));
    }
}

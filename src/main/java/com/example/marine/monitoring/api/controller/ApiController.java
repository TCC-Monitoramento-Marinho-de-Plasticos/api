package com.example.marine.monitoring.api.controller;

import com.example.marine.monitoring.api.service.PredictService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
public class ApiController {

    private final PredictService predictService;

    public ApiController(PredictService predictService) {
        this.predictService = predictService;
    }

    @PostMapping(value = "/predict", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> predict(
            @RequestParam("file") MultipartFile file,
            @RequestParam("localization") String localization) {
        return predictService.predictFromFile(file, localization);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("API is running");
    }
}

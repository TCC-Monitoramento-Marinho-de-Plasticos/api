package com.example.marine.monitoring.api.controller;

import com.example.marine.monitoring.api.service.PredictService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class ApiController {

    private final PredictService predictService;

    public ApiController(PredictService predictService) {
        this.predictService = predictService;
    }

    @PostMapping(value = "/predict", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> predict(@RequestParam("file") MultipartFile file) {
        return predictService.predictFromFile(file);
    }
}

package com.example.marine.monitoring.api.controller;

import com.example.marine.monitoring.api.service.PredictService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final PredictService predictService;

    public ApiController(PredictService predictService) {
        this.predictService = predictService;
    }

    @PostMapping(value = "/predict", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> predict(@RequestParam("file") MultipartFile file) {
        return predictService.predictFromFile(file);
    }
}

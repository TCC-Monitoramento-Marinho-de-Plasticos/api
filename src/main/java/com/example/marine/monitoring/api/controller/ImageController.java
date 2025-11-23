package com.example.marine.monitoring.api.controller;

import com.example.marine.monitoring.api.entity.LocationImageEntity;
import com.example.marine.monitoring.api.repository.ClassificationRepository;
import com.example.marine.monitoring.api.repository.LocationImageRepository;
import com.example.marine.monitoring.api.service.ImageStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/images")
@CrossOrigin(
        origins = "*",
        allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS, RequestMethod.HEAD, RequestMethod.PATCH},
        maxAge = 3600
)
public class ImageController {

    private final ImageStorageService storageService;
    private final LocationImageRepository imageRepository;

    public ImageController(ImageStorageService storageService, LocationImageRepository imageRepository) {
        this.storageService = storageService;
        this.imageRepository = imageRepository;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> upload(
            @RequestParam("location") String location,
            @RequestParam("file") MultipartFile file) {

        String url = storageService.uploadImage(location, file);

        LocationImageEntity entity = new LocationImageEntity();
        entity.setLocation(location);
        entity.setImageUrl(url);
        imageRepository.save(entity);

        return ResponseEntity.ok(url);
    }

    @GetMapping("/{location}")
    public List<String> listImages(@PathVariable String location) {
        return imageRepository.findByLocation(location)
                .stream()
                .map(LocationImageEntity::getImageUrl)
                .toList();
    }
}

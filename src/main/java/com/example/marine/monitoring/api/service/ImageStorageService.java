package com.example.marine.monitoring.api.service;

import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
public class ImageStorageService {
    public String uploadImage(String location, MultipartFile file) {
        try {
            String fileName =
                    "locations/" + location + "/" +
                            UUID.randomUUID() + "-" + file.getOriginalFilename();

            Bucket bucket = StorageClient.getInstance().bucket();
            bucket.create(fileName, file.getBytes(), file.getContentType());

            return "https://firebasestorage.googleapis.com/v0/b/"
                    + bucket.getName()
                    + "/o/"
                    + URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                    + "?alt=media";

        } catch (Exception e) {
            throw new RuntimeException("Erro ao fazer upload da imagem", e);
        }
    }
}

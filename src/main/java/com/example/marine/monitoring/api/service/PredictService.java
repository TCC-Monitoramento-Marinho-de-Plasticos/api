package com.example.marine.monitoring.api.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;

@Service
public class PredictService {
    private static final String SCRIPT_PATH = "/app/knn/predict_with_image_output.py";
    private static final String MODEL_PATH = "/app/knn/knn_model.pkl";

    public ResponseEntity<byte[]> predictFromFile(MultipartFile file) {
        try {
            File tempImage = File.createTempFile("input", ".jpg");
            file.transferTo(tempImage);

            ProcessBuilder pb = new ProcessBuilder("python3", SCRIPT_PATH, tempImage.getAbsolutePath(), MODEL_PATH);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("Python output: " + line);
                output.append(line).append("\n");
            }

            int exitCode = process.waitFor();
            tempImage.delete();

            if (exitCode != 0) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

            String outputImagePath = output.toString().trim();
            File outputImage = new File(outputImagePath);

            if (!outputImage.exists()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

            byte[] imageBytes = Files.readAllBytes(outputImage.toPath());
            outputImage.delete();

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(imageBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

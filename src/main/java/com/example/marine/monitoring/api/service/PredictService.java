package com.example.marine.monitoring.api.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

@Service
public class PredictService {
    private static final String SCRIPT_PATH = "/app/modelo/predict_with_image_output.py";
    private static final String MODEL_PATH = "/app/modelo/svm_model.pkl";

    public ResponseEntity<String> predictFromFile(MultipartFile file) {
        try {
            File tempImage = File.createTempFile("input", ".jpg");
            file.transferTo(tempImage);

            ProcessBuilder pb = new ProcessBuilder("python3", SCRIPT_PATH, tempImage.getAbsolutePath(), MODEL_PATH);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String outputLine = reader.readLine();
            System.out.println("Python output: " + outputLine);

            int exitCode = process.waitFor();
            tempImage.delete();

            if (exitCode != 0 || outputLine == null || !outputLine.contains("|")) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao processar imagem");
            }

            String[] parts = outputLine.split("\\|");
            String predictionLabel = parts[1];

            return ResponseEntity.ok(predictionLabel);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno");
        }
    }
}

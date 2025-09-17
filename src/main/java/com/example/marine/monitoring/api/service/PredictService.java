package com.example.marine.monitoring.api.service;

import com.example.marine.monitoring.api.client.GeocodingClient;
import com.example.marine.monitoring.api.entity.ClassificationEntity;
import com.example.marine.monitoring.api.entity.ImageEntity;
import com.example.marine.monitoring.api.entity.ResidueEntity;
import com.example.marine.monitoring.api.repository.ClassificationRepository;
import com.example.marine.monitoring.api.repository.ImageRepository;
import com.example.marine.monitoring.api.repository.ResidueRepository;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class PredictService {
    private static final String SCRIPT_PATH = "/app/modelo/predict_with_image_output.py";
    private static final String MODEL_PATH = "/app/modelo/svm_model.pkl";

    private final ClassificationRepository classificationRepository;
    private final ImageRepository imageRepository;
    private final ResidueRepository residueRepository;
    private final GeocodingClient geocodingClient;

    public PredictService(ClassificationRepository classificationRepository,
                          ImageRepository imageRepository,
                          ResidueRepository residueRepository,
                          GeocodingClient geocodingClient) {
        this.classificationRepository = classificationRepository;
        this.imageRepository = imageRepository;
        this.residueRepository = residueRepository;
        this.geocodingClient = geocodingClient;
    }

    public ResponseEntity<String> predictFromFile(MultipartFile file, String localization) {
        File tempImage = null;
        try {
            tempImage = saveTempImage(file);
            String outputLine = runPythonScript(tempImage);

            if (outputLine == null || !outputLine.contains("|")) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Erro ao processar imagem");
            }

            String predictionLabel = parsePrediction(outputLine);

            // salvar imagem
            ImageEntity imageEntity = new ImageEntity();
            imageEntity.setFileName("exemplo.jpg");
            imageEntity.setFilePath("/path/to/file");
            imageEntity.setCapturedAt(LocalDate.now());
//            imageEntity.setFileName(file.getOriginalFilename());
//            imageEntity.setFilePath(tempImage.getAbsolutePath());
//            imageEntity.setCapturedAt(LocalDate.now());
//            imageRepository.save(imageEntity);

            // usar client para pegar coords
            Map<String, String> coords = geocodingClient.fetchCoordinates(localization);

            String label = predictionLabel.toLowerCase();
            Short hasResidue = switch (label) {
                case "com lixo" -> 1;
                case "sem lixo" -> 0;
                default -> null;
            };

            ClassificationEntity classification = new ClassificationEntity();
            classification.setImage(imageEntity);
            classification.setHasResidue(hasResidue);
            classification.setConfidence(0.95f);
            classification.setLocation(localization);
            classification.setLatitude(coords.get("lat") != null ? Double.valueOf(coords.get("lat")) : null);
            classification.setLongitude(coords.get("lon") != null ? Double.valueOf(coords.get("lon")) : null);
            classification.setClassifiedAt(LocalDate.now());
            classificationRepository.save(classification);

//            ResidueEntity residue = new ResidueEntity(null, classification, "Garrafas PET", 3);
//            residueRepository.save(residue);

            return ResponseEntity.ok("Classificação e resíduos salvos: " + predictionLabel);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno");
        } finally {
            if (tempImage != null && tempImage.exists()) {
                tempImage.delete();
            }
        }
    }

    private File saveTempImage(MultipartFile file) throws IOException {
        File tempImage = File.createTempFile("input", ".jpg");
        file.transferTo(tempImage);
        return tempImage;
    }

    private String runPythonScript(File image) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("python3", SCRIPT_PATH, image.getAbsolutePath(), MODEL_PATH);
        pb.redirectErrorStream(true);

        Process process = pb.start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String outputLine = reader.readLine();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                return null;
            }
            return outputLine;
        }
    }

    private String parsePrediction(String outputLine) {
        String[] parts = outputLine.split("\\|");
        return parts.length > 1 ? parts[1] : "Desconhecido";
    }

}

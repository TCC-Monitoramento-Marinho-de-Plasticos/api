package com.example.marine.monitoring.api.service;

import com.example.marine.monitoring.api.client.GeocodingClient;
import com.example.marine.monitoring.api.dto.LocationDto;
import com.example.marine.monitoring.api.dto.SummaryDto;
import com.example.marine.monitoring.api.entity.ClassificationEntity;
import com.example.marine.monitoring.api.entity.ImageEntity;
import com.example.marine.monitoring.api.entity.ResidueEntity;
import com.example.marine.monitoring.api.repository.ClassificationRepository;
import com.example.marine.monitoring.api.repository.ImageRepository;
import com.example.marine.monitoring.api.repository.ResidueRepository;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PredictService {
    private static final String SCRIPT_PATH = "/app/modelo/predict_with_image_output_cnn.py";
    private static final String MODEL_PATH = "/app/modelo/modelo_cnn_imagens_de_fora_3_50epochs.keras";
    private static final String UPLOAD_DIR = "/tmp/uploads/";

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

        // Create upload directory if it doesn't exist
        new File(UPLOAD_DIR).mkdirs();
    }

    @Transactional
    public ResponseEntity<String> predictFromFile(MultipartFile file, String localization) {
        File tempImage = null;
        try {
            System.out.println("[LOG] Início do predictFromFile");

            // Validate input
            if (file.isEmpty()) {
                System.out.println("[LOG] Arquivo vazio");
                return ResponseEntity.badRequest().body("Arquivo não pode estar vazio");
            }

            if (localization == null || localization.trim().isEmpty()) {
                System.out.println("[LOG] Localização não informada");
                return ResponseEntity.badRequest().body("Localização é obrigatória");
            }

            tempImage = saveTempImage(file);
            System.out.println("[LOG] Arquivo temporário salvo: " + tempImage.getAbsolutePath());

            String outputLine = runPythonScript(tempImage);
            System.out.println("[LOG] Saída do Python: " + outputLine);

            if (outputLine == null || !outputLine.contains("|")) {
                System.out.println("[LOG] Saída do Python inválida");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Erro ao processar imagem");
            }

            String predictionLabel = parsePrediction(outputLine);
            System.out.println("[LOG] Predição extraída: " + predictionLabel);

            // Save image entity first
            ImageEntity imageEntity = new ImageEntity();
            imageEntity.setFileName(file.getOriginalFilename() != null ? file.getOriginalFilename() : "unknown.jpg");
            imageEntity.setFilePath(tempImage.getAbsolutePath());
            imageEntity.setCapturedAt(LocalDate.now());
            imageEntity = imageRepository.save(imageEntity);
            System.out.println("[LOG] Imagem salva com ID: " + imageEntity.getId());

            // Get coordinates using geocoding client
            Map<String, String> coords = geocodingClient.fetchCoordinates(localization);
            System.out.println("[LOG] Coordenadas recebidas: " + coords);

            // Determine if there's residue
            String label = predictionLabel.toLowerCase();
            Short hasResidue = switch (label) {
                case "com lixo" -> 1;
                case "sem lixo" -> 0;
                default -> null;
            };
            System.out.println("[LOG] hasResidue: " + hasResidue);

            // Create and save classification
            ClassificationEntity classification = new ClassificationEntity();
            classification.setImage(imageEntity);
            classification.setHasResidue(hasResidue);
            classification.setConfidence(0.95f);
            classification.setLocation(localization);

            if (coords.get("lat") != null && coords.get("lon") != null) {
                try {
                    classification.setLatitude(new BigDecimal(coords.get("lat")));
                    classification.setLongitude(new BigDecimal(coords.get("lon")));
                } catch (NumberFormatException e) {
                    System.err.println("[LOG] Erro ao converter coordenadas: " + e.getMessage());
                }
            }

            classification.setClassifiedAt(LocalDate.now());
            classification = classificationRepository.save(classification);
            System.out.println("[LOG] Classificação salva com ID: " + classification.getId());

            if (hasResidue != null && hasResidue == 1) {
                ResidueEntity residue = new ResidueEntity(classification, "Resíduo detectado", 1);
                residueRepository.save(residue);
                System.out.println("[LOG] Residue salvo para classificação ID: " + classification.getId());
            }

            String responseMessage = String.format(
                    "Classificação salva com sucesso!\nPredição: %s\nLocalização: %s\nCoordenadas: %s, %s",
                    predictionLabel,
                    localization,
                    coords.get("lat") != null ? coords.get("lat") : "N/A",
                    coords.get("lon") != null ? coords.get("lon") : "N/A"
            );

            System.out.println("[LOG] predictFromFile finalizado com sucesso");
            return ResponseEntity.ok(responseMessage);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("[LOG] Erro interno: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno: " + e.getMessage());
        } finally {
            if (tempImage != null && tempImage.exists()) {
                tempImage.delete();
                System.out.println("[LOG] Arquivo temporário deletado: " + tempImage.getAbsolutePath());
            }
        }
    }

    private File saveTempImage(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID().toString() + "_" +
                (file.getOriginalFilename() != null ? file.getOriginalFilename() : "image.jpg");
        File tempImage = new File(UPLOAD_DIR + fileName);
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
                System.err.println("Python script failed with exit code: " + exitCode);
                return null;
            }
            return outputLine;
        }
    }

    private String parsePrediction(String outputLine) {
        String[] parts = outputLine.split("\\|");
        return parts.length > 1 ? parts[1] : "Desconhecido";
    }

    public SummaryDto getSummary() {
        // Total reports e localizações
        Long totalReports = classificationRepository.countTotalReports();
        Long totalLocations = classificationRepository.countDistinctLocations();

        // Área crítica
        List<Object[]> criticalAreaList = classificationRepository.findCriticalArea();
        String criticalArea = null;
        Long reportsInCriticalArea = 0L;
        if (!criticalAreaList.isEmpty()) {
            Object[] first = criticalAreaList.get(0);
            criticalArea = (String) first[0];
            reportsInCriticalArea = (Long) first[1];
        }

        // Tipo de resíduo mais comum
        List<Object[]> mostCommonResidueList = residueRepository.findMostCommonResidue();
        String mostCommonResidueType = null;
        Long mostCommonResidueQuantity = 0L;
        if (!mostCommonResidueList.isEmpty()) {
            Object[] first = mostCommonResidueList.get(0);
            mostCommonResidueType = (String) first[0];
            mostCommonResidueQuantity = (Long) first[1];
        }

        return new SummaryDto(totalReports, totalLocations, criticalArea, reportsInCriticalArea,
                mostCommonResidueType, mostCommonResidueQuantity);
    }

    public List<LocationDto> getAllLocations() {
        List<ClassificationEntity> classifications = classificationRepository.findAll();

        // Agrupar por localização
        Map<String, List<ClassificationEntity>> grouped = classifications.stream()
                .collect(Collectors.groupingBy(ClassificationEntity::getLocation));

        List<LocationDto> result = new ArrayList<>();

        for (Map.Entry<String, List<ClassificationEntity>> entry : grouped.entrySet()) {
            String location = entry.getKey();
            List<ClassificationEntity> locationClassifications = entry.getValue();

            // Contar os tipos de resíduos
            Map<String, Integer> typeDistribution = new HashMap<>();
            for (ClassificationEntity c : locationClassifications) {
                for (ResidueEntity r : c.getResidues()) {
                    typeDistribution.put(r.getType(), typeDistribution.getOrDefault(r.getType(), 0) + 1);
                }
            }

            // Tipo mais comum
            String mostCommonType = typeDistribution.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("");

            ClassificationEntity first = locationClassifications.get(0);

            result.add(new LocationDto(
                    location,
                    first.getLatitude(),
                    first.getLongitude(),
                    typeDistribution.values().stream().mapToInt(Integer::intValue).sum(),
                    mostCommonType,
                    typeDistribution
            ));
        }

        return result;
    }
}

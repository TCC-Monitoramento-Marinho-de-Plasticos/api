package com.example.marine.monitoring.api.service;

import com.example.marine.monitoring.api.repository.ClassificationRepository;
import com.example.marine.monitoring.api.repository.LocationImageRepository;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class LocationSummaryService {
    private final ClassificationRepository classificationRepository;
    private final LocationImageRepository imageRepository;

    public LocationSummaryService(
            ClassificationRepository classificationRepository,
            LocationImageRepository imageRepository) {

        this.classificationRepository = classificationRepository;
        this.imageRepository = imageRepository;
    }

    public Map<String, Object> getSummary(String location) {

        long comLixo = classificationRepository.countByLocationAndHasResidue(location, (short) 1);
        long semLixo = classificationRepository.countByLocationAndHasResidue(location, (short) 0);

        // imagens cadastradas no Firebase
        List<String> imagens = imageRepository.findByLocation(location)
                .stream().map(i -> i.getImageUrl()).toList();

        String selecionada = imagens.isEmpty() ? null :
                (comLixo > semLixo ? imagens.get(0) : imagens.get(imagens.size() - 1));

        Map<String, Object> result = new HashMap<>();
        result.put("location", location);
        result.put("withResidue", comLixo);
        result.put("withoutResidue", semLixo);
        result.put("selectedImage", selecionada);
        result.put("allImages", imagens);

        return result;
    }
}

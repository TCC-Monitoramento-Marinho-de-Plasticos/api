package com.example.marine.monitoring.api.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Component
public class GeocodingClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public GeocodingClient() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public Map<String, String> fetchCoordinates(String location) {
        try {
            String url = "https://nominatim.openstreetmap.org/search?format=json&q=" + location + "&limit=1";

            System.out.println("[LOG] URL de requisição: " + url);

            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "marine-monitoring-app/1.0");
            headers.set("Accept", "application/json");

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    String.class
            );

            System.out.println("[LOG] Status HTTP: " + response.getStatusCode());
            System.out.println("[LOG] Body da resposta: " + response.getBody());

            // Desserializa manualmente
            List<Map<String, Object>> results = objectMapper.readValue(
                    response.getBody(),
                    new TypeReference<List<Map<String, Object>>>() {}
            );

            if (!results.isEmpty()) {
                Map<String, Object> first = results.get(0);
                System.out.println("[LOG] Primeira entrada do resultado: " + first);
                return Map.of(
                        "lat", first.get("lat").toString(),
                        "lon", first.get("lon").toString()
                );
            } else {
                System.out.println("[LOG] Nenhuma coordenada encontrada para: " + location);
            }

        } catch (Exception e) {
            System.err.println("[ERRO] Exceção ao buscar coordenadas para: " + location);
            e.printStackTrace();
        }

        return Map.of();
    }
}

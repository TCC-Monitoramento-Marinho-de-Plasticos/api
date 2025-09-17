package com.example.marine.monitoring.api.client;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class GeocodingClient {
    private final RestTemplate restTemplate;

    public GeocodingClient() {
        this.restTemplate = new RestTemplate();
    }

    public Map<String, String> fetchCoordinates(String location) {
        String url = "https://nominatim.openstreetmap.org/search?format=json&q=" + location;

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "marine-monitoring-app");

        ResponseEntity<Map[]> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Map[].class
        );

        if (response.getBody() != null && response.getBody().length > 0) {
            Map first = response.getBody()[0];
            return Map.of(
                    "lat", first.get("lat").toString(),
                    "lon", first.get("lon").toString()
            );
        }
        return Map.of();
    }
}

package com.example.marine.monitoring.api.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class LocationImageEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String location; // “Rio de Janeiro”, “Recife”, etc
    private String imageUrl;

    // Constructors
    public LocationImageEntity() {}

    public LocationImageEntity(Long id, String location, String imageUrl) {
        this.id = id;
        this.location = location;
        this.imageUrl = imageUrl;
    }

    // --- Getters ---
    public Long getId() {
        return id;
    }

    public String getLocation() {
        return location;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    // --- Setters ---
    public void setId(Long id) {
        this.id = id;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

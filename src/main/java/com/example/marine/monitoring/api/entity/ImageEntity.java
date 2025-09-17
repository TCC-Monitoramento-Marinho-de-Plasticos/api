package com.example.marine.monitoring.api.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "images")
public class ImageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "captured_at", nullable = false)
    private LocalDate capturedAt;

    @OneToMany(mappedBy = "image", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ClassificationEntity> classifications = new ArrayList<>();

    // Constructors
    public ImageEntity() {}

    public ImageEntity(String fileName, String filePath, LocalDate capturedAt) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.capturedAt = capturedAt;
    }

    // --- Getters ---
    public Integer getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public LocalDate getCapturedAt() {
        return capturedAt;
    }

    public List<ClassificationEntity> getClassifications() {
        return classifications;
    }

    // --- Setters ---
    public void setId(Integer id) {
        this.id = id;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setCapturedAt(LocalDate capturedAt) {
        this.capturedAt = capturedAt;
    }

    public void setClassifications(List<ClassificationEntity> classifications) {
        this.classifications = classifications;
    }
}

package com.example.marine.monitoring.api.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "classifications")
public class ClassificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "image_id", nullable = false)
    private ImageEntity image;

    @Column(name = "has_residue")
    private Short hasResidue;

    private Float confidence;

    @Column(precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(precision = 11, scale = 8)
    private BigDecimal longitude;

    @Column(length = 100)
    private String location;

    @Column(name = "classified_at", nullable = false)
    private LocalDate classifiedAt;

    @OneToMany(mappedBy = "classification", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ResidueEntity> residues = new ArrayList<>();

    // Constructors
    public ClassificationEntity() {}

    public ClassificationEntity(ImageEntity image, Short hasResidue, Float confidence,
                                BigDecimal latitude, BigDecimal longitude, String location, LocalDate classifiedAt) {
        this.image = image;
        this.hasResidue = hasResidue;
        this.confidence = confidence;
        this.latitude = latitude;
        this.longitude = longitude;
        this.location = location;
        this.classifiedAt = classifiedAt;
    }

    // --- Getters ---
    public Integer getId() { return id; }
    public ImageEntity getImage() { return image; }
    public Short getHasResidue() { return hasResidue; }
    public Float getConfidence() { return confidence; }
    public BigDecimal getLatitude() { return latitude; }
    public BigDecimal getLongitude() { return longitude; }
    public String getLocation() { return location; }
    public LocalDate getClassifiedAt() { return classifiedAt; }
    public List<ResidueEntity> getResidues() { return residues; }

    // --- Setters ---
    public void setId(Integer id) { this.id = id; }
    public void setImage(ImageEntity image) { this.image = image; }
    public void setHasResidue(Short hasResidue) { this.hasResidue = hasResidue; }
    public void setConfidence(Float confidence) { this.confidence = confidence; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }
    public void setLocation(String location) { this.location = location; }
    public void setClassifiedAt(LocalDate classifiedAt) { this.classifiedAt = classifiedAt; }
    public void setResidues(List<ResidueEntity> residues) { this.residues = residues; }
}
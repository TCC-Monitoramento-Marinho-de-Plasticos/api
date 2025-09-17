package com.example.marine.monitoring.api.entity;

import com.example.marine.monitoring.api.entity.ImageEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "classifications")
@NoArgsConstructor
@AllArgsConstructor
public class ClassificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "image_id")
    private ImageEntity image;

    @Column(name = "has_residue")
    private Short hasResidue;

    private Float confidence;

    private Double latitude;
    private Double longitude;

    private String location;

    @Column(name = "classified_at", nullable = false)
    private LocalDate classifiedAt;

    @OneToMany(mappedBy = "classification", cascade = CascadeType.ALL)
    private List<ResidueEntity> residues;

    public Integer getId() { return id; }
    public ImageEntity getImage() { return image; }
    public Short getHasResidue() { return hasResidue; }
    public Float getConfidence() { return confidence; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public String getLocation() { return location; }
    public LocalDate getClassifiedAt() { return classifiedAt; }
    public List<ResidueEntity> getResidues() { return residues; }

    // --- Setters ---
    public void setId(Integer id) { this.id = id; }
    public void setImage(ImageEntity image) { this.image = image; }
    public void setHasResidue(Short hasResidue) { this.hasResidue = hasResidue; }
    public void setConfidence(Float confidence) { this.confidence = confidence; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public void setLocation(String location) { this.location = location; }
    public void setClassifiedAt(LocalDate classifiedAt) { this.classifiedAt = classifiedAt; }
    public void setResidues(List<ResidueEntity> residues) { this.residues = residues; }
}


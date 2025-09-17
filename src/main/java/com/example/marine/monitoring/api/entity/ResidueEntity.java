package com.example.marine.monitoring.api.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "residues")
public class ResidueEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "classification_id", nullable = false)
    private ClassificationEntity classification;

    @Column(nullable = false, length = 100)
    private String type;

    @Column(nullable = false)
    private Integer quantity;

    // Constructors
    public ResidueEntity() {}

    public ResidueEntity(ClassificationEntity classification, String type, Integer quantity) {
        this.classification = classification;
        this.type = type;
        this.quantity = quantity;
    }

    // Getters
    public Integer getId() { return id; }
    public ClassificationEntity getClassification() { return classification; }
    public String getType() { return type; }
    public Integer getQuantity() { return quantity; }

    // Setters
    public void setId(Integer id) { this.id = id; }
    public void setClassification(ClassificationEntity classification) { this.classification = classification; }
    public void setType(String type) { this.type = type; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}

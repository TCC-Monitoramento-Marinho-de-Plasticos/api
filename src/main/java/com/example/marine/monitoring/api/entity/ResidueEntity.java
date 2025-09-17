package com.example.marine.monitoring.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "residues")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResidueEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "classification_id", nullable = false)
    private ClassificationEntity classification;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private Integer quantity;
}

// src/main/java/com/matcheval/stage/model/CheckedMatch.java
package com.matcheval.stage.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
public class CheckedMatch {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private OffreEmploi offre;

    @ManyToOne(optional = false)
    private Candidature candidature;

    // recruteur qui a coché
    @ManyToOne(optional = false)
    private Users recruteur;

    // manager du recruteur (nullable si tu préfères le déduire à la volée)
    @ManyToOne
    private Users manager;

    private Double scoreOverall;           // snapshot du score
    private String filenameSnapshot;       // nom fichier si utile
    private LocalDateTime createdAt = LocalDateTime.now();
}

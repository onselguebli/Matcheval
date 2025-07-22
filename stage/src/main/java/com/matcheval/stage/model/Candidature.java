package com.matcheval.stage.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Candidature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dateSoumission;
    private String statut;
    private String commentaire;

    private String candidatNom;
    private String candidatPrenom;
    private String candidatEmail;

    @ManyToOne
    private OffreEmploi offre;
    @OneToOne(mappedBy = "candidature", cascade = CascadeType.ALL)
    private CV cv;

}

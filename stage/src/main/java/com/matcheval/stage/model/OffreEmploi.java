package com.matcheval.stage.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;
@Data
@Entity
public class OffreEmploi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;
    private String description;
    private String exigences;
    private Date datePublication;
    private Date dateExpiration;
    private String statut;
    private String localisation;

    @ManyToOne
    private Users recruteur;

    @OneToMany(mappedBy = "offre", cascade = CascadeType.ALL)
    private List<Candidature> candidatures;

    @OneToMany(mappedBy = "offre")
    private List<OffreSiteExterne> offresExternes;
}

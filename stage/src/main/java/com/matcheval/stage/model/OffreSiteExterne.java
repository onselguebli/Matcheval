package com.matcheval.stage.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@Entity
public class OffreSiteExterne {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dateDiffusion;
    private String statutDiffusion;

    @ManyToOne
    private OffreEmploi offre;

    @ManyToOne
    private SiteExterne siteExterne;
}

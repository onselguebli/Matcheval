package com.matcheval.stage.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class CV {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private  String file;
    private String contenuTexte;
    private String format;
    private Date dateUpload;
    @OneToOne
    @JoinColumn(name = "candidature_id")
    private Candidature candidature;
}

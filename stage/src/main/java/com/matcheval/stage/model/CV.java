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

    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "bytea")
    private byte[] data;


    private String originalFilename;
    private String contentType;
    private Long size;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateUpload;

    private String contenuTexte;

    @OneToOne
    @JoinColumn(name = "candidature_id", unique = true)
    private Candidature candidature;
}

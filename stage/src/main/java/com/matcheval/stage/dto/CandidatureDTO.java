package com.matcheval.stage.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CandidatureDTO {
    //afficher candidature
    private Long id;
    private LocalDateTime dateSoumission;
    private String statut;
    private String commentaire;
    private String candidatNom;
    private String candidatPrenom;
    private String candidatEmail;
    private String titreOffre;

    private String cvFilename;
    private String cvContentType;
    private Long cvSize;
    private Boolean hasCv;
}

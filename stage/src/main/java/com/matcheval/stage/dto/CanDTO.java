package com.matcheval.stage.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CanDTO {
    private Long id;
    private LocalDateTime dateSoumission;
    private String statut;
    private String commentaire;
    private String candidatNom;
    private String candidatPrenom;
    private String candidatEmail;

    // Informations sur le CV
    private String cvOriginalFilename;
    private String cvContentType;
    private Long cvSize;
    private LocalDateTime cvDateUpload;
    private String cvContenuTexte;
    private Boolean hasCvText;

    // ID de l'offre
    private Long offreId;
    private String offreTitre;
}
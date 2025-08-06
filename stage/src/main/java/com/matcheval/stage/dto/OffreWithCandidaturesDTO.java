package com.matcheval.stage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class OffreWithCandidaturesDTO {
    //afficher les listes de candidatures des offres d'un recruteurs spécifique
    private Long id;
    private String titre;
    private List<CandidatureDTO> candidatures;

    @Data
    @AllArgsConstructor
    public static class CandidatureDTO {
        private Long id;
        private String candidatNom;
        private String candidatPrenom;
        private LocalDateTime dateSoumission;
    }
}

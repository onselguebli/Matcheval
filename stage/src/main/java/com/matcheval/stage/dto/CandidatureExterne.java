package com.matcheval.stage.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
//extraire les candidature externe
public class CandidatureExterne {
    private String nom;
    private String prenom;
    private String email;
    private String commentaire;
    private CVExterne cv;
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CVExterne {
        private String contenuTexte;
        private String format;
        private String dateUpload;
    }

}

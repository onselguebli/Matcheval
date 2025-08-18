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
        private String contenuTexte;  // si on veut aussi garder du texte extrait
        private String format;        // exemple: "PDF"
        private String dateUpload;    // "yyyy-MM-dd"

        // ✅ nouveaux champs pour fichier réel
        private String url;           // ex: "https://site.com/cv-123.pdf"
        private String base64;        // contenu encodé base64
        private String contentType;   // "application/pdf"
        private String filename;      // "cv-dupont.pdf"
    }

}

package com.matcheval.stage.dto;

import com.matcheval.stage.model.TypeOffre;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.Date;

@Data
public class OffreEmploiDTO {
    //Afficher offre
    private Long id;
    private String titre;
    private String description;
    private String exigences;
    private Date datePublication;
    private Date dateExpiration;
    private String statut;
    private String localisation;
    private String typeOffre;
    private String recruteurEmail;


}

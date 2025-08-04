package com.matcheval.stage.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
//affiche le nombre d'offre d'emploi par rapport au recruteur
public class RecruteurOffreStatDTO {
    private String recruteurEmail;
    private Long nombreOffres;

    public RecruteurOffreStatDTO(String recruteurEmail, Long nombreOffres) {
        this.recruteurEmail = recruteurEmail;
        this.nombreOffres = nombreOffres;
    }

}

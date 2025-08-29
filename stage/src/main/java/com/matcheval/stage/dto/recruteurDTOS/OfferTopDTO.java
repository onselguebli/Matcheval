package com.matcheval.stage.dto.recruteurDTOS;

import lombok.*;
import java.util.Date;

@Data @NoArgsConstructor @AllArgsConstructor
public class OfferTopDTO {
    private Long id;
    private String titre;
    private Date datePublication;
    private String statut;
    private long candidates;
}
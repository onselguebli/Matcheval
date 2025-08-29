package com.matcheval.stage.dto.managerDTOS;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OfferTopDTO {
    private Long id;
    private String titre;
    private Date datePublication;
    private String statut;
    private long candidates;
}

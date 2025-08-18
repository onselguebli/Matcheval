package com.matcheval.stage.dto;

import com.matcheval.stage.model.TypeOffre;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.file.FileStore;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SiteTypeCountDTO {
    //compter les candidatures par site et par type d’offre
    private String site;
    private TypeOffre typeOffre;
    private Long count;



}

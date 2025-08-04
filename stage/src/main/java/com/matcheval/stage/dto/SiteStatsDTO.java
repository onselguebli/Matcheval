package com.matcheval.stage.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
//nombre d'offre + candid par rapport à site externe
public class SiteStatsDTO {
    private Long idSite;
    private String nomSite;
    private Long nombreOffres;
    private Long nombreCandidatures;

}

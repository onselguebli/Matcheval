package com.matcheval.stage.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
//afficher le nb de cv,offre , recruteur chaque moi ,année,jour
public class TrafficDashboardDTO {
    private List<String> labels; // jours/mois/années
    private List<Integer> nbCVs;
    private List<Integer> nbOffres;
    private List<Integer> nbRecruteurs;
    private int totalCVs;
    private int totalOffres;
    private int totalRecruteurs;

    public TrafficDashboardDTO(List<String> labels, List<Long> nbCVs, List<Long> nbOffres, List<Long> nbRecruteurs, Long totalCVs, Long totalOffres, Long totalRecruteurs) {
        this.labels = labels;
        this.nbCVs = nbCVs.stream().map(Long::intValue).collect(Collectors.toList());
        this.nbOffres = nbOffres.stream().map(Long::intValue).collect(Collectors.toList());
        this.nbRecruteurs = nbRecruteurs.stream().map(Long::intValue).collect(Collectors.toList());
        this.totalCVs = totalCVs.intValue();
        this.totalOffres = totalOffres.intValue();
        this.totalRecruteurs = totalRecruteurs.intValue();
    }

}

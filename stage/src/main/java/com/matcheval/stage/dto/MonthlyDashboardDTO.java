package com.matcheval.stage.dto;

import lombok.Data;

import java.util.List;

@Data
public class MonthlyDashboardDTO {
    //afficher ses données pour chaque mois
    private List<String> months;
    private List<Integer> totalUsers;
    private List<Integer> totalCandidatures;
    private List<Integer> totalRecruteurs;
    private List<Integer> totalManagers;
    private List<Integer> totalSitesExternes;
}

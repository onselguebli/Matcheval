package com.matcheval.stage.interfaces;

import com.matcheval.stage.dto.*;
import com.matcheval.stage.model.OffreEmploi;
import com.matcheval.stage.model.Users;

import java.util.List;
import java.util.Map;

public interface IstatService {
    List<Users> findRecruteursByManagerId(Long managerId);
    Map<String, Long> countUsersByRole();

    Map<Integer, Long> countUsersByYear();
     Map<String, Long> countUsersByEnabledStatus();
     Map<String, Long> countUsersByCivility();
   Map<String, Long> countRecruteursPerManager();
     Map<String, Long> getDashboardStats();


    MonthlyDashboardDTO getMonthlyDashboardStats();
     TrafficDashboardDTO getTrafficStats(String period);
     long countActiveToday();
    long countCVsToday();
     List<RecruteurOffreStatDTO> getNombreOffresParRecruteur();
    List<SiteStatsDTO> getStatsParSite();


    Map<String, Long> getOffresCountByType();
    Map<String, Map<String, Long>> candidaturesParSiteEtType();
    Map<String, Map<String, Long>> candidaturesParSiteEtTypePourRecruteur(String recruteurEmail);
    Map<String, Map<String, Long>> toNestedMap(List<SiteTypeCountDTO> rows);

    Map<String, Long> candidaturesParTypeGlobal();
}

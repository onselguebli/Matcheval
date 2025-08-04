package com.matcheval.stage.interfaces;

import com.matcheval.stage.dto.MonthlyDashboardDTO;
import com.matcheval.stage.dto.RecruteurOffreStatDTO;
import com.matcheval.stage.dto.SiteStatsDTO;
import com.matcheval.stage.dto.TrafficDashboardDTO;
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



}

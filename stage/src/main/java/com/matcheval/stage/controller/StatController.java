package com.matcheval.stage.controller;

import com.matcheval.stage.dto.MonthlyDashboardDTO;
import com.matcheval.stage.dto.RecruteurOffreStatDTO;
import com.matcheval.stage.dto.SiteStatsDTO;
import com.matcheval.stage.dto.TrafficDashboardDTO;
import com.matcheval.stage.interfaces.IstatService;
import com.matcheval.stage.model.OffreEmploi;
import com.matcheval.stage.model.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("admin")

public class StatController {
    @Autowired
    IstatService statService;
    @GetMapping("/recruteurs-par-manager/{managerId}")
    public ResponseEntity<List<Users>> getRecruteursParManager(@PathVariable Long managerId) {
        List<Users> recruteurs = statService.findRecruteursByManagerId(managerId);
        return ResponseEntity.ok(recruteurs);
    }

    @GetMapping("/stats/users-by-role")
    public ResponseEntity<Map<String, Long>> getUserCountByRole() {
        Map<String, Long> stats = statService.countUsersByRole();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/stats/users-per-year")
    public ResponseEntity<Map<Integer, Long>> getUsersPerYear() {
        Map<Integer, Long> stats = statService.countUsersByYear();
        return ResponseEntity.ok(stats);
    }
    @GetMapping("/stats/users-by-status")
    public ResponseEntity<Map<String, Long>> getUsersByStatus() {
        Map<String, Long> stats = statService.countUsersByEnabledStatus();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/stats/users-by-civility")
    public ResponseEntity<Map<String, Long>> getUsersByCivility() {
        return ResponseEntity.ok(statService.countUsersByCivility());
    }
    @GetMapping("/stats/recruteurs-by-manager")
    public ResponseEntity<Map<String, Long>> getRecruteursByManager() {
        Map<String, Long> stats = statService.countRecruteursPerManager();
        return ResponseEntity.ok(stats);
    }
    @GetMapping("/stats/dashboard")
    public ResponseEntity<Map<String, Long>> getDashboardStats() {
        return ResponseEntity.ok(statService.getDashboardStats());
    }

    @GetMapping("stats/monthly")
    public MonthlyDashboardDTO getMonthlyDashboardStats() {
        return statService.getMonthlyDashboardStats();
    }

    @GetMapping("/stats/traffic")
    public TrafficDashboardDTO getTrafficStats(@RequestParam("period") String period) {
        return statService.getTrafficStats(period);
    }
    @GetMapping("/stats/active/today")
    public long getActiveToday() {
        return statService.countActiveToday();
    }

    @GetMapping("/stats/newCv/today")
    public long getNewCVsToday() {
        return statService.countCVsToday();
    }

    @GetMapping("/stats/offres-par-recruteur")
    public List<RecruteurOffreStatDTO> getOffresParRecruteur() {
        return statService.getNombreOffresParRecruteur();
    }

    @GetMapping("/stats/offre+candi-perSite")
    public ResponseEntity<List<SiteStatsDTO>> getStatsParSite() {
        return ResponseEntity.ok(statService.getStatsParSite());
    }
    @GetMapping("/stats/offres-by-type")
    public ResponseEntity<Map<String, Long>> getOffresByType() {
        return ResponseEntity.ok(statService.getOffresCountByType());
    }
    // Global
    @GetMapping("/stats/candidatures-par-site-type")
    public ResponseEntity<Map<String, Map<String, Long>>> candidaturesParSiteEtType() {
        return ResponseEntity.ok(statService.candidaturesParSiteEtType());
    }


    @GetMapping("/stats/candidatures-par-type")
    public ResponseEntity<Map<String, Long>> candidaturesParTypeGlobal() {
        return ResponseEntity.ok(statService.candidaturesParTypeGlobal());
    }

}

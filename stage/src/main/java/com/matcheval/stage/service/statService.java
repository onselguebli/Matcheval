package com.matcheval.stage.service;

import com.matcheval.stage.dto.*;
import com.matcheval.stage.interfaces.IstatService;
import com.matcheval.stage.model.*;
import com.matcheval.stage.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class statService implements IstatService {


    @Autowired
    private UserRepo userRepo;
    @Autowired
    private CandidatureRepo candidatureRepo;
    @Autowired
    private SiteExterneRepo siteExterneRepo;
    @Autowired
    private CvRepo cvRepo;
    @Autowired
    private OffreRepo offreRepo;
    @Autowired
    private OffreSiteExterneRepo offreSiteExterneRepo;




    public List<Users> findRecruteursByManagerId(Long managerId) {
        return userRepo.findByManagerId(managerId);  // Assure-toi que ce repo existe
    }

    @Override
    public Map<String, Long> countUsersByRole() {
        List<Object[]> results = userRepo.countUsersByRole();

        Map<String, Long> roleCounts = new HashMap<>();
        for (Object[] row : results) {
            String role = row[0].toString(); // u.role is an enum
            Long count = (Long) row[1];
            roleCounts.put(role, count);
        }

        return roleCounts;
    }

    @Override
    public Map<Integer, Long> countUsersByYear() {
        List<Object[]> results = userRepo.countUsersByYear();

        Map<Integer, Long> yearCounts = new HashMap<>();
        for (Object[] row : results) {
            Integer year = (Integer) row[0];
            Long count = (Long) row[1];
            yearCounts.put(year, count);
        }

        return yearCounts;
    }

    @Override
    public Map<String, Long> countUsersByEnabledStatus() {
        List<Object[]> results = userRepo.countUsersByEnabledStatus();

        Map<String, Long> statusCounts = new HashMap<>();
        for (Object[] row : results) {
            Boolean enabled = (Boolean) row[0];
            Long count = (Long) row[1];
            statusCounts.put(enabled ? "Activés" : "Désactivés", count);
        }

        return statusCounts;
    }

    @Override
    public Map<String, Long> countUsersByCivility() {
        List<Object[]> results = userRepo.countUsersByCivility();

        Map<String, Long> civilityCounts = new HashMap<>();
        for (Object[] row : results) {
            Civility civilityEnum = (Civility) row[0];
            Long count = (Long) row[1];

            String civilityKey = (civilityEnum != null) ? civilityEnum.name() : "Non spécifiée";
            civilityCounts.put(civilityKey, count);
        }

        return civilityCounts;
    }

    @Override
    public Map<String, Long> countRecruteursPerManager() {
        List<Object[]> results = userRepo.countRecruteursPerManager();
        Map<String, Long> managerCounts = new HashMap<>();
        for (Object[] row : results) {
            String managerName = row[0].toString();
            Long count = (Long) row[1];
            managerCounts.put(managerName, count);
        }
        return managerCounts;
    }

    @Override
    public Map<String, Long> getDashboardStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalUsers", userRepo.count());
        stats.put("totalCandidatures", candidatureRepo.count());
        stats.put("totalSitesExternes", siteExterneRepo.count());
        stats.put("totalRecruteurs", userRepo.countByRole(Roles.RECRUITER));
        stats.put("totalManagers", userRepo.countByRole(Roles.MANAGER));
        return stats;
    }

    @Override
    public MonthlyDashboardDTO getMonthlyDashboardStats() {
        MonthlyDashboardDTO dto = new MonthlyDashboardDTO();

        List<String> months = List.of("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
        dto.setMonths(months);

        dto.setTotalUsers(aggregateByMonth(userRepo.countUsersByMonth()));
        dto.setTotalRecruteurs(aggregateByMonth(userRepo.countUsersByRolePerMonth(Roles.RECRUITER)));
        dto.setTotalManagers(aggregateByMonth(userRepo.countUsersByRolePerMonth(Roles.MANAGER)));
        dto.setTotalCandidatures(aggregateByMonth(candidatureRepo.countCandidaturesByMonth()));
        dto.setTotalSitesExternes(aggregateByMonth(siteExterneRepo.countSitesByMonth()));

        return dto;
    }


    private List<Integer> aggregateByMonth(List<Object[]> rawData) {
        Map<Integer, Integer> map = new HashMap<>();
        for (Object[] row : rawData) {
            // Now row[0] is the month (from EXTRACT) and row[1] is the count
            int month = ((Number) row[0]).intValue(); // Month is now first element
            int count = ((Number) row[1]).intValue(); // Count is now second element
            map.put(month, count);
        }

        List<Integer> counts = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            counts.add(map.getOrDefault(i, 0));
        }
        return counts;
    }
    @Override
    public TrafficDashboardDTO getTrafficStats(String period) {
        List<Object[]> cvsRaw, offresRaw, recruteursRaw;
        List<String> labels;

        switch (period.toLowerCase()) {
            case "day":
                cvsRaw = cvRepo.countByDay();
                offresRaw = offreRepo.countByDay();
                recruteursRaw = userRepo.countRecruteursByDay();
                labels = getDaysLabels();
                break;
            case "year":
                cvsRaw = cvRepo.countByYear();
                offresRaw = offreRepo.countByYear();
                recruteursRaw = userRepo.countRecruteursByYear();
                labels = getYearLabels();
                break;
            default:
                cvsRaw = cvRepo.countByMonth();
                offresRaw = offreRepo.countByMonth();
                recruteursRaw = userRepo.countRecruteursByMonth();
                labels = getMonthLabels();
        }

        List<Long> cvsAgg = aggregate(labels.size(), cvsRaw);
        List<Long> offresAgg = aggregate(labels.size(), offresRaw);
        List<Long> recruteursAgg = aggregate(labels.size(), recruteursRaw);

        return new TrafficDashboardDTO(
                labels,
                cvsAgg,
                offresAgg,
                recruteursAgg,
                sumFromAggregateList(cvsAgg),
                sumFromAggregateList(offresAgg),
                sumFromAggregateList(recruteursAgg)
        );
    }

    private List<String> getMonthLabels() {
        return List.of("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
    }
    private List<String> getYearLabels() {
        // option statique ou dynamique
        return List.of("2023", "2024", "2025"); // ou extraire depuis la base si besoin
    }
    private List<String> getDaysLabels() {
        // option 1 : générer les derniers 30 jours
        LocalDate today = LocalDate.now();
        return IntStream.range(0, 30)
                .mapToObj(today::minusDays)
                .sorted()
                .map(LocalDate::toString)
                .collect(Collectors.toList());
    }
    private List<Long> aggregate(int size, List<Object[]> rawData) {
        List<Long> result = new ArrayList<>(Collections.nCopies(size, 0L));

        for (Object[] row : rawData) {
            if (row[0] == null || row[1] == null) continue;

            int index;

            if (row[0] instanceof Number num) {
                index = num.intValue(); // month = 1–12, year = 2023...
                if (size == 12) {
                    index = index - 1; // Pour mois : Janvier = index 0
                } else if (size == 3 && getYearLabels().contains(String.valueOf(index))) {
                    index = getYearLabels().indexOf(String.valueOf(index));
                } else {
                    continue; // Année non listée → on ignore
                }
            } else if (row[0] instanceof java.sql.Date date) {
                String dateStr = date.toLocalDate().toString(); // yyyy-MM-dd
                index = getDaysLabels().indexOf(dateStr);
            } else if (row[0] instanceof java.time.LocalDate date) {
                index = getDaysLabels().indexOf(date.toString());
            } else {
                continue;
            }

            Long count = ((Number) row[1]).longValue();
            if (index >= 0 && index < size) {
                result.set(index, count);
            }
        }

        return result;
    }
    private Long sumFromAggregateList(List<Long> aggregateList) {
        return aggregateList.stream().reduce(0L, Long::sum);
    }

    public long countActiveToday() {
        LocalDate today = LocalDate.now();
        return userRepo.countByLastLoginBetween(today.atStartOfDay(), today.plusDays(1).atStartOfDay());
    }

    @Override
    public long countCVsToday() {
        LocalDate today = LocalDate.now();

        Date startOfDay = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endOfDay = Date.from(today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        return cvRepo.countByDateUploadBetween(startOfDay, endOfDay);

    }

    @Override
    public List<RecruteurOffreStatDTO> getNombreOffresParRecruteur() {
        return offreRepo.countOffresByRecruteur();
    }



//    @Override
//    public List<SiteStatsDTO> getStatsParSite() {
//        List<SiteExterne> sites = siteExterneRepo.findAll();
//        List<SiteStatsDTO> stats = new ArrayList<>();
//
//        for (SiteExterne site : sites) {
//            List<OffreSiteExterne> offresExternes = offreSiteExterneRepo.findBySiteExterne(site);
//            long nbOffres = offresExternes.size();
//
//            long nbCandidatures = candidatureRepo.countBySourceSiteIn(offresExternes); // 👈 Utilise bien le champ ajouté
//
//            SiteStatsDTO dto = new SiteStatsDTO();
//            dto.setNomSite(site.getNom());
//            dto.setNombreOffres(nbOffres);
//            dto.setNombreCandidatures(nbCandidatures);
//            stats.add(dto);
//        }
//
//        return stats;
//    }

    public List<SiteStatsDTO> getStatsParSite() {
        return offreSiteExterneRepo.getStatsBySite();
    }


}

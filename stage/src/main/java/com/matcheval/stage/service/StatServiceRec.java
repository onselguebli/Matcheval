package com.matcheval.stage.service;

import com.matcheval.stage.dto.recruteurDTOS.CountLabelDTO;
import com.matcheval.stage.dto.recruteurDTOS.MonthlyCountDTO;
import com.matcheval.stage.dto.recruteurDTOS.OfferTopDTO;
import com.matcheval.stage.dto.recruteurDTOS.OverviewStatsDTO;
import com.matcheval.stage.interfaces.IStatServiceRec;
import com.matcheval.stage.repo.CandidatureRepo;
import com.matcheval.stage.repo.OffreRepo;
import com.matcheval.stage.repo.OffreSiteExterneRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StatServiceRec implements IStatServiceRec {
    @Autowired
    OffreRepo offreRepo;
    @Autowired
    CandidatureRepo candidatureRepo;
    @Autowired
    OffreSiteExterneRepo ose;

    public StatServiceRec(OffreRepo offreRepo, CandidatureRepo candidatureRepo, OffreSiteExterneRepo ose) {
        this.offreRepo = offreRepo;
        this.candidatureRepo = candidatureRepo;
        this.ose = ose;
    }

    @Override
    public OverviewStatsDTO overview(String email) {
        long totalOffers = offreRepo.totalOffers(email);
        long activeOffers = offreRepo.activeOffers(email);
        long expiredOffers = offreRepo.expiredOffers(email);
        long totalCand = candidatureRepo.totalCandidatures(email);

        long pending = 0, accepted = 0, rejected = 0;
        for (Object[] row : candidatureRepo.countCandidaturesByStatut(email)) {
            String label = (String) row[0];
            long count = ((Number) row[1]).longValue();
            if (label == null) continue;
            switch (label.toUpperCase()) {
                case "PENDING", "EN_ATTENTE" -> pending = count;
                case "ACCEPTED", "ACCEPTEE" -> accepted = count;
                case "REJECTED", "REFUSEE" -> rejected = count;
                default -> { }
            }
        }

        Double avgPerOffer = offreRepo.avgCandidatesPerOffer(email);
        Double avgDaysToFirst = offreRepo.avgDaysToFirstCandidate(email);

        return new OverviewStatsDTO(
                totalOffers, activeOffers, expiredOffers,
                totalCand, pending, accepted, rejected,
                avgPerOffer, avgDaysToFirst
        );
    }

    @Override
    public List<CountLabelDTO> offersByStatus(String email) {
        return mapCount(offreRepo.countOffersByStatut(email));
    }

    @Override
    public List<CountLabelDTO> offersByType(String email) {
        return mapCount(offreRepo.countOffersByType(email));
    }

    @Override
    public List<MonthlyCountDTO> candidaturesMonthly(String email, int year) {
        List<MonthlyCountDTO> out = new ArrayList<>();
        for (Object[] r : candidatureRepo.candidaturesMonthly(email, year)) {
            out.add(new MonthlyCountDTO((String) r[0], ((Number) r[1]).longValue()));
        }
        return out;
    }

    @Override
    public List<CountLabelDTO> candidaturesBySource(String email, int days) {
        return mapCount(ose.candidaturesBySource(email, days));
    }

    @Override
    public List<OfferTopDTO> topOffers(String email, int limit) {
        List<OfferTopDTO> out = new ArrayList<>();
        for (Object[] r : offreRepo.topOffersByCandidates(email, limit)) {
            out.add(new OfferTopDTO(
                    ((Number) r[0]).longValue(),
                    (String) r[1],
                    (java.util.Date) r[2],
                    (String) r[3],
                    ((Number) r[4]).longValue()
            ));
        }
        return out;
    }

    @Override
    public List<CountLabelDTO> mapCount(List<Object[]> rows) {
        List<CountLabelDTO> out = new ArrayList<>();
        for (Object[] r : rows) {
            out.add(new CountLabelDTO(
                    r[0] == null ? "INCONNU" : r[0].toString(),
                    ((Number) r[1]).longValue()
            ));
        }
        return out;
    }
}

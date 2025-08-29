package com.matcheval.stage.dto.recruteurDTOS;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class OverviewStatsDTO {
    private long totalOffers;
    private long activeOffers;
    private long expiredOffers;

    private long totalCandidatures;
    private long pendingCandidatures;
    private long acceptedCandidatures;
    private long rejectedCandidatures;

    private Double avgCandidatesPerOffer;
    private Double avgDaysToFirstCandidate;
}

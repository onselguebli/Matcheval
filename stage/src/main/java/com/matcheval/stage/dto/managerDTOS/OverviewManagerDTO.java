package com.matcheval.stage.dto.managerDTOS;

import lombok.Data;

@Data
public class OverviewManagerDTO {
    private long totalRecruiters;
    private long totalOffers;
    private long activeOffers;
    private long expiredOffers;
    private long totalCandidatures;
    private long pending;
    private long accepted;
    private long rejected;
    private double avgCandidatesPerOffer;
    private Double avgDaysToFirstCandidate;
    private long checkedMatchesLast30;


}
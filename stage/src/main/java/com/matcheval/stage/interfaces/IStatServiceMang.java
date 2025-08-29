package com.matcheval.stage.interfaces;

import com.matcheval.stage.dto.managerDTOS.*;

import java.util.List;

public interface IStatServiceMang {
    OverviewManagerDTO overview(String managerEmail);
    List<MonthlyCountDTO> candidaturesMonthly(String email, int year);
    List<CountLabelDTO> candidaturesBySource(String email, int days);
    List<CountLabelDTO> candidaturesByRecruiter(String email, int days);
    List<RecruiterPipelineDTO> pipelineByRecruiter(String email, int days);
    List<OfferTopDTO> topOffers(String email, int limit);
    List<CountLabelDTO> topRecruitersByChecked(String email, int limit);
    List<MeetingDTO> upcomingMeetings(String managerEmail, int days);
}

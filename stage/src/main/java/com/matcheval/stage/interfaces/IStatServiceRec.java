package com.matcheval.stage.interfaces;

import com.matcheval.stage.dto.recruteurDTOS.CountLabelDTO;
import com.matcheval.stage.dto.recruteurDTOS.MonthlyCountDTO;
import com.matcheval.stage.dto.recruteurDTOS.OfferTopDTO;
import com.matcheval.stage.dto.recruteurDTOS.OverviewStatsDTO;

import java.util.List;

public interface IStatServiceRec {

     OverviewStatsDTO overview(String email);
    List<CountLabelDTO> offersByStatus(String email);
    List<CountLabelDTO> offersByType(String email);
    List<MonthlyCountDTO> candidaturesMonthly(String email, int year);
    List<CountLabelDTO> candidaturesBySource(String email, int days);
    List<OfferTopDTO> topOffers(String email, int limit);
    List<CountLabelDTO> mapCount(List<Object[]> rows);

}

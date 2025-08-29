package com.matcheval.stage.controller;

import com.matcheval.stage.dto.*;
import com.matcheval.stage.dto.recruteurDTOS.CountLabelDTO;
import com.matcheval.stage.dto.recruteurDTOS.MonthlyCountDTO;
import com.matcheval.stage.dto.recruteurDTOS.OfferTopDTO;
import com.matcheval.stage.dto.recruteurDTOS.OverviewStatsDTO;
import com.matcheval.stage.interfaces.IStatServiceRec;
import com.matcheval.stage.service.StatServiceRec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("recruiter/stats")
public class RecruiterStatsController {

    @Autowired
    IStatServiceRec service;

    public RecruiterStatsController(StatServiceRec service) {
        this.service = service;
    }

    @GetMapping("/overview")
    public OverviewStatsDTO overview(@RequestParam String email) {
        return service.overview(email);
    }

    @GetMapping("/offers-by-status")
    public List<CountLabelDTO> offersByStatus(@RequestParam String email) {
        return service.offersByStatus(email);
    }

    @GetMapping("/offers-by-type")
    public List<CountLabelDTO> offersByType(@RequestParam String email) {
        return service.offersByType(email);
    }

    @GetMapping("/candidatures-monthly")
    public List<MonthlyCountDTO> candidaturesMonthly(@RequestParam String email, @RequestParam int year) {
        return service.candidaturesMonthly(email, year);
    }

    @GetMapping("/candidatures-by-source")
    public List<CountLabelDTO> candidaturesBySource(@RequestParam String email, @RequestParam(defaultValue = "90") int days) {
        return service.candidaturesBySource(email, days);
    }

    @GetMapping("/top-offers")
    public List<OfferTopDTO> topOffers(@RequestParam String email, @RequestParam(defaultValue = "5") int limit) {
        return service.topOffers(email, limit);
    }
}

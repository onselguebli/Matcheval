package com.matcheval.stage.controller;

import com.matcheval.stage.dto.managerDTOS.*;
import com.matcheval.stage.interfaces.IStatServiceMang;
import com.matcheval.stage.interfaces.IStatServiceRec;
import com.matcheval.stage.interfaces.IstatService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("manager/stats")
@AllArgsConstructor
public class StatMangController {
    @Autowired
    IStatServiceMang service ;
    @GetMapping("/overview")
    public OverviewManagerDTO overview(@RequestParam String email){
        return service.overview(email);
    }

    @GetMapping("/candidatures-monthly")
    public List<MonthlyCountDTO> monthly(@RequestParam String email, @RequestParam int year){
        return service.candidaturesMonthly(email, year);
    }

    @GetMapping("/sources")
    public List<CountLabelDTO> bySource(@RequestParam String email, @RequestParam(defaultValue = "90") int days){
        return service.candidaturesBySource(email, days);
    }

    @GetMapping("/by-recruiter")
    public List<CountLabelDTO> byRecruiter(@RequestParam String email, @RequestParam(defaultValue = "30") int days){
        return service.candidaturesByRecruiter(email, days);
    }

    @GetMapping("/pipeline-by-recruiter")
    public List<RecruiterPipelineDTO> pipelineByRecruiter(@RequestParam String email, @RequestParam(defaultValue = "90") int days){
        return service.pipelineByRecruiter(email, days);
    }

    @GetMapping("/top-offers")
    public List<OfferTopDTO> topOffers(@RequestParam String email, @RequestParam(defaultValue = "5") int limit){
        return service.topOffers(email, limit);
    }

    @GetMapping("/top-recruiters-checked")
    public List<CountLabelDTO> topRecruitersChecked(@RequestParam String email, @RequestParam(defaultValue = "5") int limit){
        return service.topRecruitersByChecked(email, limit);
    }

    @GetMapping("/meetings-upcoming")
    public List<MeetingDTO> upcoming(@RequestParam String email, @RequestParam(defaultValue = "14") int days){
        return service.upcomingMeetings(email, days);
    }
}

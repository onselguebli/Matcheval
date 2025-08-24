package com.matcheval.stage.controller;

import com.matcheval.stage.dto.AddCheckRequest;
import com.matcheval.stage.model.CheckedMatch;
import com.matcheval.stage.service.CheckedMatchService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.matcheval.stage.dto.CheckedMatchDTO;

import java.util.List;
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200"})
public class CheckedMatchController {

    @Autowired CheckedMatchService checkedMatchService;

    @PostMapping("/recruiter/checked-matches/add")
    public CheckedMatchDTO addCheck(@RequestBody AddCheckRequest req) {
        return checkedMatchService.addCheckByEmail(
                req.getOffreId(),
                req.getCandidatureId(),
                req.getRecruteurEmail(),
                req.getScoreOverall(),
                req.getFilenameSnapshot()
        );
    }

    @GetMapping("/recruiter/checked-matches")
    public List<CheckedMatchDTO> listForRecruteur(@RequestParam String email) {
        return checkedMatchService.listForRecruteurByEmail(email);
    }

    @GetMapping("/manager/checked-matches")
    public List<CheckedMatchDTO> listForManager(@RequestParam String email,
                                                @RequestParam(required = false) String recruteurEmail) {
        return checkedMatchService.listForManagerByEmail(email, recruteurEmail);
    }

    @DeleteMapping("/{checkId}/checked-matches")
    public void deleteCheck(@PathVariable Long checkId) {
        checkedMatchService.deleteCheck(checkId);
    }



}

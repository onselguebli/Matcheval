package com.matcheval.stage.controller;

import com.matcheval.stage.interfaces.IstatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("recruiter")

public class StatRecController {
    @Autowired
    IstatService statService;

    @GetMapping("/stats/candidatures-par-site-type/recruteur/{email}")
    public ResponseEntity<Map<String, Map<String, Long>>> candidaturesParSiteEtTypePourRecruteur(
            @PathVariable String email) {
        return ResponseEntity.ok(statService.candidaturesParSiteEtTypePourRecruteur(email));
    }

}

package com.matcheval.stage.controller;

import com.matcheval.stage.dto.CandidatureDTO;
import com.matcheval.stage.dto.OffreWithCandidaturesDTO;
import com.matcheval.stage.interfaces.ICandidatureService;
import com.matcheval.stage.interfaces.IOffreService;
import com.matcheval.stage.interfaces.IUserService;
import com.matcheval.stage.dto.ReqRes;
import com.matcheval.stage.model.Candidature;
import com.matcheval.stage.model.OffreEmploi;
import com.matcheval.stage.service.OffreService;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("recruiter")
public class UserController {

    @Autowired
    IUserService userService;
    @Autowired
    IOffreService offreService;
    @Autowired
    ICandidatureService candidatureService;

    @PostMapping("/login")
    public ReqRes login(@RequestBody ReqRes reqRes) {  // ✅ Use DTO
        return userService.login(reqRes);
    }
    @GetMapping("/offres-candidatures/{recruteurEmail}")
    public ResponseEntity<List<OffreWithCandidaturesDTO>> getOffresWithCandidatures(@PathVariable String recruteurEmail) {
        return offreService.getOffresWithCandidatures(recruteurEmail);
    }
    @GetMapping("/candidature/{id}")
    public ResponseEntity<CandidatureDTO> getCandidatureById(@PathVariable Long id) {
        return candidatureService.getCandidatureById(id);
    }

    @GetMapping("/candidature/{id}/cv")
    public ResponseEntity<Resource> downloadCv(@PathVariable Long id) {
        ByteArrayResource resource = candidatureService.getCvFile(id);
        String filename    = candidatureService.getCvFilename(id);
        String contentType = candidatureService.getCvContentType(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(
                        (contentType != null && !contentType.isBlank()) ? contentType : "application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentLength(resource.contentLength())
                .body(resource); // ByteArrayResource implémente org.springframework.core.io.Resource
    }


    @PutMapping("/candidature/{id}/statut")
    public ResponseEntity<CandidatureDTO> updateStatut(
            @PathVariable Long id,
            @RequestBody CandidatureDTO dto) {
        CandidatureDTO updated = candidatureService.updateCandidature(id, dto);
        return ResponseEntity.ok(updated);
    }


}

package com.matcheval.stage.controller;

import com.matcheval.stage.service.MatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("recruiter/matching")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200"})
public class MatchingController {


    @Autowired
    MatchingService matchingService;

    @PostMapping("/{offreId}/upload-cv")
    public ResponseEntity<Map<String, Object>> matchUploadedCv(
            @PathVariable Long offreId,
            @RequestParam("cv") MultipartFile cvFile) {
        try {
            Map<String, Object> result = matchingService.matchCvWithOffre(offreId, cvFile);
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("error", "Erreur de traitement du CV"));
        }
    }

    @GetMapping("/{offreId}/all-cvs")
    public ResponseEntity<List<Map<String, Object>>> matchAllCvsForOffre(@PathVariable Long offreId) {
        try {
            List<Map<String, Object>> results = matchingService.matchAllCvsWithOffre(offreId);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(List.of(Map.of("error", e.getMessage())));
        }
    }

    @GetMapping("/{offreId}/candidature/{candidatureId}")
    public ResponseEntity<Map<String, Object>> matchSpecificCv(
            @PathVariable Long offreId,
            @PathVariable Long candidatureId) {
        try {
            Map<String, Object> result = matchingService.matchSpecificCv(candidatureId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    @PostMapping("/{offreId}/selected-cvs")
    public ResponseEntity<List<Map<String, Object>>> matchSelectedCvsForOffre(
            @PathVariable Long offreId,
            @RequestBody List<Long> candidatureIds) {
        try {
            List<Map<String, Object>> results = matchingService.matchSelectedCvsWithOffre(offreId, candidatureIds);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(List.of(Map.of("error", e.getMessage())));
        }
    }


}

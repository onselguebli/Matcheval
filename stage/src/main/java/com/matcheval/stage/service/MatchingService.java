package com.matcheval.stage.service;

import com.matcheval.stage.model.Candidature;
import com.matcheval.stage.model.OffreEmploi;
import com.matcheval.stage.repo.CandidatureRepo;
import com.matcheval.stage.repo.OffreRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchingService {

    @Autowired
    OffreRepo offreRepository;
    @Autowired
    CandidatureRepo candidatureRepository;
    private final RestTemplate restTemplate;

    private final String FASTAPI_URL = "http://localhost:8000";

    public Map<String, Object> matchCvWithOffre(Long offreId, MultipartFile cvFile) throws IOException {
        OffreEmploi offre = offreRepository.findById(offreId)
                .orElseThrow(() -> new RuntimeException("Offre non trouvée"));

        // Préparer les données pour FastAPI
        Map<String, Object> jobData = new HashMap<>();
        jobData.put("title", offre.getTitre());
        jobData.put("description", offre.getDescription());
        jobData.put("skills_required", extractSkillsFromText(offre.getExigences()));
        jobData.put("min_years_experience", extractYearsFromText(offre.getExigences()));
        jobData.put("location", offre.getLocalisation());
        jobData.put("languages_required", Arrays.asList("french", "english"));

        // Envoyer à FastAPI
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        var bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("files", cvFile.getResource());
        bodyBuilder.part("job_json", jobData);

        HttpEntity<?> requestEntity = new HttpEntity<>(bodyBuilder.build(), headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                FASTAPI_URL + "/match-multiple",
                HttpMethod.POST,
                requestEntity,
                Map.class
        );

        return response.getBody();
    }

    public List<Map<String, Object>> matchAllCvsWithOffre(Long offreId) {
        OffreEmploi offre = offreRepository.findById(offreId)
                .orElseThrow(() -> new RuntimeException("Offre non trouvée"));

        List<Candidature> candidatures = candidatureRepository.findByOffreId(offreId);
        List<Map<String, Object>> results = new ArrayList<>();

        for (Candidature cand : candidatures) {
            if (cand.getCv() != null && cand.getCv().getData() != null) {
                try {
                    Map<String, Object> matchResult = matchExistingCvWithOffre(offre, cand);
                    results.add(matchResult);
                } catch (Exception e) {
                    // Gérer l'erreur mais continuer avec les autres CVs
                    Map<String, Object> errorResult = new HashMap<>();
                    errorResult.put("candidatureId", cand.getId());
                    errorResult.put("error", e.getMessage());
                    errorResult.put("score", 0.0);
                    results.add(errorResult);
                }
            }
        }

        // Trier par score décroissant
        results.sort((a, b) -> Double.compare(
                (Double) b.getOrDefault("score", 0.0),
                (Double) a.getOrDefault("score", 0.0)
        ));

        return results;
    }

    private Map<String, Object> matchExistingCvWithOffre(OffreEmploi offre, Candidature cand) {
        // Préparer les données pour FastAPI
        Map<String, Object> jobData = new HashMap<>();
        jobData.put("title", offre.getTitre());
        jobData.put("description", offre.getDescription());
        jobData.put("skills_required", extractSkillsFromText(offre.getExigences()));
        jobData.put("min_years_experience", extractYearsFromText(offre.getExigences()));
        jobData.put("location", offre.getLocalisation());
        jobData.put("languages_required", Arrays.asList("french", "english"));

        // Préparer le CV (utiliser le texte extrait si disponible)
        String cvText = cand.getCv().getContenuTexte();
        if (cvText == null || cvText.trim().isEmpty()) {
            cvText = "CV content not available";
        }

        Map<String, Object> cvData = new HashMap<>();
        cvData.put("filename", cand.getCv().getOriginalFilename());
        cvData.put("text", cvText);
        cvData.put("skills", extractSkillsFromText(cvText));

        // Appeler FastAPI (version simplifiée pour le texte)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> request = new HashMap<>();
        request.put("job", jobData);
        request.put("cv", cvData);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(request, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                FASTAPI_URL + "/match-text",
                HttpMethod.POST,
                requestEntity,
                Map.class
        );

        Map<String, Object> result = response.getBody();
        result.put("candidatureId", cand.getId());
        result.put("candidatNom", cand.getCandidatNom());
        result.put("candidatPrenom", cand.getCandidatPrenom());
        result.put("candidatEmail", cand.getCandidatEmail());

        return result;
    }

    private List<String> extractSkillsFromText(String text) {
        if (text == null) return Arrays.asList();

        List<String> skills = new ArrayList<>();
        String[] commonSkills = {"java", "python", "javascript", "spring", "react", "angular",
                "sql", "docker", "kubernetes", "aws", "azure", "git"};

        String lowerText = text.toLowerCase();
        for (String skill : commonSkills) {
            if (lowerText.contains(skill)) {
                skills.add(skill);
            }
        }
        return skills;
    }

    private Integer extractYearsFromText(String text) {
        if (text == null) return 0;

        // Regex simple pour trouver des années d'expérience
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(\\d+)\\s*(ans|années|years)");
        java.util.regex.Matcher matcher = pattern.matcher(text.toLowerCase());

        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return 0;
    }
}
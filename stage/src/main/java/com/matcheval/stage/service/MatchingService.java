package com.matcheval.stage.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matcheval.stage.model.Candidature;
import com.matcheval.stage.model.OffreEmploi;
import com.matcheval.stage.repo.CandidatureRepo;
import com.matcheval.stage.repo.OffreRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchingService {

    private final OffreRepo offreRepository;
    private final CandidatureRepo candidatureRepository;
    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${fastapi.url}")
    private String fastapiUrl;

    // =========================
    // PUBLIC API
    // =========================

    public Map<String, Object> matchCvWithOffre(Long offreId, MultipartFile cvFile) {
        OffreEmploi offre = offreRepository.findById(offreId)
                .orElseThrow(() -> new RuntimeException("Offre non trouvée"));

        if (cvFile == null || cvFile.isEmpty()) {
            throw new RuntimeException("Fichier CV vide ou non fourni");
        }

        HttpEntity<?> requestEntity = buildFastApiRequest(offre, cvFile);

        return callFastApiMatchMultiple(requestEntity);
    }

    public List<Map<String, Object>> matchAllCvsWithOffre(Long offreId) {
        OffreEmploi offre = offreRepository.findById(offreId)
                .orElseThrow(() -> new RuntimeException("Offre non trouvée"));

        List<Candidature> candidatures = candidatureRepository.findByOffreId(offreId);
        List<Map<String, Object>> results = new ArrayList<>();

        for (Candidature cand : candidatures) {
            if (cand.getCv() != null && cand.getCv().getData() != null && cand.getCv().getData().length > 0) {
                try {
                    Map<String, Object> matchResult = matchExistingCvWithOffre(offre, cand);
                    results.add(matchResult);
                } catch (Exception e) {
                    Map<String, Object> errorResult = new HashMap<>();
                    errorResult.put("candidatureId", cand.getId());
                    errorResult.put("error", e.getMessage());
                    errorResult.put("score_overall", 0.0);
                    results.add(errorResult);
                }
            }
        }

        // ✅ Trier par score_overall décroissant (nom réel renvoyé par FastAPI)
        results.sort((a, b) -> Double.compare(
                getScoreOverall(b),
                getScoreOverall(a)
        ));

        return results;
    }

    public List<Map<String, Object>> matchSelectedCvsWithOffre(Long offreId, List<Long> candidatureIds) {
        OffreEmploi offre = offreRepository.findById(offreId)
                .orElseThrow(() -> new RuntimeException("Offre non trouvée"));

        if (candidatureIds == null || candidatureIds.isEmpty()) {
            return List.of();
        }

        List<Map<String, Object>> results = new ArrayList<>();

        for (Long candId : candidatureIds) {
            Candidature cand = candidatureRepository.findById(candId)
                    .orElseThrow(() -> new RuntimeException("Candidature non trouvée: " + candId));

            if (cand.getCv() != null && cand.getCv().getData() != null && cand.getCv().getData().length > 0) {
                try {
                    Map<String, Object> matchResult = matchExistingCvWithOffre(offre, cand);
                    results.add(matchResult);
                } catch (Exception e) {
                    Map<String, Object> errorResult = new HashMap<>();
                    errorResult.put("candidatureId", cand.getId());
                    errorResult.put("error", e.getMessage());
                    errorResult.put("score_overall", 0.0);
                    results.add(errorResult);
                }
            }
        }

        results.sort((a, b) -> Double.compare(
                getScoreOverall(b),
                getScoreOverall(a)
        ));

        return results;
    }

    public Map<String, Object> matchSpecificCv(Long candidatureId) {
        Candidature cand = candidatureRepository.findById(candidatureId)
                .orElseThrow(() -> new RuntimeException("Candidature non trouvée: " + candidatureId));

        if (cand.getOffre() == null) {
            throw new RuntimeException("Aucune offre associée à cette candidature");
        }

        return matchExistingCvWithOffre(cand.getOffre(), cand);
    }

    public Map<String, Object> matchSpecificCvWithDetails(Long candidatureId) {
        Candidature cand = candidatureRepository.findById(candidatureId)
                .orElseThrow(() -> new RuntimeException("Candidature non trouvée: " + candidatureId));

        if (cand.getOffre() == null) {
            throw new RuntimeException("Aucune offre associée à cette candidature");
        }

        Map<String, Object> result = matchExistingCvWithOffre(cand.getOffre(), cand);

        result.put("candidatureId", cand.getId());
        result.put("candidatNom", cand.getCandidatNom());
        result.put("candidatPrenom", cand.getCandidatPrenom());
        result.put("candidatEmail", cand.getCandidatEmail());
        result.put("dateSoumission", cand.getDateSoumission());
        result.put("offreId", cand.getOffre().getId());
        result.put("offreTitre", cand.getOffre().getTitre());

        return result;
    }

    // =========================
    // INTERNAL: build request + call FastAPI
    // =========================

    private HttpEntity<?> buildFastApiRequest(OffreEmploi offre, MultipartFile cvFile) {
        Map<String, Object> jobData = buildJobData(offre);

        final String jobJson;
        try {
            jobJson = objectMapper.writeValueAsString(jobData);
        } catch (Exception e) {
            throw new RuntimeException("Erreur sérialisation job_json", e);
        }

        MultipartBodyBuilder builder = new MultipartBodyBuilder();

        // ✅ IMPORTANT: mettre filename + content-type (sinon FastAPI peut recevoir un fichier sans metadata)
        String filename = (cvFile.getOriginalFilename() != null && !cvFile.getOriginalFilename().isBlank())
                ? cvFile.getOriginalFilename()
                : "cv.pdf";

        MediaType fileType = MediaType.APPLICATION_PDF;
        if (cvFile.getContentType() != null && cvFile.getContentType().toLowerCase().contains("pdf")) {
            fileType = MediaType.APPLICATION_PDF;
        }

        builder.part("files", cvFile.getResource())
                .filename(filename)
                .contentType(fileType);

        // ✅ job_json est un champ texte contenant du JSON (Form)
        builder.part("job_json", jobJson)
                .contentType(MediaType.TEXT_PLAIN);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        return new HttpEntity<>(builder.build(), headers);
    }

    private Map<String, Object> matchExistingCvWithOffre(OffreEmploi offre, Candidature cand) {
        byte[] data = cand.getCv().getData();
        String filename = cand.getCv().getOriginalFilename();
        String contentType = cand.getCv().getContentType();

        // ✅ construire un "fichier" à partir des bytes (plus fiable que MultipartFile anonyme)
        ByteArrayResource resource = new ByteArrayResource(data) {
            @Override
            public String getFilename() {
                return (filename != null && !filename.isBlank()) ? filename : "cv.pdf";
            }
        };

        Map<String, Object> jobData = buildJobData(offre);

        final String jobJson;
        try {
            jobJson = objectMapper.writeValueAsString(jobData);
        } catch (Exception e) {
            throw new RuntimeException("Erreur sérialisation job_json", e);
        }

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        MediaType fileType = (contentType != null && contentType.toLowerCase().contains("pdf"))
                ? MediaType.APPLICATION_PDF
                : MediaType.APPLICATION_OCTET_STREAM;

        builder.part("files", resource)
                .filename(resource.getFilename())
                .contentType(fileType);

        builder.part("job_json", jobJson)
                .contentType(MediaType.TEXT_PLAIN);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<?> requestEntity = new HttpEntity<>(builder.build(), headers);

        Map<String, Object> responseBody = callFastApiMatchMultiple(requestEntity);

        // FastAPI renvoie { job_received, results: [ ... ] }
        Object resultsObj = responseBody.get("results");
        if (resultsObj instanceof List<?> list && !list.isEmpty() && list.get(0) instanceof Map<?, ?>) {
            @SuppressWarnings("unchecked")
            Map<String, Object> first = (Map<String, Object>) list.get(0);

            first.put("candidatureId", cand.getId());
            first.put("candidatNom", cand.getCandidatNom());
            first.put("candidatPrenom", cand.getCandidatPrenom());
            first.put("candidatEmail", cand.getCandidatEmail());

            return first;
        }

        return Map.of(
                "error", "No matching results",
                "candidatureId", cand.getId(),
                "score_overall", 0.0
        );
    }

    private Map<String, Object> callFastApiMatchMultiple(HttpEntity<?> requestEntity) {
        String url = normalizeFastApiUrl(fastapiUrl) + "/match-multiple";

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    Map.class
            );

            Map<String, Object> body = response.getBody();
            return body != null ? body : Map.of("error", "Empty response from FastAPI");

        } catch (HttpStatusCodeException e) {
            // ✅ log utile sur Render
            String resp = e.getResponseBodyAsString();
            log.error("FastAPI HTTP error: status={} url={} body={}", e.getStatusCode(), url, resp);
            throw new RuntimeException("FastAPI error: " + e.getStatusCode() + " - " + resp);

        } catch (ResourceAccessException e) {
            // timeout / DNS / connexion
            log.error("FastAPI unreachable: url={} msg={}", url, e.getMessage(), e);
            throw new RuntimeException("FastAPI unreachable: " + e.getMessage());

        } catch (Exception e) {
            log.error("FastAPI call failed: url={} msg={}", url, e.getMessage(), e);
            throw new RuntimeException("FastAPI call failed: " + e.getMessage());
        }
    }

    private String normalizeFastApiUrl(String base) {
        if (base == null) return "";
        String b = base.trim();
        if (b.endsWith("/")) b = b.substring(0, b.length() - 1);
        return b;
    }

    private Map<String, Object> buildJobData(OffreEmploi offre) {
        Map<String, Object> jobData = new HashMap<>();
        jobData.put("title", safe(offre.getTitre()));
        jobData.put("description", safe(offre.getDescription()));
        jobData.put("skills_required", extractSkillsFromText(offre.getExigences()));
        jobData.put("min_years_experience", extractYearsFromText(offre.getExigences()));
        jobData.put("location", safe(offre.getLocalisation()));
        jobData.put("languages_required", List.of("french", "english"));
        return jobData;
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }

    private double getScoreOverall(Map<String, Object> item) {
        Object v = item.get("score_overall");
        if (v instanceof Number n) return n.doubleValue();
        if (v instanceof String s) {
            try { return Double.parseDouble(s); } catch (Exception ignored) {}
        }
        // fallback ancien champ éventuel
        Object v2 = item.get("score");
        if (v2 instanceof Number n2) return n2.doubleValue();
        return 0.0;
    }

    // =========================
    // EXTRACTION HELPERS
    // =========================

    private List<String> extractSkillsFromText(String text) {
        if (text == null) return List.of();

        List<String> skills = new ArrayList<>();
        String[] commonSkills = {
                "java", "python", "javascript", "spring", "react", "angular",
                "sql", "docker", "kubernetes", "aws", "azure", "git"
        };

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
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(\\d+)\\s*(ans|années|years)", java.util.regex.Pattern.CASE_INSENSITIVE);
        java.util.regex.Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return 0;
    }
}

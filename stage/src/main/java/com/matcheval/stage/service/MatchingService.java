package com.matcheval.stage.service;

import com.matcheval.stage.model.Candidature;
import com.matcheval.stage.model.OffreEmploi;
import com.matcheval.stage.repo.CandidatureRepo;
import com.matcheval.stage.repo.OffreRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchingService {

    @Autowired
    OffreRepo offreRepository;
    @Autowired
    CandidatureRepo candidatureRepository;

    private final RestTemplate restTemplate;

    @Value("${fastapi.url}")
    private String fastapiUrl;

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
                fastapiUrl + "/match-multiple",
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

        // Créer un MultipartFile à partir des données binaires du CV
        MultipartFile cvFile = createMultipartFileFromBytes(
                cand.getCv().getData(),
                cand.getCv().getOriginalFilename(),
                cand.getCv().getContentType()
        );

        // Envoyer à FastAPI avec le format attendu
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        var bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("files", cvFile.getResource());
        bodyBuilder.part("job_json", jobData);

        HttpEntity<?> requestEntity = new HttpEntity<>(bodyBuilder.build(), headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                fastapiUrl + "/match-multiple",
                HttpMethod.POST,
                requestEntity,
                Map.class
        );

        Map<String, Object> result = response.getBody();
        // Extraire le premier résultat (puisqu'on envoie un seul CV)
        if (result != null && result.containsKey("results") && ((List<?>) result.get("results")).size() > 0) {
            Map<String, Object> firstResult = (Map<String, Object>) ((List<?>) result.get("results")).get(0);
            firstResult.put("candidatureId", cand.getId());
            firstResult.put("candidatNom", cand.getCandidatNom());
            firstResult.put("candidatPrenom", cand.getCandidatPrenom());
            firstResult.put("candidatEmail", cand.getCandidatEmail());
            return firstResult;
        }

        return Map.of("error", "No matching results", "candidatureId", cand.getId());
    }

    // Méthode utilitaire pour créer un MultipartFile à partir de bytes
    private MultipartFile createMultipartFileFromBytes(byte[] data, String filename, String contentType) {
        return new MultipartFile() {
            @Override
            public String getName() {
                return "file";
            }

            @Override
            public String getOriginalFilename() {
                return filename;
            }

            @Override
            public String getContentType() {
                return contentType;
            }

            @Override
            public boolean isEmpty() {
                return data == null || data.length == 0;
            }

            @Override
            public long getSize() {
                return data != null ? data.length : 0;
            }

            @Override
            public byte[] getBytes() throws IOException {
                return data != null ? data : new byte[0];
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return new ByteArrayInputStream(data != null ? data : new byte[0]);
            }

            @Override
            public void transferTo(File dest) throws IOException, IllegalStateException {
                Files.write(dest.toPath(), data != null ? data : new byte[0]);
            }
        };
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

    public List<Map<String, Object>> matchSelectedCvsWithOffre(Long offreId, List<Long> candidatureIds) {
        OffreEmploi offre = offreRepository.findById(offreId)
                .orElseThrow(() -> new RuntimeException("Offre non trouvée"));

        List<Map<String, Object>> results = new ArrayList<>();

        for (Long candId : candidatureIds) {
            Candidature cand = candidatureRepository.findById(candId)
                    .orElseThrow(() -> new RuntimeException("Candidature non trouvée: " + candId));

            if (cand.getCv() != null && cand.getCv().getData() != null) {
                try {
                    Map<String, Object> matchResult = matchExistingCvWithOffre(offre, cand);
                    results.add(matchResult);
                } catch (Exception e) {
                    Map<String, Object> errorResult = new HashMap<>();
                    errorResult.put("candidatureId", cand.getId());
                    errorResult.put("error", e.getMessage());
                    errorResult.put("score", 0.0);
                    results.add(errorResult);
                }
            }
        }

        results.sort((a, b) -> Double.compare(
                (Double) b.getOrDefault("score", 0.0),
                (Double) a.getOrDefault("score", 0.0)
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

    ////////pour selectionner et afficher les cv de score high :
    // Dans MatchingService.java
    public Map<String, Object> matchSpecificCvWithDetails(Long candidatureId) {
        Candidature cand = candidatureRepository.findById(candidatureId)
                .orElseThrow(() -> new RuntimeException("Candidature non trouvée: " + candidatureId));

        if (cand.getOffre() == null) {
            throw new RuntimeException("Aucune offre associée à cette candidature");
        }

        Map<String, Object> result = matchExistingCvWithOffre(cand.getOffre(), cand);

        // Ajouter des informations supplémentaires
        result.put("candidatureId", cand.getId());
        result.put("candidatNom", cand.getCandidatNom());
        result.put("candidatPrenom", cand.getCandidatPrenom());
        result.put("candidatEmail", cand.getCandidatEmail());
        result.put("dateSoumission", cand.getDateSoumission());
        result.put("offreId", cand.getOffre().getId());
        result.put("offreTitre", cand.getOffre().getTitre());

        return result;
    }


}
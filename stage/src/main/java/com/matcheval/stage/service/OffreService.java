package com.matcheval.stage.service;

import com.matcheval.stage.dto.CanDTO;
import com.matcheval.stage.dto.CandidatureExterne;
import com.matcheval.stage.dto.OffreEmploiDTO;
import com.matcheval.stage.dto.OffreWithCandidaturesDTO;
import com.matcheval.stage.model.*;
import com.matcheval.stage.interfaces.IOffreService;
import com.matcheval.stage.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OffreService implements IOffreService {
    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private OffreRepo offreRepository;


    @Autowired
    private SiteExterneRepo siteRepo;
    @Autowired
    private CandidatureRepo candidatureRepo;
    @Autowired
    private OffreSiteExterneRepo offreSiteExterneRepo;

    @Override
    public boolean diffuserOffre(OffreEmploi offre, SiteExterne site) {
        try {
            Map<String, Object> payload = Map.of(
                    "titre", offre.getTitre(),
                    "description", offre.getDescription(),
                    "exigence",offre.getExigences(),
                    "localisation", offre.getLocalisation(),
                    "date de publication",offre.getDatePublication(),
                    "date d expiration",offre.getDateExpiration(),
                    "type d offre",offre.getTypeOffre().name(),
                    "satut",offre.getStatut()

            );

            restTemplate.postForEntity(site.getApiConfig(), payload, String.class);
            return true;
        } catch (Exception e) {
            System.out.println("❌ Erreur diffusion vers " + site.getNom() + ": " + e.getMessage());
            return false;
        }
    }

    @Override
    public OffreEmploi publierEtDiffuserOffre(OffreEmploi offre, Users recruteur) {

        offre.setRecruteur(recruteur);
        offre.setDatePublication(new Date());
        // 1. sauvegarde l'offre
        OffreEmploi savedOffre = offreRepository.save(offre);

        // 2. récupérer les sites
        List<SiteExterne> sites = siteRepo.findAll();

        // 3. diffuser
        for (SiteExterne site : sites) {
            boolean success = diffuserOffre(savedOffre, site);
            System.out.println("✅ Diffusion vers " + site.getNom() + ": " + (success ? "OK" : "Échec"));
            OffreSiteExterne ose = new OffreSiteExterne();
            ose.setOffre(savedOffre);
            ose.setSiteExterne(site);
            ose.setDateDiffusion(LocalDateTime.now());
            ose.setStatutDiffusion(success ? "SUCCES" : "ECHEC");

            offreSiteExterneRepo.save(ose);

        }

        return savedOffre;
    }
    public void extraireCandidaturesDepuisSitesExternes(OffreEmploi offre) {
        List<SiteExterne> sites = siteRepo.findAll();

        for (SiteExterne site : sites) {
            String url = site.getApiConfig() + "/candidatures?offreId=" + offre.getId();

            try {
                ResponseEntity<CandidatureExterne[]> response =
                        restTemplate.getForEntity(url, CandidatureExterne[].class);
                CandidatureExterne[] donnees = response.getBody();
                if (donnees == null) continue;

                for (CandidatureExterne externe : donnees) {
                    if (candidatureRepo.existsByCandidatEmailAndOffreId(externe.getEmail(), offre.getId())) {
                        continue;
                    }

                    var optLink = offreSiteExterneRepo.findByOffreAndSiteExterne(offre, site);
                    if (optLink.isEmpty()) {
                        System.err.println("❌ Pas de lien OffreSiteExterne pour " + site.getNom());
                        continue;
                    }
                    OffreSiteExterne lien = optLink.get();

                    // 1) Candidature
                    Candidature cand = new Candidature();
                    cand.setOffre(offre);
                    cand.setSourceSite(lien);
                    cand.setDateSoumission(LocalDateTime.now());
                    cand.setStatut("En attente");
                    cand.setCommentaire(externe.getCommentaire());
                    cand.setCandidatNom(externe.getNom());
                    cand.setCandidatPrenom(externe.getPrenom());
                    cand.setCandidatEmail(externe.getEmail());

                    // 2) CV (vrai fichier via base64 ou url)
                    CV cv = new CV();
                    byte[] bytes = null;
                    String contentType = nvl(externe.getCv() != null ? externe.getCv().getContentType() : null, "application/pdf");
                    String filename = nvl(externe.getCv() != null ? externe.getCv().getFilename() : null, "cv.pdf");

                    if (externe.getCv() != null) {
                        // a) base64 prioritaire
                        String b64 = externe.getCv().getBase64();
                        if (b64 != null && !b64.isBlank()) {
                            // si jamais le partenaire envoie "data:...;base64,XXXX", on enlève la tête
                            int coma = b64.indexOf(',');
                            if (b64.startsWith("data:") && coma > 0) {
                                b64 = b64.substring(coma + 1);
                            }
                            try {
                                bytes = Base64.getDecoder().decode(b64);
                            } catch (IllegalArgumentException e) {
                                System.err.println("⚠️ Base64 invalide pour " + externe.getEmail());
                            }
                        }

                        // b) URL si pas de base64
                        if (bytes == null && externe.getCv().getUrl() != null && !externe.getCv().getUrl().isBlank()) {
                            try {
                                ResponseEntity<byte[]> fileResp = restTemplate.getForEntity(externe.getCv().getUrl(), byte[].class);
                                if (fileResp.getStatusCode().is2xxSuccessful() && fileResp.getBody() != null) {
                                    bytes = fileResp.getBody();
                                    if (fileResp.getHeaders().getContentType() != null) {
                                        contentType = fileResp.getHeaders().getContentType().toString();
                                    }
                                    // si filename manquant, on l’infère de l’URL
                                    if ("cv.pdf".equals(filename)) {
                                        filename = inferNameFromUrl(externe.getCv().getUrl(), filename);
                                    }
                                }
                            } catch (Exception e) {
                                System.err.println("⚠️ Erreur téléchargement CV URL pour " + externe.getEmail() + " : " + e.getMessage());
                            }
                        }

                        // c) dateUpload
                        if (externe.getCv().getDateUpload() != null) {
                            try {
                                var fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                                var ld = LocalDate.parse(externe.getCv().getDateUpload(), fmt);
                                cv.setDateUpload(java.sql.Date.valueOf(ld));
                            } catch (Exception ignore) {
                                cv.setDateUpload(new Date());
                            }
                        } else {
                            cv.setDateUpload(new Date());
                        }

                        // d) si toujours pas de contentType, on l’infère du nom
                        if (contentType == null || contentType.isBlank()) {
                            contentType = guessContentType(filename); // simple mapping
                        }
                    }

                    // 3) Affecter données si présentes
                    if (bytes != null) {
                        cv.setData(bytes);
                        cv.setSize((long) bytes.length);
                        cv.setContentType(contentType);
                        cv.setOriginalFilename(filename);
                    } else {
                        // rien reçu -> on insère la candidature sans blob (à toi de décider si skip)
                        System.err.println("⚠️ Aucun fichier CV reçu pour " + externe.getEmail());
                    }

                    // 4) Lier & sauver
                    cv.setCandidature(cand);
                    cand.setCv(cv);
                    candidatureRepo.save(cand);
                }

                System.out.println("✅ Candidatures extraites depuis " + site.getNom());
            } catch (Exception e) {
                System.err.println("❌ Erreur d'extraction sur " + site.getNom() + " : " + e.getMessage());
            }
        }
    }

    // helpers
    private static String nvl(String v, String def) { return (v == null || v.isBlank()) ? def : v; }

    private static String inferNameFromUrl(String url, String fallback) {
        try {
            var u = URI.create(url);
            var p = u.getPath();
            if (p != null && p.contains("/")) return p.substring(p.lastIndexOf('/') + 1);
        } catch (Exception ignore) {}
        return fallback;
    }

    private static String guessContentType(String filename) {
        String f = filename.toLowerCase();
        if (f.endsWith(".pdf")) return "application/pdf";
        if (f.endsWith(".docx")) return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        if (f.endsWith(".doc")) return "application/msword";
        if (f.endsWith(".png")) return "image/png";
        if (f.endsWith(".jpg") || f.endsWith(".jpeg")) return "image/jpeg";
        return "application/octet-stream";
    }

    public ResponseEntity<List<OffreWithCandidaturesDTO>> getOffresWithCandidatures( String recruteurEmail) {
        List<OffreEmploi> offres = offreRepository.findOffresWithCandidaturesByRecruteur(recruteurEmail);

        List<OffreWithCandidaturesDTO> dtoList = offres.stream().map(offre -> {
            List<OffreWithCandidaturesDTO.CandidatureDTO> candidatures = offre.getCandidatures()
                    .stream()
                    .map(c -> new OffreWithCandidaturesDTO.CandidatureDTO(
                            c.getId(),
                            c.getCandidatNom(),
                            c.getCandidatPrenom(),
                            c.getDateSoumission()))
                    .toList();

            return new OffreWithCandidaturesDTO(
                    offre.getId(),
                    offre.getTitre(),
                    candidatures);
        }).toList();

        return ResponseEntity.ok(dtoList);
    }


    @Override
    public List<OffreEmploiDTO> getOffresByRecruteurEmail(String email) {
        List<OffreEmploi> offres = offreRepository.findByRecruteurEmail(email);
        return offres.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public OffreEmploiDTO modifierOffreEtSynchroniser(Long id, OffreEmploiDTO dto) {
        OffreEmploi offre = offreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offre non trouvée"));

        if (dto.getTitre() != null) offre.setTitre(dto.getTitre());
        if (dto.getDatePublication()!=null) offre.setDatePublication(dto.getDatePublication());
        if (dto.getDescription() != null) offre.setDescription(dto.getDescription());
        if (dto.getExigences() != null) offre.setExigences(dto.getExigences());
        if (dto.getDateExpiration() != null) offre.setDateExpiration(dto.getDateExpiration());
        if (dto.getStatut() != null) offre.setStatut(dto.getStatut());
        if (dto.getLocalisation() != null) offre.setLocalisation(dto.getLocalisation());
        if (dto.getTypeOffre() != null) offre.setTypeOffre(TypeOffre.valueOf(dto.getTypeOffre()));

        OffreEmploi saved = offreRepository.save(offre);

        List<OffreSiteExterne> liens = offreSiteExterneRepo.findByOffre(saved);
        for (OffreSiteExterne ose : liens) {
            SiteExterne site = ose.getSiteExterne();
            boolean success = diffuserOffre(saved, site);
            ose.setStatutDiffusion(success ? "SUCCES" : "ECHEC");
            ose.setDateDiffusion(LocalDateTime.now());
            offreSiteExterneRepo.save(ose);
        }

        return mapToDTO(saved);
    }




    private OffreEmploiDTO mapToDTO(OffreEmploi offre) {
        OffreEmploiDTO dto = new OffreEmploiDTO();
        dto.setId(offre.getId());
        dto.setTitre(offre.getTitre());
        dto.setDescription(offre.getDescription());
        dto.setExigences(offre.getExigences());
        dto.setDatePublication(offre.getDatePublication());
        dto.setDateExpiration(offre.getDateExpiration());
        dto.setStatut(offre.getStatut());
        dto.setLocalisation(offre.getLocalisation());
        dto.setTypeOffre(offre.getTypeOffre() != null ? offre.getTypeOffre().name() : null);
        dto.setRecruteurEmail(offre.getRecruteur() != null ? offre.getRecruteur().getEmail() : null);
        return dto;
    }

    public List<CanDTO> getCandidaturesForOffre(Long offreId) {
        List<Candidature> candidatures = candidatureRepo.findByOffreId(offreId);

        return candidatures.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    private CanDTO convertToDTO(Candidature candidature) {
        CanDTO dto = new CanDTO();
        dto.setId(candidature.getId());
        dto.setDateSoumission(candidature.getDateSoumission());
        dto.setStatut(candidature.getStatut());
        dto.setCommentaire(candidature.getCommentaire());
        dto.setCandidatNom(candidature.getCandidatNom());
        dto.setCandidatPrenom(candidature.getCandidatPrenom());
        dto.setCandidatEmail(candidature.getCandidatEmail());

        // Informations sur le CV
        if (candidature.getCv() != null) {
            CV cv = candidature.getCv();
            dto.setCvOriginalFilename(cv.getOriginalFilename());
            dto.setCvContentType(cv.getContentType());
            dto.setCvSize(cv.getSize());
            dto.setCvDateUpload(cv.getDateUpload() != null ?
                    cv.getDateUpload().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime() : null);
            dto.setCvContenuTexte(cv.getContenuTexte());
            dto.setHasCvText(cv.getContenuTexte() != null && !cv.getContenuTexte().trim().isEmpty());
        }

        // Informations sur l'offre
        if (candidature.getOffre() != null) {
            dto.setOffreId(candidature.getOffre().getId());
            dto.setOffreTitre(candidature.getOffre().getTitre());
        }

        return dto;
    }
}

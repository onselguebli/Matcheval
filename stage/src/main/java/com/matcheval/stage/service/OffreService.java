package com.matcheval.stage.service;

import com.matcheval.stage.dto.CandidatureExterne;
import com.matcheval.stage.model.*;
import com.matcheval.stage.interfaces.IOffreService;
import com.matcheval.stage.repo.CandidatureRepo;
import com.matcheval.stage.repo.OffreRepo;
import com.matcheval.stage.repo.SiteExterneRepo;
import com.matcheval.stage.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
@Service
public class OffreService implements IOffreService {
    private final RestTemplate restTemplate = new RestTemplate();
    @Autowired
    private OffreRepo offreRepository;


    @Autowired
    private SiteExterneRepo siteRepo;
    @Autowired
    private CandidatureRepo candidatureRepo;

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
        }

        return savedOffre;
    }
    public void extraireCandidaturesDepuisSitesExternes(OffreEmploi offre) {
        List<SiteExterne> sites = siteRepo.findAll();

        for (SiteExterne site : sites) {
            String url = site.getApiConfig() + "/candidatures?offreId=" + offre.getId();

            try {
                ResponseEntity<CandidatureExterne[]> response = restTemplate.getForEntity(url, CandidatureExterne[].class);
                CandidatureExterne[] donnees = response.getBody();

                if (donnees == null) continue;

                for (CandidatureExterne externe : donnees) {
                    boolean dejaImportee = candidatureRepo.existsByCandidatEmailAndOffreId(externe.getEmail(), offre.getId());
                    if (dejaImportee) continue;

                    Candidature candidature = new Candidature();
                    candidature.setOffre(offre);
                    candidature.setDateSoumission(LocalDateTime.now());
                    candidature.setStatut("En attente");
                    candidature.setCommentaire(externe.getCommentaire());
                    candidature.setCandidatNom(externe.getNom());
                    candidature.setCandidatPrenom(externe.getPrenom());
                    candidature.setCandidatEmail(externe.getEmail());

                    // Création de l'objet CV
                    CV cv = new CV();
                    cv.setContenuTexte(externe.getCv().getContenuTexte());
                    cv.setFormat(externe.getCv().getFormat());
                    cv.setDateUpload(String.valueOf(LocalDate.parse(externe.getCv().getDateUpload()))); // conversion date
                    cv.setCandidature(candidature); // relation inverse

                    candidature.setCv(cv);

                    candidatureRepo.save(candidature);}

                System.out.println("✅ Candidatures extraites depuis " + site.getNom());

            } catch (Exception e) {
                System.err.println("❌ Erreur d'extraction sur " + site.getNom() + " : " + e.getMessage());
            }
        }
    }

}

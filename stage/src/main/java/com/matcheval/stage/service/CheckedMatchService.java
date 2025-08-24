// src/main/java/com/matcheval/stage/service/CheckedMatchService.java
package com.matcheval.stage.service;

import com.matcheval.stage.dto.CheckedMatchDTO;
import com.matcheval.stage.model.*;
import com.matcheval.stage.repo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckedMatchService {

    @Autowired
    CheckedMatchRepo checkedMatchRepo;
    @Autowired
    CandidatureRepo candidatureRepo;
    @Autowired
    OffreRepo offreRepo;
    @Autowired
    UserRepo usersRepo; // ton repo Users

    public CheckedMatchDTO addCheckByEmail(
            Long offreId, Long candidatureId, String recruteurEmail,
            Double scoreOverall, String filenameSnapshot) {

        OffreEmploi offre = offreRepo.findById(offreId)
                .orElseThrow(() -> new RuntimeException("Offre non trouvée"));

        Candidature cand = candidatureRepo.findById(candidatureId)
                .orElseThrow(() -> new RuntimeException("Candidature non trouvée"));

        Users recruteur = usersRepo.findByEmail(recruteurEmail);
        if (recruteur == null) {
            throw new RuntimeException("Recruteur introuvable avec email: " + recruteurEmail);
        }


        CheckedMatch cm = new CheckedMatch();
        cm.setOffre(offre);
        cm.setCandidature(cand);
        cm.setRecruteur(recruteur);
        cm.setManager(recruteur.getManager());
        cm.setScoreOverall(scoreOverall);
        cm.setFilenameSnapshot(filenameSnapshot);

        cm = checkedMatchRepo.save(cm);
        return toDto(cm);
    }

    // ------- LISTES PAR EMAIL (au lieu d'IDs) --------

    public List<CheckedMatchDTO> listForRecruteurByEmail(String recruteurEmail) {
        return checkedMatchRepo.findByRecruteur_EmailOrderByCreatedAtDesc(recruteurEmail)
                .stream().map(this::toDto).toList();
    }

    public List<CheckedMatchDTO> listForManagerByEmail(String managerEmail, String maybeRecruteurEmail) {
        var list = (maybeRecruteurEmail != null && !maybeRecruteurEmail.isBlank())
                ? checkedMatchRepo.findAllForManagerAndRecruteurEmail(managerEmail, maybeRecruteurEmail)
                : checkedMatchRepo.findAllForManagerEmail(managerEmail);
        return list.stream().map(this::toDto).toList();
    }

    public void deleteCheck(Long checkId) {
        checkedMatchRepo.deleteById(checkId);
    }

    private CheckedMatchDTO toDto(CheckedMatch cm) {
        return new CheckedMatchDTO(
                cm.getId(),
                cm.getOffre() != null ? cm.getOffre().getId() : null,
                cm.getOffre() != null ? cm.getOffre().getTitre() : null,
                cm.getCandidature() != null ? cm.getCandidature().getId() : null,
                cm.getCandidature() != null ? cm.getCandidature().getCandidatNom() : null,
                cm.getCandidature() != null ? cm.getCandidature().getCandidatPrenom() : null,
                cm.getCandidature() != null ? cm.getCandidature().getCandidatEmail() : null,
                cm.getRecruteur() != null ? cm.getRecruteur().getEmail() : null,
                cm.getScoreOverall(),
                cm.getFilenameSnapshot(),
                cm.getCreatedAt()  // assure-toi que CheckedMatch.setCreatedAt est bien set (Instant.now() / LocalDateTime.now())
        );
    }
}

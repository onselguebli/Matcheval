package com.matcheval.stage.service;

import com.matcheval.stage.dto.CandidatureDTO;
import com.matcheval.stage.interfaces.ICandidatureService;
import com.matcheval.stage.model.Candidature;
import com.matcheval.stage.repo.CandidatureRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CandidatureService implements ICandidatureService {
@Autowired
    CandidatureRepo candidatureRepo;
    @Override
    public ResponseEntity<CandidatureDTO> getCandidatureById(Long id) {
        Optional<Candidature> candidatureOpt = candidatureRepo.findById(id);

        if (candidatureOpt.isPresent()) {
            Candidature candidature = candidatureOpt.get();

            CandidatureDTO dto = new CandidatureDTO();
            dto.setId(candidature.getId());
            dto.setDateSoumission(candidature.getDateSoumission());
            dto.setStatut(candidature.getStatut());
            dto.setCommentaire(candidature.getCommentaire());
            dto.setCandidatNom(candidature.getCandidatNom());
            dto.setCandidatPrenom(candidature.getCandidatPrenom());
            dto.setCandidatEmail(candidature.getCandidatEmail());
            dto.setTitreOffre(candidature.getOffre().getTitre());

            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.notFound().build();
        }

    }

    @Override
    public CandidatureDTO updateCandidature(Long id, CandidatureDTO updatedData) {
        Candidature candidature = candidatureRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidature non trouvée"));


        if (updatedData.getStatut() != null) {
            candidature.setStatut(updatedData.getStatut());
            candidatureRepo.save(candidature);
        }
        return updatedData;
    }
}

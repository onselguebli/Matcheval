package com.matcheval.stage.service;

import com.matcheval.stage.dto.CandidatureDTO;
import com.matcheval.stage.interfaces.ICandidatureService;
import com.matcheval.stage.model.Candidature;
import com.matcheval.stage.repo.CandidatureRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class CandidatureService implements ICandidatureService {
@Autowired
    CandidatureRepo candidatureRepo;
    // CandidatureService.java
    @Override
    public ResponseEntity<CandidatureDTO> getCandidatureById(Long id) {
        return candidatureRepo.findById(id)
                .map(c -> {
                    CandidatureDTO dto = new CandidatureDTO();
                    dto.setId(c.getId());
                    dto.setDateSoumission(c.getDateSoumission());
                    dto.setStatut(c.getStatut());
                    dto.setCommentaire(c.getCommentaire());
                    dto.setCandidatNom(c.getCandidatNom());
                    dto.setCandidatPrenom(c.getCandidatPrenom());
                    dto.setCandidatEmail(c.getCandidatEmail());
                    dto.setTitreOffre(c.getOffre().getTitre());

                    if (c.getCv() != null && c.getCv().getData() != null) {
                        dto.setHasCv(true);
                        dto.setCvFilename(c.getCv().getOriginalFilename());
                        dto.setCvContentType(c.getCv().getContentType());
                        dto.setCvSize(c.getCv().getSize());
                    } else {
                        dto.setHasCv(false);
                    }
                    return ResponseEntity.ok(dto);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @Override
    public ByteArrayResource getCvFile(Long candidatureId) {
        Candidature candidature = candidatureRepo.findById(candidatureId)
                .orElseThrow(() -> new RuntimeException("Candidature non trouvée"));

        if (candidature.getCv() == null || candidature.getCv().getData() == null) {
            throw new RuntimeException("CV non trouvé pour cette candidature");
        }

        return new ByteArrayResource(candidature.getCv().getData());
    }
    @Override
    public String getCvContentType(Long candidatureId) {
        return candidatureRepo.findById(candidatureId)
                .map(c -> c.getCv() != null ? c.getCv().getContentType() : "application/octet-stream")
                .orElse("application/octet-stream");
    }
    @Override
    public String getCvFilename(Long candidatureId) {
        return candidatureRepo.findById(candidatureId)
                .map(c -> c.getCv() != null ? c.getCv().getOriginalFilename() : "cv.pdf")
                .orElse("cv.pdf");
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

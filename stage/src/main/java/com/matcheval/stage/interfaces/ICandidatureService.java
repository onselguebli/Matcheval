package com.matcheval.stage.interfaces;

import com.matcheval.stage.dto.CandidatureDTO;
import com.matcheval.stage.model.Candidature;
import org.springframework.http.ResponseEntity;

public interface ICandidatureService {

    ResponseEntity<CandidatureDTO> getCandidatureById(Long id);
    public  CandidatureDTO updateCandidature(Long id, CandidatureDTO updatedData);
}

package com.matcheval.stage.interfaces;

import com.matcheval.stage.dto.CandidatureDTO;
import com.matcheval.stage.model.Candidature;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;

public interface ICandidatureService {

    ResponseEntity<CandidatureDTO> getCandidatureById(Long id);

    ByteArrayResource getCvFile(Long candidatureId);

    String getCvContentType(Long candidatureId);

    String getCvFilename(Long candidatureId);

    public  CandidatureDTO updateCandidature(Long id, CandidatureDTO updatedData);
}

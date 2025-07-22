package com.matcheval.stage.repo;

import com.matcheval.stage.model.Candidature;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CandidatureRepo extends JpaRepository<Candidature,Long> {
    boolean existsByCandidatEmailAndOffreId(String email, Long id);
    List<Candidature> findByOffreId(Long offreId);

}


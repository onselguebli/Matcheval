package com.matcheval.stage.repo;

import com.matcheval.stage.model.OffreEmploi;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OffreRepo extends JpaRepository<OffreEmploi,Long> {
}

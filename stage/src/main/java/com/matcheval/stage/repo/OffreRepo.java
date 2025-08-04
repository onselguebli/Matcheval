package com.matcheval.stage.repo;

import com.matcheval.stage.dto.RecruteurOffreStatDTO;
import com.matcheval.stage.dto.SiteStatsDTO;
import com.matcheval.stage.model.OffreEmploi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OffreRepo extends JpaRepository<OffreEmploi,Long> {

    @Query("SELECT FUNCTION('DATE', o.datePublication), COUNT(o) FROM OffreEmploi o GROUP BY FUNCTION('DATE', o.datePublication) ORDER BY FUNCTION('DATE', o.datePublication)")
    List<Object[]> countByDay();

    @Query("SELECT EXTRACT(MONTH FROM o.datePublication), COUNT(o) FROM OffreEmploi o GROUP BY EXTRACT(MONTH FROM o.datePublication) ORDER BY EXTRACT(MONTH FROM o.datePublication)")
    List<Object[]> countByMonth();

    @Query("SELECT EXTRACT(YEAR FROM o.datePublication), COUNT(o) FROM OffreEmploi o GROUP BY EXTRACT(YEAR FROM o.datePublication) ORDER BY EXTRACT(YEAR FROM o.datePublication)")
    List<Object[]> countByYear();


    @Query("SELECT new com.matcheval.stage.dto.RecruteurOffreStatDTO(o.recruteur.email, COUNT(o)) " +
            "FROM OffreEmploi o GROUP BY o.recruteur.email")
    List<RecruteurOffreStatDTO> countOffresByRecruteur();



}

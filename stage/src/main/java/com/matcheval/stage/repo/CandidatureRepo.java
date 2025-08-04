package com.matcheval.stage.repo;

import com.matcheval.stage.dto.SiteStatsDTO;
import com.matcheval.stage.model.Candidature;
import com.matcheval.stage.model.OffreSiteExterne;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CandidatureRepo extends JpaRepository<Candidature,Long> {
    boolean existsByCandidatEmailAndOffreId(String email, Long id);
    List<Candidature> findByOffreId(Long offreId);
    @Query("SELECT EXTRACT(MONTH FROM c.dateSoumission) as month, COUNT(c) " +
            "FROM Candidature c " +
            "GROUP BY EXTRACT(MONTH FROM c.dateSoumission)")
    List<Object[]> countCandidaturesByMonth();

    @Query("""
    SELECT new com.matcheval.stage.dto.SiteStatsDTO(
        s.id, s.nom, COUNT(DISTINCT ose.offre.id), COUNT(c.id)
    )
    FROM OffreSiteExterne ose
    JOIN ose.siteExterne s
    LEFT JOIN ose.offre o
    LEFT JOIN o.candidatures c
    GROUP BY s.id, s.nom
""")
    long countBySourceSiteIn(List<OffreSiteExterne> offresExternes);




}


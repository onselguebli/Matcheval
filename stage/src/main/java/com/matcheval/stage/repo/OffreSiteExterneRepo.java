package com.matcheval.stage.repo;

import com.matcheval.stage.dto.SiteStatsDTO;
import com.matcheval.stage.model.OffreEmploi;
import com.matcheval.stage.model.OffreSiteExterne;
import com.matcheval.stage.model.SiteExterne;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OffreSiteExterneRepo extends JpaRepository<OffreSiteExterne,Long> {
    List<OffreSiteExterne> findBySiteExterne(SiteExterne site);

    Optional<OffreSiteExterne> findByOffreAndSiteExterne(OffreEmploi offre, SiteExterne site);

    @Query("""
SELECT new com.matcheval.stage.dto.SiteStatsDTO(
    s.id,
    s.nom,
    (SELECT COUNT(ose) * 1L 
     FROM OffreSiteExterne ose 
     WHERE ose.siteExterne.id = s.id),
     
    (SELECT COUNT(c) * 1L 
     FROM Candidature c 
     WHERE c.sourceSite.siteExterne.id = s.id 
       AND c.sourceSite.offre.id = c.offre.id)
)
FROM SiteExterne s
""")

    List<SiteStatsDTO> getStatsBySite();
}

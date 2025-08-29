package com.matcheval.stage.repo;

import com.matcheval.stage.dto.SiteStatsDTO;
import com.matcheval.stage.model.OffreEmploi;
import com.matcheval.stage.model.OffreSiteExterne;
import com.matcheval.stage.model.SiteExterne;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    List<OffreSiteExterne> findByOffre(OffreEmploi saved);

    @Query(value = """
    SELECT COALESCE(s.nom, 'Inconnu') AS label, COUNT(*) AS count
    FROM candidature c
    LEFT JOIN offre_site_externe ose ON c.source_site_id = ose.id
    LEFT JOIN site_externe s ON ose.site_externe_id = s.id
    JOIN offre_emploi o ON c.offre_id = o.id
    JOIN users u ON o.recruteur_id = u.id
    WHERE u.email = :email
      AND c.date_soumission >= (now() - (:days || ' days')::interval)
    GROUP BY label
    ORDER BY count DESC
  """, nativeQuery = true)
    List<Object[]> candidaturesBySource(@Param("email") String email, @Param("days") int days);
}

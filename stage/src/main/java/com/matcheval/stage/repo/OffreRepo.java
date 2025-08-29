package com.matcheval.stage.repo;

import com.matcheval.stage.dto.OffreWithCandidaturesDTO;
import com.matcheval.stage.dto.RecruteurOffreStatDTO;
import com.matcheval.stage.dto.SiteStatsDTO;
import com.matcheval.stage.model.OffreEmploi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT o FROM OffreEmploi o LEFT JOIN FETCH o.candidatures WHERE o.recruteur.email = :recruteurEmail")
    List<OffreEmploi> findOffresWithCandidaturesByRecruteur(@Param("recruteurEmail") String recruteurEmail);

    List<OffreEmploi> findByRecruteurEmail(String email);

    @Query("SELECT o.typeOffre, COUNT(o) FROM OffreEmploi o GROUP BY o.typeOffre")
    List<Object[]> countOffresByType();

    @Query(value = """
    SELECT o.statut AS label, COUNT(*) AS count
    FROM offre_emploi o
    JOIN users u ON o.recruteur_id = u.id
    WHERE u.email = :email
    GROUP BY o.statut
  """, nativeQuery = true)
    List<Object[]> countOffersByStatut(@Param("email") String email);

    @Query(value = """
    SELECT o.type_offre AS label, COUNT(*) AS count
    FROM offre_emploi o
    JOIN users u ON o.recruteur_id = u.id
    WHERE u.email = :email
    GROUP BY o.type_offre
  """, nativeQuery = true)
    List<Object[]> countOffersByType(@Param("email") String email);

    @Query(value = """
    SELECT COUNT(*)
    FROM offre_emploi o
    JOIN users u ON o.recruteur_id = u.id
    WHERE u.email = :email
  """, nativeQuery = true)
    long totalOffers(@Param("email") String email);

    @Query(value = """
    SELECT COUNT(*)
    FROM offre_emploi o
    JOIN users u ON o.recruteur_id = u.id
    WHERE u.email = :email
      AND (
        (o.date_expiration IS NOT NULL AND o.date_publication IS NOT NULL
         AND now() BETWEEN o.date_publication AND o.date_expiration)
        OR UPPER(o.statut) = 'ACTIVE'
      )
  """, nativeQuery = true)
    long activeOffers(@Param("email") String email);

    @Query(value = """
    SELECT COUNT(*)
    FROM offre_emploi o
    JOIN users u ON o.recruteur_id = u.id
    WHERE u.email = :email
      AND (
        (o.date_expiration IS NOT NULL AND now() > o.date_expiration)
        OR UPPER(o.statut) IN ('EXPIRED','ARCHIVED')
      )
  """, nativeQuery = true)
    long expiredOffers(@Param("email") String email);

    @Query(value = """
    SELECT o.id, o.titre, o.date_publication, o.statut, COUNT(c.id) AS candidates
    FROM offre_emploi o
    JOIN users u ON o.recruteur_id = u.id
    LEFT JOIN candidature c ON c.offre_id = o.id
    WHERE u.email = :email
    GROUP BY o.id, o.titre, o.date_publication, o.statut
    ORDER BY candidates DESC
    LIMIT :limit
  """, nativeQuery = true)
    List<Object[]> topOffersByCandidates(@Param("email") String email, @Param("limit") int limit);

    // Moyenne de candidats par offre
    @Query(value = """
    SELECT AVG(cnt)::float
    FROM (
      SELECT COUNT(c.id) AS cnt
      FROM offre_emploi o
      JOIN users u ON o.recruteur_id = u.id
      LEFT JOIN candidature c ON c.offre_id = o.id
      WHERE u.email = :email
      GROUP BY o.id
    ) t
  """, nativeQuery = true)
    Double avgCandidatesPerOffer(@Param("email") String email);

    // Moyenne (jours) jusqu'à la première candidature
    @Query(value = """
    SELECT AVG(diff_days)::float
    FROM (
      SELECT EXTRACT(EPOCH FROM (MIN(c.date_soumission) - o.date_publication))/86400.0 AS diff_days
      FROM offre_emploi o
      JOIN users u ON o.recruteur_id = u.id
      JOIN candidature c ON c.offre_id = o.id
      WHERE u.email = :email
      GROUP BY o.id
      HAVING MIN(c.date_soumission) IS NOT NULL AND o.date_publication IS NOT NULL
    ) t
  """, nativeQuery = true)
    Double avgDaysToFirstCandidate(@Param("email") String email);

    ///concernant dashboard de manager :
    @Query("""
  select count(o) from OffreEmploi o
  where o.recruteur.manager.email = :managerEmail
""")
    long countAllForManager(String managerEmail);

    @Query("""
  select count(o) from OffreEmploi o
  where o.recruteur.manager.email = :managerEmail and o.statut = 'active'
""")
    long countActiveForManager(String managerEmail);

    @Query("""
  select count(o) from OffreEmploi o
  where o.recruteur.manager.email = :managerEmail and o.statut = 'expired'
""")
    long countExpiredForManager(String managerEmail);

    @Query("""
  select new com.matcheval.stage.dto.managerDTOS.OfferTopDTO(
    o.id, o.titre, o.datePublication, o.statut, count(c)
  )
  from OffreEmploi o left join o.candidatures c
  where o.recruteur.manager.email = :managerEmail
  group by o.id, o.titre, o.datePublication, o.statut
  order by count(c) desc
""")
    List<com.matcheval.stage.dto.managerDTOS.OfferTopDTO> topOffers(
            String managerEmail,
            org.springframework.data.domain.Pageable pageable
    );

}



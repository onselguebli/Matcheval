package com.matcheval.stage.repo;

import com.matcheval.stage.dto.SiteStatsDTO;
import com.matcheval.stage.dto.SiteTypeCountDTO;
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
        select new com.matcheval.stage.dto.SiteTypeCountDTO(se.nom, o.typeOffre, count(c))
        from Candidature c
           join c.offre o
           join c.sourceSite ose
           join ose.siteExterne se
        group by se.nom, o.typeOffre
        order by se.nom asc, o.typeOffre asc
    """)
    List<SiteTypeCountDTO> countBySiteAndType();
    @Query("""
        select new com.matcheval.stage.dto.SiteTypeCountDTO(se.nom, o.typeOffre, count(c))
        from Candidature c
           join c.offre o
           join o.recruteur r
           join c.sourceSite ose
           join ose.siteExterne se
        where r.email = :recruteurEmail
        group by se.nom, o.typeOffre
        order by se.nom asc, o.typeOffre asc
    """)
    List<SiteTypeCountDTO> countBySiteAndTypeForRecruteur(String recruteurEmail);
    @Query("""
    select o.typeOffre as type, count(c) as cnt
    from Candidature c
      join c.offre o
    group by o.typeOffre
    order by o.typeOffre
  """)
    List<Object[]> countCandidaturesByType();

}


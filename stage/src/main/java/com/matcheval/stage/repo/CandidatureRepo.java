package com.matcheval.stage.repo;

import com.matcheval.stage.dto.SiteTypeCountDTO;
import com.matcheval.stage.model.Candidature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query(value = """
    SELECT COUNT(*)
    FROM candidature c
    JOIN offre_emploi o ON c.offre_id = o.id
    JOIN users u ON o.recruteur_id = u.id
    WHERE u.email = :email
  """, nativeQuery = true)
    long totalCandidatures(@Param("email") String email);

    @Query(value = """
    SELECT c.statut AS label, COUNT(*) AS count
    FROM candidature c
    JOIN offre_emploi o ON c.offre_id = o.id
    JOIN users u ON o.recruteur_id = u.id
    WHERE u.email = :email
    GROUP BY c.statut
  """, nativeQuery = true)
    List<Object[]> countCandidaturesByStatut(@Param("email") String email);

    @Query(value = """
    SELECT to_char(c.date_soumission, 'YYYY-MM') AS period, COUNT(*) AS count
    FROM candidature c
    JOIN offre_emploi o ON c.offre_id = o.id
    JOIN users u ON o.recruteur_id = u.id
    WHERE u.email = :email
      AND EXTRACT(YEAR FROM c.date_soumission) = :year
    GROUP BY period
    ORDER BY period
  """, nativeQuery = true)
    List<Object[]> candidaturesMonthly(@Param("email") String email, @Param("year") int year);
///for manager dashboard
@Query("""
    select count(c) from Candidature c
    where c.offre.recruteur.manager.email = :managerEmail
  """)
long countAllForManager(String managerEmail);

    // ✅ 1 ligne, 3 colonnes (JPQL)
    @Query("""
    select
      coalesce(sum(case when c.statut='PENDING'  then 1 else 0 end), 0),
      coalesce(sum(case when c.statut='ACCEPTED' then 1 else 0 end), 0),
      coalesce(sum(case when c.statut='REJECTED' then 1 else 0 end), 0)
    from Candidature c
    where c.offre.recruteur.manager.email = :managerEmail
  """)
    Object[] pipelineGlobal(String managerEmail);

    // ✅ mensuel (Postgres)
    @Query(value = """
    select to_char(date_trunc('month', c.date_soumission), 'YYYY-MM') as period,
           count(*) as cnt
    from candidature c
    join offre_emploi o on c.offre_id = o.id
    join users r on o.recruteur_id = r.id
    join users m on r.manager_id = m.id
    where m.email = :managerEmail
      and extract(year from c.date_soumission) = :year
    group by 1
    order by 1
  """, nativeQuery = true)
    List<Object[]> monthly(String managerEmail, int year);

    // ✅ source (Postgres interval)
    @Query(value = """
    select coalesce(se.nom, 'Inconnu') as label, count(*) as cnt
    from candidature c
    join offre_emploi o on c.offre_id = o.id
    join users r on o.recruteur_id = r.id
    join users m on r.manager_id = m.id
    left join offre_site_externe ose on c.source_site_id = ose.id
    left join site_externe se on ose.site_externe_id = se.id
    where m.email = :managerEmail
      and c.date_soumission >= now() - (:days || ' days')::interval
    group by 1
    order by 2 desc
  """, nativeQuery = true)
    List<Object[]> bySource(String managerEmail, int days);

    // ✅ par recruteur (derniers X jours)
    @Query(value = """
    select r.email as recruiter, count(*) as cnt
    from candidature c
    join offre_emploi o on c.offre_id = o.id
    join users r on o.recruteur_id = r.id
    join users m on r.manager_id = m.id
    where m.email = :managerEmail
      and c.date_soumission >= now() - (:days || ' days')::interval
    group by r.email
    order by cnt desc
  """, nativeQuery = true)
    List<Object[]> byRecruiterLastDays(String managerEmail, int days);

    // ✅ pipeline par recruteur (Postgres)
    @Query(value = """
    select r.email,
      sum(case when c.statut='PENDING' then 1 else 0 end)   as pending,
      sum(case when c.statut='ACCEPTED' then 1 else 0 end)  as accepted,
      sum(case when c.statut='REJECTED' then 1 else 0 end)  as rejected,
      count(*)                                             as total
    from candidature c
    join offre_emploi o on c.offre_id = o.id
    join users r on o.recruteur_id = r.id
    join users m on r.manager_id = m.id
    where m.email = :managerEmail
      and c.date_soumission >= now() - (:days || ' days')::interval
    group by r.email
    order by total desc
  """, nativeQuery = true)
    List<Object[]> pipelineByRecruiter(String managerEmail, int days);

    // ✅ avg jours jusqu'au 1er candidat (Postgres)
    @Query(value = """
    select avg(extract(epoch from (min_cand - date_publication))/86400.0) as avg_days
    from (
      select o.id, o.date_publication,
             (select min(c.date_soumission)
              from candidature c where c.offre_id = o.id) as min_cand
      from offre_emploi o
      join users r on o.recruteur_id = r.id
      join users m on r.manager_id = m.id
      where m.email = :managerEmail
    ) t
    where min_cand is not null
  """, nativeQuery = true)
    Double avgDaysToFirstCandidate(String managerEmail);

    // ✅ avg candidats / offre (Postgres)
    @Query(value = """
    select coalesce(avg(cand_cnt), 0)
    from (
      select o.id, (select count(*) from candidature c where c.offre_id = o.id) as cand_cnt
      from offre_emploi o
      join users r on o.recruteur_id = r.id
      join users m on r.manager_id = m.id
      where m.email = :managerEmail
    ) x
  """, nativeQuery = true)
    Double avgCandidatesPerOffer(String managerEmail);
}


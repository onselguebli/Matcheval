package com.matcheval.stage.repo;

import com.matcheval.stage.model.CheckedMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CheckedMatchRepo extends JpaRepository<CheckedMatch, Long> {

    // Liste du recruteur par email
    List<CheckedMatch> findByRecruteur_EmailOrderByCreatedAtDesc(String email);

    @Query("""
    select cm from CheckedMatch cm
    where cm.manager.email = :managerEmail
    order by cm.createdAt desc
  """)
    List<CheckedMatch> findAllForManagerEmail(String managerEmail);

    @Query("""
    select cm from CheckedMatch cm
    where cm.manager.email = :managerEmail
      and cm.recruteur.email = :recruteurEmail
    order by cm.createdAt desc
  """)
    List<CheckedMatch> findAllForManagerAndRecruteurEmail(String managerEmail, String recruteurEmail);

    // ✅ Postgres-safe: on passe un since (LocalDateTime) en paramètre
    @Query("""
    select count(cm) from CheckedMatch cm
    where cm.recruteur.manager.email = :managerEmail
      and cm.createdAt >= :since
  """)
    long countLast30ForManagerSince(String managerEmail, java.time.LocalDateTime since);

    // ✅ Top recruteurs: préfère la pagination plutôt que LIMIT :limit
    @Query(value = """
    select r.email, count(*) as cnt
    from checked_match cm
    join users r on cm.recruteur_id = r.id
    join users m on r.manager_id = m.id
    where m.email = :managerEmail
    group by r.email
    order by cnt desc
  """, nativeQuery = true)
    List<Object[]> topRecruitersByChecked(String managerEmail, org.springframework.data.domain.Pageable pageable);
}

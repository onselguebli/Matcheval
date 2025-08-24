package com.matcheval.stage.repo;

import com.matcheval.stage.model.CheckedMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CheckedMatchRepo extends JpaRepository<CheckedMatch, Long> {

    // Liste du recruteur par email
    List<CheckedMatch> findByRecruteur_EmailOrderByCreatedAtDesc(String email);

    // Vue manager : tous les checks des recruteurs rattachés à ce manager (filtrés par email du manager)
    @Query("""
    SELECT cm FROM CheckedMatch cm
    WHERE cm.manager.email = :managerEmail
    ORDER BY cm.createdAt DESC
  """)
    List<CheckedMatch> findAllForManagerEmail(String managerEmail);

    // Vue manager + filtre sur un recruteur précis (par email)
    @Query("""
    SELECT cm FROM CheckedMatch cm
    WHERE cm.manager.email = :managerEmail
      AND cm.recruteur.email = :recruteurEmail
    ORDER BY cm.createdAt DESC
  """)
    List<CheckedMatch> findAllForManagerAndRecruteurEmail(String managerEmail, String recruteurEmail);
}

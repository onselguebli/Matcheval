package com.matcheval.stage.repo;

import com.matcheval.stage.model.Roles;
import com.matcheval.stage.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserRepo extends JpaRepository<Users,Long> {
    Users findByEmail(String email);

    List<Users> findByRole(Roles role);

    boolean existsByEmail(String email);

    List<Users> findByManagerId(Long managerId);

    @Query("SELECT u.role AS role, COUNT(u) AS count FROM Users u GROUP BY u.role")
    List<Object[]> countUsersByRole();

    @Query("SELECT YEAR(u.createdAt), COUNT(u) FROM Users u GROUP BY YEAR(u.createdAt)")
    List<Object[]> countUsersByYear();
    @Query("SELECT u.enabled, COUNT(u) FROM Users u GROUP BY u.enabled")
    List<Object[]> countUsersByEnabledStatus();
    @Query("SELECT u.civility, COUNT(u) FROM Users u GROUP BY u.civility")
    List<Object[]> countUsersByCivility();

    @Query("SELECT u.manager.email, COUNT(u) FROM Users u WHERE u.role = 'RECRUITER' GROUP BY u.manager.email")
    List<Object[]> countRecruteursPerManager();
    long countByRole(Roles role);

    @Query("SELECT EXTRACT(MONTH FROM u.createdAt) as month, COUNT(u) " +
            "FROM Users u " +
            "GROUP BY EXTRACT(MONTH FROM u.createdAt)")
    List<Object[]> countUsersByMonth();

    @Query("SELECT EXTRACT(MONTH FROM u.createdAt) as month, COUNT(u) " +
            "FROM Users u " +
            "WHERE u.role = :role " +
            "GROUP BY EXTRACT(MONTH FROM u.createdAt)")
    List<Object[]> countUsersByRolePerMonth(@Param("role") Roles role);



    @Query("SELECT FUNCTION('DATE', u.createdAt), COUNT(u) FROM Users u WHERE u.role = 'RECRUITER' GROUP BY FUNCTION('DATE', u.createdAt) ORDER BY FUNCTION('DATE', u.createdAt)")
    List<Object[]> countRecruteursByDay();

    @Query("SELECT EXTRACT(MONTH FROM u.createdAt), COUNT(u) FROM Users u WHERE u.role = 'RECRUITER' GROUP BY EXTRACT(MONTH FROM u.createdAt) ORDER BY EXTRACT(MONTH FROM u.createdAt)")
    List<Object[]> countRecruteursByMonth();

    @Query("SELECT EXTRACT(YEAR FROM u.createdAt), COUNT(u) FROM Users u WHERE u.role = 'RECRUITER' GROUP BY EXTRACT(YEAR FROM u.createdAt) ORDER BY EXTRACT(YEAR FROM u.createdAt)")
    List<Object[]> countRecruteursByYear();

    Long countByLastLoginBetween(LocalDateTime start, LocalDateTime end);
    @Query("""
  select count(u) from Users u
  where u.manager.email = :managerEmail and u.role = com.matcheval.stage.model.Roles.RECRUITER
""")
    long countRecruitersOf(String managerEmail);



}

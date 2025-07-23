package com.matcheval.stage.repo;

import com.matcheval.stage.model.Roles;
import com.matcheval.stage.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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



}

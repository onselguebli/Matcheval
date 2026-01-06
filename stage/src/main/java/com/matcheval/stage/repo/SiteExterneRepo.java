package com.matcheval.stage.repo;

import com.matcheval.stage.model.SiteExterne;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface SiteExterneRepo extends JpaRepository<SiteExterne,Long> {
    @Query("SELECT EXTRACT(MONTH FROM s.dateA) as month, COUNT(s) " +
            "FROM SiteExterne s " +
            "GROUP BY EXTRACT(MONTH FROM s.dateA)")
    List<Object[]> countSitesByMonth();
    boolean existsByNom(String nom);
    SiteExterne findByNom(String nom);

}

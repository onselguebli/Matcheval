package com.matcheval.stage.repo;

import com.matcheval.stage.model.CV;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface CvRepo extends JpaRepository<CV, Long> {
    @Query("SELECT FUNCTION('DATE', c.dateUpload), COUNT(c) FROM CV c GROUP BY FUNCTION('DATE', c.dateUpload) ORDER BY FUNCTION('DATE', c.dateUpload)")
    List<Object[]> countByDay();

    @Query("SELECT EXTRACT(month FROM c.dateUpload), COUNT(c) FROM CV c GROUP BY EXTRACT(month FROM c.dateUpload) ORDER BY EXTRACT(month FROM c.dateUpload)")
    List<Object[]> countByMonth();

    @Query("SELECT EXTRACT(year FROM c.dateUpload), COUNT(c) FROM CV c GROUP BY EXTRACT(year FROM c.dateUpload) ORDER BY EXTRACT(year FROM c.dateUpload)")
    List<Object[]> countByYear();
    Long countByDateUploadBetween(Date start, Date end);
}

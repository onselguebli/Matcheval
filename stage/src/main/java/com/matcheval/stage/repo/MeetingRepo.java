package com.matcheval.stage.repo;

import com.matcheval.stage.model.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface MeetingRepo extends JpaRepository<Meeting, Long> {
    List<Meeting> findByCreatedByOrderByStartAtDesc(String email);
    @Query("""
    select m from Meeting m
    where m.createdBy = :managerEmail and m.startAt >= :from
    order by m.startAt asc
  """)
    List<Meeting> upcoming(String managerEmail, Date from);
}


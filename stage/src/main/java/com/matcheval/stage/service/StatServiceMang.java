package com.matcheval.stage.service;

import com.matcheval.stage.dto.managerDTOS.*;
import com.matcheval.stage.interfaces.IStatServiceMang;
import com.matcheval.stage.model.Meeting;
import com.matcheval.stage.repo.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class StatServiceMang implements IStatServiceMang {

    @Autowired UserRepo usersRepo;
    @Autowired OffreRepo offreRepo;
    @Autowired CandidatureRepo candRepo;
    @Autowired CheckedMatchRepo checkedRepo;
    @Autowired MeetingRepo meetingRepo;

    public OverviewManagerDTO overview(String managerEmail) {
        OverviewManagerDTO dto = new OverviewManagerDTO();
        dto.setTotalRecruiters(usersRepo.countRecruitersOf(managerEmail));
        dto.setTotalOffers(offreRepo.countAllForManager(managerEmail));
        dto.setActiveOffers(offreRepo.countActiveForManager(managerEmail));
        dto.setExpiredOffers(offreRepo.countExpiredForManager(managerEmail));
        dto.setTotalCandidatures(candRepo.countAllForManager(managerEmail));

        Object[] pipe = candRepo.pipelineGlobal(managerEmail);
        if (pipe != null && pipe.length == 1 && pipe[0] instanceof Object[] inner) {
            pipe = inner; // robustesse si résultat imbriqué
        }
        if (pipe != null && pipe.length >= 3) {
            dto.setPending(  toLong(pipe[0]) );
            dto.setAccepted( toLong(pipe[1]) );
            dto.setRejected( toLong(pipe[2]) );
        }

        dto.setAvgCandidatesPerOffer(
                Optional.ofNullable(candRepo.avgCandidatesPerOffer(managerEmail)).orElse(0.0)
        );
        dto.setAvgDaysToFirstCandidate(
                Optional.ofNullable(candRepo.avgDaysToFirstCandidate(managerEmail)).orElse(0.0)
        );

        dto.setCheckedMatchesLast30(
                checkedRepo.countLast30ForManagerSince(managerEmail, java.time.LocalDateTime.now().minusDays(30))
        );

        return dto;
    }

    public List<MonthlyCountDTO> candidaturesMonthly(String email, int year) {
        return candRepo.monthly(email, year).stream()
                .map(a -> new MonthlyCountDTO(String.valueOf(a[0]), toLong(a, 1)))
                .collect(Collectors.toList());
    }

    public List<CountLabelDTO> candidaturesBySource(String email, int days) {
        return candRepo.bySource(email, days).stream()
                .map(a -> new CountLabelDTO(String.valueOf(a[0]), toLong(a, 1)))
                .collect(Collectors.toList());
    }

    public List<CountLabelDTO> candidaturesByRecruiter(String email, int days) {
        return candRepo.byRecruiterLastDays(email, days).stream()
                .map(a -> new CountLabelDTO(String.valueOf(a[0]), toLong(a, 1)))
                .collect(Collectors.toList());
    }

    public List<RecruiterPipelineDTO> pipelineByRecruiter(String email, int days) {
        return candRepo.pipelineByRecruiter(email, days).stream().map(a -> {
            RecruiterPipelineDTO d = new RecruiterPipelineDTO();
            d.setRecruiter(String.valueOf(a[0]));
            d.setPending(  toLong(a, 1) );
            d.setAccepted( toLong(a, 2) );
            d.setRejected( toLong(a, 3) );
            d.setTotal(    toLong(a, 4) );
            return d;
        }).collect(Collectors.toList());
    }

    public List<OfferTopDTO> topOffers(String email, int limit) {
        return offreRepo.topOffers(email, org.springframework.data.domain.PageRequest.of(0, limit));
    }

    public List<CountLabelDTO> topRecruitersByChecked(String email, int limit) {
        return checkedRepo.topRecruitersByChecked(email, org.springframework.data.domain.PageRequest.of(0, limit))
                .stream()
                .map(a -> new CountLabelDTO(String.valueOf(a[0]), toLong(a[1])))
                .collect(Collectors.toList());
    }

    public List<MeetingDTO> upcomingMeetings(String managerEmail, int days) {
        Date from = new Date();
        List<Meeting> meetings = meetingRepo.upcoming(managerEmail, from);
        Calendar cal = Calendar.getInstance();
        cal.setTime(from);
        cal.add(Calendar.DAY_OF_MONTH, days);
        Date to = cal.getTime();

        return meetings.stream()
                .filter(m -> m.getStartAt().before(to))
                .map(m -> {
                    MeetingDTO d = new MeetingDTO();
                    d.setId(m.getId());
                    d.setTitle(m.getTitle());
                    d.setRoomName(m.getRoomName());
                    d.setStartAt(m.getStartAt());
                    d.setDurationMin(m.getDurationMin());
                    return d;
                }).collect(Collectors.toList());
    }

    /** Conversions robustes */
    private long toLong(Object[] row, int idx) {
        if (row == null || row.length <= idx || row[idx] == null) return 0L;
        return toLong(row[idx]);
    }
    private long toLong(Object v) {
        if (v == null) return 0L;
        if (v instanceof Object[] arr) return arr.length > 0 ? toLong(arr[0]) : 0L;
        if (v instanceof Long l) return l;
        if (v instanceof Integer i) return i.longValue();
        if (v instanceof java.math.BigInteger bi) return bi.longValue();
        if (v instanceof java.math.BigDecimal bd) return bd.longValue();
        if (v instanceof Number n) return n.longValue();
        return Long.parseLong(v.toString());
    }
}


package com.matcheval.stage.service;

import com.matcheval.stage.dto.MeetingDTO;
import com.matcheval.stage.model.Meeting;
import com.matcheval.stage.repo.MeetingRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MeetingService {
    @Autowired
    MeetingRepo repo;

    public Meeting create(Meeting m) { return repo.save(m); }

    public List<Meeting> listMine(String email) {
        return repo.findByCreatedByOrderByStartAtDesc(email);
    }

    public Meeting get(Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Meeting not found"));
    }

    public MeetingDTO toDTO(Meeting m, String jitsiBase) {
        MeetingDTO dto = new MeetingDTO();
        dto.setId(m.getId());
        dto.setTitle(m.getTitle());
        dto.setRoomName(m.getRoomName());
        dto.setStartAt(m.getStartAt());
        dto.setDurationMin(m.getDurationMin());
        dto.setInviteLink(jitsiBase + "/" + m.getRoomName());
        // dto.setPassword(m.getPassword()); // si tu veux, mais évite de l’exposer à tous
        return dto;
    }
}

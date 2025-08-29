package com.matcheval.stage.controller;

import com.matcheval.stage.dto.MeetingDTO;
import com.matcheval.stage.model.Meeting;
import com.matcheval.stage.service.MeetingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("manager/meetings")
@RequiredArgsConstructor
public class MeetingController {
    @Autowired
    MeetingService service;

    private String getEmailFromSecurity() {
        // récupère l’email du JWT / SecurityContext
        return org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getName();
    }

    private static final String JITSI_BASE = "https://meet.jit.si"; // ou ton domaine

    @PostMapping
    public ResponseEntity<MeetingDTO> create(@RequestBody MeetingDTO input) {
        String email = getEmailFromSecurity();

        Meeting m = new Meeting();
        m.setTitle(input.getTitle());
        m.setRoomName(input.getRoomName());
        m.setDurationMin(input.getDurationMin());
        m.setCreatedBy(email);
        m.setPassword(input.getPassword());
        m.setStartAt(input.getStartAt() != null ? input.getStartAt() : new Date());

        Meeting saved = service.create(m);
        return ResponseEntity.ok(service.toDTO(saved, JITSI_BASE));
    }

    @GetMapping
    public ResponseEntity<List<MeetingDTO>> myMeetings() {
        String email = getEmailFromSecurity();
        List<MeetingDTO> list = service.listMine(email).stream()
                .map(m -> service.toDTO(m, JITSI_BASE))
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MeetingDTO> get(@PathVariable Long id) {
        Meeting m = service.get(id);
        return ResponseEntity.ok(service.toDTO(m, JITSI_BASE));
    }
}


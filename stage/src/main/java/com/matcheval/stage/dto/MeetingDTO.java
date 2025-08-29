package com.matcheval.stage.dto;

import lombok.Data;

import java.util.Date;

// MeetingDTO.java
@Data
public class MeetingDTO {
    private Long id;
    private String title;
    private String roomName;
    private Date startAt;
    private Integer durationMin;
    private String inviteLink; // construit côté service
    private String password;   // optionnel: à éviter en GET public
}


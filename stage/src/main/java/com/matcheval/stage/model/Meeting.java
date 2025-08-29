package com.matcheval.stage.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class Meeting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String roomName;
    private Date startAt;
    private Integer durationMin;
    private String createdBy;  // email manager
    private String password;   // optionnel (évite de la renvoyer côté front si sensible)
}


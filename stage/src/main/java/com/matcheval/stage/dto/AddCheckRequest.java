package com.matcheval.stage.dto;

import lombok.Data;

// CheckedMatchController.java
@Data
public class AddCheckRequest {
    private Long offreId;
    private Long candidatureId;
    private String recruteurEmail;
    private Double scoreOverall;
    private String filenameSnapshot;
}

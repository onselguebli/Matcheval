// src/main/java/com/matcheval/stage/dto/CheckedMatchDTO.java
package com.matcheval.stage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CheckedMatchDTO {
    private Long id;
    private Long offreId;
    private String offreTitre;

    private Long candidatureId;
    private String candidatNom;
    private String candidatPrenom;
    private String candidatEmail;

    private String recruteurEmail;
    private Double scoreOverall;
    private String filenameSnapshot;
    private LocalDateTime createdAt;
}

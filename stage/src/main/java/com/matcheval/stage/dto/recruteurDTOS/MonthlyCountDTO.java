package com.matcheval.stage.dto.recruteurDTOS;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class MonthlyCountDTO {
    private String period; // ex: "2025-08"
    private long count;
}
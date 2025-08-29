package com.matcheval.stage.dto.managerDTOS;

import lombok.Data;

@Data
public class RecruiterPerfDTO {
    private String recruiter; // email ou nom
    private long candidatures;
    private long checked;     // CheckedMatch
    private Double acceptanceRate; // accepted / total
}

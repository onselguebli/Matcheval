package com.matcheval.stage.dto.managerDTOS;

import lombok.Data;

@Data
public class RecruiterPipelineDTO {
    private String recruiter; // email ou nom
    private long pending;
    private long accepted;
    private long rejected;
    private long total;

}

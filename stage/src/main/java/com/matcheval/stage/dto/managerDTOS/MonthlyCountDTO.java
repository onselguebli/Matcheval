package com.matcheval.stage.dto.managerDTOS;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyCountDTO {
    private String period; // yyyy-MM
    private long count;
}

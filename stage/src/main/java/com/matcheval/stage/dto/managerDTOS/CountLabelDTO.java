package com.matcheval.stage.dto.managerDTOS;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CountLabelDTO {
    private String label;
    private long count;

}

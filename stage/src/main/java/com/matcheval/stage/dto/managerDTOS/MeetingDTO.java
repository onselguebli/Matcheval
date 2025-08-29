package com.matcheval.stage.dto.managerDTOS;

import lombok.Data;

import java.util.Date;

@Data
public class MeetingDTO {
    private Long id;
    private String title;
    private String roomName;
    private Date startAt;
    private Integer durationMin;

}

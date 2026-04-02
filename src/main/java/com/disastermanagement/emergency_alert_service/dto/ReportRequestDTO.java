package com.disastermanagement.emergency_alert_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportRequestDTO {

    private Long disasterId;
    private String details;
}
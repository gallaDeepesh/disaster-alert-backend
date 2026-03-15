package com.disastermanagement.emergency_alert_service.dto;

import lombok.*;

@Getter
@Setter
public class AssignTaskRequest {

    private Long disasterId;

    private Long responderId;

    private String description;

    public Long getDisasterId() {
        return disasterId;
    }

    public void setDisasterId(Long disasterId) {
        this.disasterId = disasterId;
    }

    public Long getResponderId() {
        return responderId;
    }

    public void setResponderId(Long responderId) {
        this.responderId = responderId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
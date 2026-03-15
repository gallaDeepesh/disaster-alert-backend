package com.disastermanagement.emergency_alert_service.dto;

import lombok.*;

@Getter
@Setter
public class AlertRequestDTO {

    private Long disasterId;
    private String message;
    private String region;

    public Long getDisasterId() {
        return disasterId;
    }

    public void setDisasterId(Long disasterId) {
        this.disasterId = disasterId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}

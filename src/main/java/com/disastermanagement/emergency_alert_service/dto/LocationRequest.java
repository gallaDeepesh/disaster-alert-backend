package com.disastermanagement.emergency_alert_service.dto;

public class LocationRequest {
    private Double latitude;
    private Double longitude;

    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }

    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
}

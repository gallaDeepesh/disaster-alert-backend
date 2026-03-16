package com.disastermanagement.emergency_alert_service.dto;

public class DashboardStats {

    private long disasters;
    private long alerts;
    private long tasks;
    private long responders;

    public DashboardStats(long disasters, long alerts, long tasks, long responders) {
        this.disasters = disasters;
        this.alerts = alerts;
        this.tasks = tasks;
        this.responders = responders;
    }

    public long getDisasters() { return disasters; }
    public long getAlerts() { return alerts; }
    public long getTasks() { return tasks; }
    public long getResponders() { return responders; }
}
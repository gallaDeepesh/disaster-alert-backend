package com.disastermanagement.emergency_alert_service.listeners;

import com.disastermanagement.emergency_alert_service.entity.DisasterAnalytics;
import com.disastermanagement.emergency_alert_service.event.DisasterCreatedEvent;
import com.disastermanagement.emergency_alert_service.repository.DisasterAnalyticsRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AnalyticsListener {


    private final DisasterAnalyticsRepository analyticsRepository;

    public AnalyticsListener(DisasterAnalyticsRepository analyticsRepository) {
        this.analyticsRepository = analyticsRepository;
    }

    @EventListener
    public void storeAnalytics(DisasterCreatedEvent event) {

        var disaster = event.getDisaster();

        DisasterAnalytics analytics = new DisasterAnalytics();

        analytics.setDisasterId(disaster.getId());
        analytics.setDisasterType(disaster.getType());
        analytics.setMagnitude(disaster.getMagnitude());
        analytics.setLocation(disaster.getLocation());
        analytics.setRespondersAssigned(1); // we assigned 1 responder
        analytics.setRecordedAt(LocalDateTime.now());

        analyticsRepository.save(analytics);

        System.out.println("Analytics recorded for disaster: " + disaster.getLocation());
    }
}

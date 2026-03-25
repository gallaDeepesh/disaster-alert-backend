package com.disastermanagement.emergency_alert_service.listeners;

import com.disastermanagement.emergency_alert_service.entity.Disaster;
import com.disastermanagement.emergency_alert_service.event.DisasterCreatedEvent;
import com.disastermanagement.emergency_alert_service.repository.UserRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener {
    private UserRepository userRepository;
    @EventListener
    public void sendNotification(DisasterCreatedEvent event) {

        Disaster disaster = event.getDisaster();

        System.out.println(
                "NOTIFICATION SERVICE: Sending alerts to citizens"
        );
    }
}

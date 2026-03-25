package com.disastermanagement.emergency_alert_service.listeners;

import com.disastermanagement.emergency_alert_service.entity.Alert;
import com.disastermanagement.emergency_alert_service.entity.User;
import com.disastermanagement.emergency_alert_service.event.DisasterCreatedEvent;
import com.disastermanagement.emergency_alert_service.repository.AlertRepository;
import com.disastermanagement.emergency_alert_service.repository.UserRepository;
import com.disastermanagement.emergency_alert_service.service.EmailService;
import com.disastermanagement.emergency_alert_service.utility.GeoUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import com.disastermanagement.emergency_alert_service.entity.Role;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DisasterAlertListener {
    private final UserRepository userRepository;
    private final AlertRepository alertRepository;
    private final EmailService emailService;

    public DisasterAlertListener(UserRepository userRepository, AlertRepository alertRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.alertRepository = alertRepository;
        this.emailService = emailService;
    }

    @EventListener
    public void handleDisasterDetected(DisasterCreatedEvent event) {

        var disaster = event.getDisaster();

        List<User> citizens = userRepository.findByRole(Role.CITIZEN);
        System.out.println("Total Citizens found: " + citizens.size());
        for (User user : citizens) {

            double distance = GeoUtils.calculateDistance(
                    user.getLatitude(),
                    user.getLongitude(),
                    disaster.getLatitude(),
                    disaster.getLongitude()
            );
            System.out.println("Checking user " + user.getEmail() + " | Distance: " + distance + " km");
            if (distance <= 50) { // 50km radius

                Alert alert = new Alert();
                alert.setUserId(user.getId());
                alert.setDisaster(disaster);
                alert.setMessage("⚠ Earthquake detected near your location: "
                        + disaster.getLocation());
                alert.setBroadcastTime(LocalDateTime.now());

                if(!alertRepository.existsByDisasterIdAndUserId(
                        disaster.getId(),
                        user.getId()
                )) {
                    alertRepository.save(alert);

                    System.out.println(
                            "Alert sent to user " + user.getEmail()
                    );
                    emailService.sendAlertEmail(
                            user.getEmail(),
                            "⚠ Disaster Alert near your location: "
                                    + disaster.getLocation()
                    );
                }
            }
        }
    }
}
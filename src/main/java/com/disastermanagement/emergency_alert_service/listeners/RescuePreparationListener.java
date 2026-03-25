package com.disastermanagement.emergency_alert_service.listeners;

import com.disastermanagement.emergency_alert_service.entity.RescueTask;
import com.disastermanagement.emergency_alert_service.entity.Role;
import com.disastermanagement.emergency_alert_service.entity.User;
import com.disastermanagement.emergency_alert_service.event.DisasterCreatedEvent;
import com.disastermanagement.emergency_alert_service.repository.RescueTaskRepository;
import com.disastermanagement.emergency_alert_service.repository.UserRepository;
import com.disastermanagement.emergency_alert_service.utility.GeoUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Component
public class RescuePreparationListener {

    private final UserRepository userRepository;
    private final RescueTaskRepository rescueTaskRepository;

    public RescuePreparationListener(UserRepository userRepository, RescueTaskRepository rescueTaskRepository) {
        this.userRepository = userRepository;
        this.rescueTaskRepository = rescueTaskRepository;
    }

    @EventListener
    public void prepareRescue(DisasterCreatedEvent event) {

        var disaster = event.getDisaster();

        List<User> responders =
                userRepository.findByRoleAndStatus(Role.RESPONDER, "AVAILABLE");

        if (responders.isEmpty()) {
            System.out.println("No responders available");
            return;
        }

        User nearestResponder = responders.stream()
                .min(Comparator.comparingDouble(responder ->
                        GeoUtils.calculateDistance(
                                responder.getLatitude(),
                                responder.getLongitude(),
                                disaster.getLatitude(),
                                disaster.getLongitude()
                        )
                ))
                .orElse(null);

        if (nearestResponder != null) {

            RescueTask task = new RescueTask();

            task.setDisaster(disaster);
            task.setResponder(nearestResponder);
            task.setStatus("ASSIGNED");
            task.setAssignedAt(LocalDateTime.now());


            rescueTaskRepository.save(task);

            System.out.println(
                    "Responder assigned: " + nearestResponder.getEmail()
            );

            nearestResponder.setStatus("BUSY");
            userRepository.save(nearestResponder);
        }
    }
}

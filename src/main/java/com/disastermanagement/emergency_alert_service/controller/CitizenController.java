package com.disastermanagement.emergency_alert_service.controller;

import com.disastermanagement.emergency_alert_service.dto.LocationRequest;
import com.disastermanagement.emergency_alert_service.entity.Alert;
import com.disastermanagement.emergency_alert_service.entity.Disaster;
import com.disastermanagement.emergency_alert_service.entity.RescueRequest;
import com.disastermanagement.emergency_alert_service.entity.User;
import com.disastermanagement.emergency_alert_service.repository.AlertRepository;
import com.disastermanagement.emergency_alert_service.repository.UserRepository;
import com.disastermanagement.emergency_alert_service.service.CitizenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/citizen")
public class CitizenController {

    private final AlertRepository alertRepository;
    private final UserRepository userRepository;
    private final CitizenService citizenService;

    public CitizenController(AlertRepository alertRepository, UserRepository userRepository, CitizenService citizenService) {
        this.alertRepository = alertRepository;
        this.userRepository = userRepository;
        this.citizenService = citizenService;
    }

    @GetMapping("/test")
    public String citizenTest() {
        return "Citizen access granted!";
    }


        // Request rescue
        @PostMapping("/request-help")
        public RescueRequest requestHelp(@RequestBody RescueRequest request,Authentication authentication) {
            User user = userRepository
                    .findByEmail(authentication.getName())
                    .orElseThrow();

            return citizenService.createRescueRequest(request,user.getId());
        }

        // View rescue requests
        @GetMapping("/alerts")
        public List<Alert> getAlerts(Authentication authentication) {

            User user = userRepository
                    .findByEmail(authentication.getName())
                    .orElseThrow();

            return citizenService.getCitizenAlerts(user.getId());
        }

        // request delete
        @DeleteMapping("/request/{id}")
        public String cancelRequest(@PathVariable Long id) {
            citizenService.deleteRequest(id);
            return "Request cancelled";
        }
// view near by disasters
    @GetMapping("/disasters")
    public List<Disaster> getNearbyDisasters(Authentication authentication) {

        User user = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow();

        return citizenService.getNearbyDisasters(
                user.getLatitude(),
                user.getLongitude()
        );
    }

    // getting user longitude and latitude
    @PutMapping("/location")
    public String updateLocation(
            @RequestBody LocationRequest request,
            Authentication authentication) {

        User user = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow();

        user.setLatitude(request.getLatitude());
        user.setLongitude(request.getLongitude());

        userRepository.save(user);
        return "Location updated successfully";
    }

}


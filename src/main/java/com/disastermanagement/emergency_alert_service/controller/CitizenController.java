package com.disastermanagement.emergency_alert_service.controller;

import com.disastermanagement.emergency_alert_service.entity.Alert;
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



        // View alerts
        @GetMapping("/{citizenId}/alerts")
        public List<Alert> getAlerts(@PathVariable Long citizenId) {

            return citizenService.getCitizenAlerts(citizenId);
        }

        // Request rescue
        @PostMapping("/request-help")
        public RescueRequest requestHelp(@RequestBody RescueRequest request) {

            return citizenService.createRescueRequest(request);
        }

        // View rescue requests
        @GetMapping("/{citizenId}/requests")
        public List<RescueRequest> getRequests(@PathVariable Long citizenId) {

            return citizenService.getCitizenRequests(citizenId);
        }

}


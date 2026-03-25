package com.disastermanagement.emergency_alert_service.controller;

import com.disastermanagement.emergency_alert_service.dto.AlertRequestDTO;
import com.disastermanagement.emergency_alert_service.dto.AssignTaskRequest;
import com.disastermanagement.emergency_alert_service.dto.DashboardStats;
import com.disastermanagement.emergency_alert_service.entity.*;
import com.disastermanagement.emergency_alert_service.repository.AlertRepository;
import com.disastermanagement.emergency_alert_service.repository.DisasterRepository;
import com.disastermanagement.emergency_alert_service.repository.RescueTaskRepository;
import com.disastermanagement.emergency_alert_service.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final RescueTaskRepository rescueTaskRepository;
    private final AlertRepository alertRepository;
    private final DisasterRepository disasterRepository;
    private final UserRepository userRepository;

    public AdminController(RescueTaskRepository rescueTaskRepository, AlertRepository alertRepository, DisasterRepository disasterRepository, UserRepository userRepository) {
        this.rescueTaskRepository = rescueTaskRepository;
        this.alertRepository = alertRepository;
        this.disasterRepository = disasterRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/test")
    public String adminTest() {
        return "Admin access granted!";
    }
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboardStats(){

        long disasters = disasterRepository.count();
        long alerts = alertRepository.count();
        long tasks = rescueTaskRepository.count();
        long responders = userRepository.findByRole(Role.RESPONDER).size();

        return ResponseEntity.ok(
                new DashboardStats(
                        disasters,
                        alerts,
                        tasks,
                        responders
                )
        );
    }

    @GetMapping("/disasters")
    public List<Disaster> getAllDisasters() {
        return disasterRepository.findAll();
    }

    @PostMapping("/create-alert")
    public ResponseEntity<String> createAlert(
            @RequestBody AlertRequestDTO request,
            Authentication authentication) {

        Disaster disaster = disasterRepository
                .findById(request.getDisasterId())
                .orElseThrow(() -> new RuntimeException("Disaster not found"));

        User admin = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow();

        Alert alert = new Alert();
        alert.setDisaster(disaster);
        alert.setMessage(request.getMessage());
        alert.setBroadcastTime(LocalDateTime.now());

        alertRepository.save(alert);

        return ResponseEntity.ok("Alert created successfully");
    }



    @GetMapping("/rescue-tasks")
    public List<RescueTask> getAllTasks() {
        return rescueTaskRepository.findAll();
    }

    @GetMapping("/responders")
    public List<User> getAllResponder(){ return userRepository.findByRole(Role.RESPONDER);}

    @GetMapping("/alerts")
    public List<Alert> getAllAlerts(){ return alertRepository.findAll();}
}


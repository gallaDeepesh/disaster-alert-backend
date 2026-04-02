package com.disastermanagement.emergency_alert_service.controller;

import com.disastermanagement.emergency_alert_service.dto.ReportRequestDTO;
import com.disastermanagement.emergency_alert_service.dto.UpdateTaskRequest;
import com.disastermanagement.emergency_alert_service.entity.*;
import com.disastermanagement.emergency_alert_service.repository.*;
import com.disastermanagement.emergency_alert_service.service.ResponderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/responder")
public class ResponderController {

    private final AlertRepository alertRepository;
    private final RescueTaskRepository rescueTaskRepository;
    private final UserRepository userRepository;
    private final AlertAcknowledgmentRepository acknowledgmentRepository;
    private final ReportRepository reportRepository;

    public ResponderController(
            AlertRepository alertRepository,
            RescueTaskRepository rescueTaskRepository,
            UserRepository userRepository,
            AlertAcknowledgmentRepository acknowledgmentRepository,
            ReportRepository reportRepository) {

        this.alertRepository = alertRepository;
        this.rescueTaskRepository = rescueTaskRepository;
        this.userRepository = userRepository;
        this.acknowledgmentRepository = acknowledgmentRepository;
        this.reportRepository = reportRepository;
    }


    @GetMapping("/alerts")
    public List<Alert> getActiveAlerts() {

        return alertRepository.findByStatus("ACTIVE");
    }


    @PostMapping("/acknowledge/{alertId}")
    public ResponseEntity<String> acknowledgeAlert(
            @PathVariable Long alertId,
            Authentication authentication) {

        User responder = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow();

        Alert alert = alertRepository
                .findById(alertId)
                .orElseThrow(() -> new RuntimeException("Alert not found"));

        if (acknowledgmentRepository
                .existsByAlertAndResponder(alert, responder)) {

            return ResponseEntity.ok("Already acknowledged");
        }

        AlertAcknowledgment ack = new AlertAcknowledgment();

        ack.setAlert(alert);
        ack.setResponder(responder);
        ack.setAcknowledgedAt(LocalDateTime.now());

        acknowledgmentRepository.save(ack);

        return ResponseEntity.ok("Alert acknowledged");
    }


    @GetMapping("/tasks")
    public List<RescueTask> getMyTasks(Authentication authentication) {

        User responder = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow();

        return rescueTaskRepository.findByResponder(responder);
    }

    @PutMapping("/tasks/{taskId}/status")
    public ResponseEntity<String> updateTaskStatus(
            @PathVariable Long taskId,
            @RequestBody UpdateTaskRequest request,
            Authentication authentication) {

        User responder = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow();

        RescueTask task = rescueTaskRepository
                .findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.getResponder().getId()
                .equals(responder.getId())) {

            return ResponseEntity
                    .status(403)
                    .body("Not authorized to update this task");
        }

        task.setStatus(request.getStatus());
        task.setUpdatedAt(LocalDateTime.now());

        rescueTaskRepository.save(task);

        return ResponseEntity.ok("Task updated successfully");
    }


    @PostMapping("/reports")
    public ResponseEntity<String> submitReport(
            @RequestBody ReportRequestDTO request,
            Authentication authentication) {

        User responder = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow();

        Disaster disaster = new Disaster();
        disaster.setId(request.getDisasterId());

        Report report = new Report();

        report.setDisaster(disaster);
        report.setResponder(responder);
        report.setDetails(request.getDetails());
        report.setSubmittedAt(LocalDateTime.now());

        reportRepository.save(report);

        return ResponseEntity.ok("Report submitted successfully");
    }


    @GetMapping("/history")
    public List<RescueTask> getCompletedTasks(
            Authentication authentication) {

        User responder = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow();

        return rescueTaskRepository
                .findByResponderAndStatus(
                        responder,
                        "COMPLETED"
                );
    }
}
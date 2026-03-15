package com.disastermanagement.emergency_alert_service.controller;

import com.disastermanagement.emergency_alert_service.dto.UpdateTaskRequest;
import com.disastermanagement.emergency_alert_service.entity.RescueTask;
import com.disastermanagement.emergency_alert_service.entity.User;
import com.disastermanagement.emergency_alert_service.repository.AlertAcknowledgmentRepository;
import com.disastermanagement.emergency_alert_service.repository.AlertRepository;
import com.disastermanagement.emergency_alert_service.repository.RescueTaskRepository;
import com.disastermanagement.emergency_alert_service.repository.UserRepository;
import com.disastermanagement.emergency_alert_service.service.ResponderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import java.util.List;

@RestController
@RequestMapping("/responder")
public class ResponderController {

    private final ResponderService responderService;

    public ResponderController(ResponderService responderService) {
        this.responderService = responderService;
    }

    // View assigned tasks
    @GetMapping("/{responderId}/tasks")
    public List<RescueTask> getTasks(@PathVariable Long responderId) {

        return responderService.getResponderTasks(responderId);
    }

    // Update task status
    @PutMapping("/task/{taskId}")
    public RescueTask updateTaskStatus(
            @PathVariable Long taskId,
            @RequestParam String status) {

        return responderService.updateTaskStatus(taskId, status);
    }
}
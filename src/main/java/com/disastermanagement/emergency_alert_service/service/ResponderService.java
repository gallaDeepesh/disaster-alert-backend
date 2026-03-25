package com.disastermanagement.emergency_alert_service.service;



import com.disastermanagement.emergency_alert_service.entity.RescueTask;
import com.disastermanagement.emergency_alert_service.repository.RescueTaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResponderService {

    private final RescueTaskRepository rescueTaskRepository;

    public ResponderService(RescueTaskRepository rescueTaskRepository) {
        this.rescueTaskRepository = rescueTaskRepository;
    }

    public List<RescueTask> getResponderTasks(Long responderId) {

        return rescueTaskRepository.findByResponderId(responderId);
    }

    public RescueTask updateTaskStatus(Long taskId, String status) {

        RescueTask task = rescueTaskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        task.setStatus(status);

        return rescueTaskRepository.save(task);
    }
}

package com.disastermanagement.emergency_alert_service.repository;

import com.disastermanagement.emergency_alert_service.entity.RescueTask;
import com.disastermanagement.emergency_alert_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RescueTaskRepository extends JpaRepository<RescueTask, Long> {
    List<RescueTask> findByResponderId(Long responder);

    List<RescueTask> findByResponder(User responder);
    List<RescueTask> findByResponderAndStatus(User responder,String status);
}

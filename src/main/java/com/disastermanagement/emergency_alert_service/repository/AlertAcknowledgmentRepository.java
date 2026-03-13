package com.disastermanagement.emergency_alert_service.repository;

import com.disastermanagement.emergency_alert_service.entity.Alert;
import com.disastermanagement.emergency_alert_service.entity.AlertAcknowledgment;
import com.disastermanagement.emergency_alert_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertAcknowledgmentRepository
        extends JpaRepository<AlertAcknowledgment, Long> {

    boolean existsByAlertAndResponder(Alert alert, User responder);
}

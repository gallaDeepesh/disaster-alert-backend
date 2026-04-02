package com.disastermanagement.emergency_alert_service.repository;

import com.disastermanagement.emergency_alert_service.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    boolean existsByDisasterIdAndUserId(Long disasterId, Long userId);
    List<Alert> findByUserId(Long userId);

    List<Alert> findByStatus(String active);
}
package com.disastermanagement.emergency_alert_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.disastermanagement.emergency_alert_service.entity.DisasterAnalytics;
public interface DisasterAnalyticsRepository extends JpaRepository<DisasterAnalytics, Long> {
}

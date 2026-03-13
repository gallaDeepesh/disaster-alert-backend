package com.disastermanagement.emergency_alert_service.repository;

import com.disastermanagement.emergency_alert_service.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {}

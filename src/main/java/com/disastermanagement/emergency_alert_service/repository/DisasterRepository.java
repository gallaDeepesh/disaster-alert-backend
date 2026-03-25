package com.disastermanagement.emergency_alert_service.repository;

import com.disastermanagement.emergency_alert_service.entity.Disaster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DisasterRepository extends JpaRepository<Disaster, Long> {
    boolean existsByExternalId(String externalId);

    Optional<Disaster> findByExternalId(String externalId);

    List<Disaster> findByTypeAndStatus(String type, String status);

    List<Disaster> findBySeverityOrderByTimestampDesc(String severity);
}

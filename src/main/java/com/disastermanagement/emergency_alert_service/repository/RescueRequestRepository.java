package com.disastermanagement.emergency_alert_service.repository;


import com.disastermanagement.emergency_alert_service.entity.RescueRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RescueRequestRepository extends JpaRepository<RescueRequest, Long> {

    List<RescueRequest> findByCitizenId(Long citizenId);

}
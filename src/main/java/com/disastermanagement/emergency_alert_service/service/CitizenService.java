package com.disastermanagement.emergency_alert_service.service;

import com.disastermanagement.emergency_alert_service.entity.Alert;
import com.disastermanagement.emergency_alert_service.entity.Disaster;
import com.disastermanagement.emergency_alert_service.entity.RescueRequest;
import com.disastermanagement.emergency_alert_service.entity.User;
import com.disastermanagement.emergency_alert_service.repository.AlertRepository;
import com.disastermanagement.emergency_alert_service.repository.DisasterRepository;
import com.disastermanagement.emergency_alert_service.repository.RescueRequestRepository;
import com.disastermanagement.emergency_alert_service.utility.GeoUtils;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CitizenService {

    private final AlertRepository alertRepository;
    private final RescueRequestRepository rescueRequestRepository;
    private final DisasterRepository disasterRepository;

    public CitizenService(AlertRepository alertRepository,
                          RescueRequestRepository rescueRequestRepository, DisasterRepository disasterRepository) {
        this.alertRepository = alertRepository;
        this.rescueRequestRepository = rescueRequestRepository;
        this.disasterRepository = disasterRepository;
    }

    public List<Alert> getCitizenAlerts(Long citizenId) {
        return alertRepository.findByUserId(citizenId);
    }

    public RescueRequest createRescueRequest(RescueRequest request, Long citizenId) {
        request.setCitizenId(citizenId);
        request.setStatus("PENDING");
        request.setCreatedAt(LocalDateTime.now());

        return rescueRequestRepository.save(request);
    }

    public List<RescueRequest> getCitizenRequests(Long citizenId) {

        return rescueRequestRepository.findByCitizenId(citizenId);
    }

    public void deleteRequest(Long id) {
        RescueRequest rr = rescueRequestRepository.findById(id).orElseThrow();
        rescueRequestRepository.delete(rr);
    }

    public List<Disaster> getNearbyDisasters(double userLat, double userLon) {

        List<Disaster> allDisasters = disasterRepository.findAll();

        return allDisasters.stream()
                .filter(disaster -> {

                    double distance = GeoUtils.calculateDistance(
                            userLat,
                            userLon,
                            disaster.getLatitude(),
                            disaster.getLongitude()
                    );

                    return distance <= 50; // 50 km radius
                })
                .toList();
    }
}


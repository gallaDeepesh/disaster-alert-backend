package com.disastermanagement.emergency_alert_service.service;

import com.disastermanagement.emergency_alert_service.dto.EarthquakeResponseDTO;
import com.disastermanagement.emergency_alert_service.entity.Disaster;
import com.disastermanagement.emergency_alert_service.repository.DisasterRepository;
import com.project.disaster.events.DisasterDetectedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.context.ApplicationEventPublisher;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class EarthquakeService {
    private final ApplicationEventPublisher eventPublisher;
    private final WebClient webClient;
    private final DisasterRepository disasterRepository;

    public EarthquakeService(ApplicationEventPublisher eventPublisher, WebClient webClient, DisasterRepository disasterRepository) {
        this.eventPublisher = eventPublisher;
        this.webClient = webClient;
        this.disasterRepository = disasterRepository;
    }

    private final String USGS_URL =
            "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.geojson";

    @Scheduled(fixedRate = 300000) // every 5 minutes
    public void scheduledFetch() {
        fetchAndSaveEarthquakes();
    }

    public void fetchAndSaveEarthquakes() {

        EarthquakeResponseDTO response =
                webClient.get()
                        .uri(USGS_URL)
                        .retrieve()
                        .bodyToMono(EarthquakeResponseDTO.class)
                        .block();

        if (response == null || response.getFeatures() == null) {
            return;
        }

        for (EarthquakeResponseDTO.Feature feature : response.getFeatures()) {
            // to prevent duplicates
            if (disasterRepository
                    .findByExternalId(feature.getId())
                    .isPresent()) {
                continue;  // skip duplicate
            }

            EarthquakeResponseDTO.Properties prop =
                    feature.getProperties();

            Disaster disaster = new Disaster();
            disaster.setType("EARTHQUAKE");
            disaster.setLocation(prop.getPlace());
            disaster.setExternalId(feature.getId());
            disaster.setMagnitude(prop.getMag());
            if (feature.getGeometry() != null && feature.getGeometry().getCoordinates() != null) {
                disaster.setLongitude(feature.getGeometry().getCoordinates().get(0));
                disaster.setLatitude(feature.getGeometry().getCoordinates().get(1));
            }

            disaster.setSeverity(
                    prop.getMag() != null && prop.getMag() > 5
                            ? "HIGH"
                            : "LOW"
            );
            disaster.setTimestamp(
                    LocalDateTime.ofInstant(
                            Instant.ofEpochMilli(prop.getTime()),
                            ZoneId.systemDefault()
                    )
            );
            disaster.setStatus("ACTIVE");

            Disaster savedDisaster = disasterRepository.save(disaster);

            eventPublisher.publishEvent(
                    new DisasterDetectedEvent(savedDisaster)
            );
        }
    }
}
package com.disastermanagement.emergency_alert_service.service.fetchers;


import com.disastermanagement.emergency_alert_service.entity.Disaster;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

/**
 * USGS Earthquake Hazards Program — GeoJSON Summary Feed.
 * Free, no auth, near real-time.
 *
 * Bounding box for India: lat [8, 37], lon [68, 97]
 * API docs: https://earthquake.usgs.gov/fdsnws/event/1/
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UsgsEarthquakeFetcher implements DisasterFetchStrategy {

    private static final String URL =
            "https://earthquake.usgs.gov/fdsnws/event/1/query"
                    + "?format=geojson"
                    + "&minlatitude=8.0&maxlatitude=37.0"
                    + "&minlongitude=68.0&maxlongitude=97.0"
                    + "&orderby=time"
                    + "&limit=100";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public String getSourceName() {
        return "USGS_EARTHQUAKE";
    }

    @Override
    public List<Disaster> fetch() {
        List<Disaster> disasters = new ArrayList<>();
        try {
            String json = restTemplate.getForObject(URL, String.class);
            JsonNode root     = objectMapper.readTree(json);
            JsonNode features = root.path("features");

            for (JsonNode feature : features) {
                try {
                    disasters.add(mapToDisaster(feature));
                } catch (Exception e) {
                    log.warn("[USGS] Skipping malformed feature: {}", e.getMessage());
                }
            }
            log.info("[USGS] Fetched {} earthquake records for India", disasters.size());
        } catch (Exception e) {
            log.error("[USGS] Fetch failed: {}", e.getMessage(), e);
        }
        return disasters;
    }

    private Disaster mapToDisaster(JsonNode feature) {
        JsonNode props  = feature.path("properties");
        JsonNode coords = feature.path("geometry").path("coordinates");

        double magnitude = props.path("mag").asDouble(0.0);
        long   epochMs   = props.path("time").asLong();
        String place     = props.path("place").asText("Unknown");
        String usgsId    = feature.path("id").asText();

        LocalDateTime timestamp = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(epochMs), ZoneOffset.UTC);

        Disaster d = new Disaster();
        d.setExternalId("USGS-" + usgsId);
        d.setType("EARTHQUAKE");
        d.setLocation(place);
        d.setMagnitude(magnitude);
        d.setLatitude(coords.path(1).asDouble());
        d.setLongitude(coords.path(0).asDouble());
        d.setTimestamp(timestamp);
        d.setSeverity(resolveSeverity(magnitude));
        d.setStatus("ACTIVE");
        return d;
    }

    /** USGS magnitude → severity mapping */
    private String resolveSeverity(double mag) {
        if (mag >= 7.0) return "CRITICAL";
        if (mag >= 5.0) return "HIGH";
        if (mag >= 3.0) return "MODERATE";
        return "LOW";
    }
}

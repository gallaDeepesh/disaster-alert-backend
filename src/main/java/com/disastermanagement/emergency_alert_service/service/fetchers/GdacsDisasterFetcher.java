package com.disastermanagement.emergency_alert_service.service.fetchers;


import com.disastermanagement.emergency_alert_service.entity.Disaster;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * GDACS — Global Disaster Alert and Coordination System.
 * Covers: Earthquakes, Tropical Cyclones, Floods, Volcanoes, Droughts, Wildfires.
 * Free, no auth, operated by EU Joint Research Centre + UN OCHA.
 *
 * Docs: https://www.gdacs.org/gdacsapi/
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GdacsDisasterFetcher implements DisasterFetchStrategy {

    /**
     * EQ=Earthquake, TC=Cyclone, FL=Flood, VO=Volcano, DR=Drought, WF=Wildfire
     */
    private static final String URL =
            "https://www.gdacs.org/gdacsapi/api/events/geteventlist/SEARCH"
                    + "?eventlist=EQ;TC;FL;VO;DR;WF"
                    + "&alertlevel=Green;Orange;Red"
                    + "&country=India"
                    + "&pagesize=100";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public String getSourceName() {
        return "GDACS";
    }

    @Override
    public List<Disaster> fetch() {
        List<Disaster> disasters = new ArrayList<>();
        try {
            String json   = restTemplate.getForObject(URL, String.class);
            JsonNode root  = objectMapper.readTree(json);
            JsonNode items = root.path("features");

            for (JsonNode item : items) {
                try {
                    disasters.add(mapToDisaster(item));
                } catch (Exception e) {
                    log.warn("[GDACS] Skipping malformed item: {}", e.getMessage());
                }
            }
            log.info("[GDACS] Fetched {} disaster records for India", disasters.size());
        } catch (Exception e) {
            log.error("[GDACS] Fetch failed: {}", e.getMessage(), e);
        }
        return disasters;
    }

    private Disaster mapToDisaster(JsonNode feature) {
        JsonNode props  = feature.path("properties");
        JsonNode coords = feature.path("geometry").path("coordinates");

        String eventId    = props.path("eventid").asText();
        String eventType  = props.path("eventtype").asText("UNKNOWN");
        String alertLevel = props.path("alertlevel").asText("Green");
        String name       = props.path("name").asText("Unknown location");
        String fromDate   = props.path("fromdate").asText("");
        double magnitude  = props.path("severitydata").path("severity").asDouble(0.0);

        Disaster d = new Disaster();
        d.setExternalId("GDACS-" + eventId);
        d.setType(resolveType(eventType));
        d.setLocation(name);
        d.setSeverity(resolveAlertLevel(alertLevel));
        d.setMagnitude(magnitude);
        d.setTimestamp(parseDate(fromDate));
        d.setStatus(props.path("iscurrent").asBoolean(false) ? "ACTIVE" : "CLOSED");

        if (!coords.isMissingNode() && coords.isArray() && coords.size() >= 2) {
            d.setLatitude(coords.get(1).asDouble());
            d.setLongitude(coords.get(0).asDouble());
        }
        return d;
    }

    private String resolveType(String code) {
        return switch (code.toUpperCase()) {
            case "EQ" -> "EARTHQUAKE";
            case "TC" -> "CYCLONE";
            case "FL" -> "FLOOD";
            case "VO" -> "VOLCANO";
            case "DR" -> "DROUGHT";
            case "WF" -> "WILDFIRE";
            default   -> "UNKNOWN";
        };
    }

    private String resolveAlertLevel(String level) {
        return switch (level.toLowerCase()) {
            case "red"    -> "CRITICAL";
            case "orange" -> "HIGH";
            case "green"  -> "MODERATE";
            default       -> "LOW";
        };
    }

    private LocalDateTime parseDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) return LocalDateTime.now();
        try {
            return LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_DATE_TIME);
        } catch (DateTimeParseException e) {
            return LocalDateTime.now();
        }
    }
}

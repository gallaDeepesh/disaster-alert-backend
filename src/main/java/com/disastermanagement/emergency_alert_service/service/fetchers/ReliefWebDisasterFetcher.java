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
 * ReliefWeb Disasters API — UN OCHA's humanitarian information platform.
 * Covers: all major disaster categories officially declared in India.
 * Free, no auth required.
 *
 * Docs: https://apidoc.reliefweb.int/
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReliefWebDisasterFetcher implements DisasterFetchStrategy {

    private static final String URL =
            "https://api.reliefweb.int/v1/disasters"
                    + "?appname=disaster-fetch-service"
                    + "&filter[operator]=AND"
                    + "&filter[conditions][0][field]=country.iso3"
                    + "&filter[conditions][0][value]=IND"
                    + "&fields[include][]=name"
                    + "&fields[include][]=type"
                    + "&fields[include][]=status"
                    + "&fields[include][]=date"
                    + "&fields[include][]=country"
                    + "&fields[include][]=glide"
                    + "&limit=50"
                    + "&sort[]=date.created:desc";

    // ReliefWeb uses ISO-8601 with timezone offset
    private static final DateTimeFormatter RW_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public String getSourceName() {
        return "RELIEFWEB";
    }

    @Override
    public List<Disaster> fetch() {
        List<Disaster> disasters = new ArrayList<>();
        try {
            String json    = restTemplate.getForObject(URL, String.class);
            JsonNode root   = objectMapper.readTree(json);
            JsonNode items  = root.path("data");

            for (JsonNode item : items) {
                try {
                    disasters.add(mapToDisaster(item));
                } catch (Exception e) {
                    log.warn("[ReliefWeb] Skipping malformed record: {}", e.getMessage());
                }
            }
            log.info("[ReliefWeb] Fetched {} disaster records for India", disasters.size());
        } catch (Exception e) {
            log.error("[ReliefWeb] Fetch failed: {}", e.getMessage(), e);
        }
        return disasters;
    }

    private Disaster mapToDisaster(JsonNode item) {
        String id      = item.path("id").asText();
        JsonNode fields = item.path("fields");

        String name    = fields.path("name").asText("Unknown");
        String status  = fields.path("status").asText("alert");
        String dateStr = fields.path("date").path("created").asText("");

        // First entry in type array gives primary disaster type
        String typeCode = "UNKNOWN";
        JsonNode typeArr = fields.path("type");
        if (typeArr.isArray() && typeArr.size() > 0) {
            typeCode = typeArr.get(0).path("name").asText("UNKNOWN");
        }

        // GLIDE number carries location info (e.g., FL-2024-000001-IND)
        String glide = fields.path("glide").asText("");

        Disaster d = new Disaster();
        d.setExternalId("RW-" + id);
        d.setType(typeCode.toUpperCase().replace(" ", "_"));
        d.setLocation(extractLocation(name));
        d.setSeverity(resolveStatus(status));
        d.setMagnitude(0.0); // ReliefWeb doesn't publish magnitude
        d.setTimestamp(parseDate(dateStr));
        d.setStatus(status.equalsIgnoreCase("ongoing") ? "ACTIVE" : "CLOSED");
        // Lat/Lon not available from this endpoint; leave null — geocoder can enrich later
        return d;
    }

    /**
     * Strips trailing country suffix: "India: Flood in Kerala" -> "Flood in Kerala"
     */
    private String extractLocation(String name) {
        return name.contains(":") ? name.substring(name.indexOf(':') + 1).trim() : name;
    }

    private String resolveStatus(String status) {
        return switch (status.toLowerCase()) {
            case "alert"   -> "HIGH";
            case "ongoing" -> "CRITICAL";
            case "past"    -> "LOW";
            default        -> "MODERATE";
        };
    }

    private LocalDateTime parseDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) return LocalDateTime.now();
        try {
            // Strip trailing timezone offset for simple parse
            String cleaned = dateStr.replaceAll("[+-]\\d{2}:\\d{2}$", "").trim();
            return LocalDateTime.parse(cleaned, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException e) {
            return LocalDateTime.now();
        }
    }
}

package com.disastermanagement.emergency_alert_service.service;

import com.disastermanagement.emergency_alert_service.entity.Disaster;
import com.disastermanagement.emergency_alert_service.event.DisasterCreatedEvent;
import com.disastermanagement.emergency_alert_service.repository.DisasterRepository;
import com.disastermanagement.emergency_alert_service.service.fetchers.DisasterFetchStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Orchestrates all DisasterFetchStrategy implementations.
 *
 * Flow per strategy:
 *   1. strategy.fetch()               — retrieve and map raw API data
 *   2. Deduplicate by externalId      — skip already-persisted records
 *   3. saveAll() in one transaction   — batch persist new disasters
 *   4. publishEvent(DisasterCreatedEvent) per record — notify all listeners
 *
 * Scheduling  : every 30 minutes via @Scheduled cron.
 * Extensibility: add a new @Component implementing DisasterFetchStrategy
 *                and Spring auto-registers it here — zero changes needed.
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class DisasterDataFetchService {

    private final List<DisasterFetchStrategy> strategies;
    private final DisasterRepository disasterRepository;
    private final ApplicationEventPublisher   eventPublisher;

    // ---------------------------------------------------------------
    // Scheduled sweep — every 30 minutes
    // ---------------------------------------------------------------
    @Scheduled(cron = "0 */30 * * * *")
    public void scheduledFetch() {
        log.info("=== Scheduled disaster data fetch starting ({} sources) ===",
                strategies.size());
        strategies.forEach(this::fetchAndProcess);
        log.info("=== Scheduled disaster data fetch complete ===");
    }

    // ---------------------------------------------------------------
    // On-demand (usable from REST controller or integration tests)
    // ---------------------------------------------------------------
    public void fetchAll() {
        strategies.forEach(this::fetchAndProcess);
    }

    public void fetchBySource(String sourceName) {
        strategies.stream()
                .filter(s -> s.getSourceName().equalsIgnoreCase(sourceName))
                .findFirst()
                .ifPresentOrElse(
                        this::fetchAndProcess,
                        () -> log.warn("No strategy registered for source: {}", sourceName)
                );
    }

    // ---------------------------------------------------------------
    // Core pipeline
    // ---------------------------------------------------------------
    @Transactional
    public void fetchAndProcess(DisasterFetchStrategy strategy) {
        String source = strategy.getSourceName();
        log.info("[{}] Fetching...", source);

        List<Disaster> fetched = strategy.fetch();
        if (fetched.isEmpty()) {
            log.info("[{}] No records returned", source);
            return;
        }

        // Deduplicate: only keep records not already in DB
        List<Disaster> newOnes = fetched.stream()
                .filter(d -> !disasterRepository.existsByExternalId(d.getExternalId()))
                .toList();

        if (newOnes.isEmpty()) {
            log.info("[{}] All {} records already persisted — nothing new", source, fetched.size());
            return;
        }

        List<Disaster> saved = disasterRepository.saveAll(newOnes);
        log.info("[{}] Persisted {}/{} new disaster records", source, saved.size(), fetched.size());

        // Publish one event per persisted disaster
        saved.forEach(disaster -> {
            eventPublisher.publishEvent(new DisasterCreatedEvent(this, disaster, source));
            log.debug("[{}] Event published — externalId={}, type={}, severity={}",
                    source, disaster.getExternalId(), disaster.getType(), disaster.getSeverity());
        });
    }
}
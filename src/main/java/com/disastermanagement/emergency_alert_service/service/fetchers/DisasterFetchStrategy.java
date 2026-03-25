package com.disastermanagement.emergency_alert_service.service.fetchers;


import com.disastermanagement.emergency_alert_service.entity.Disaster;

import java.util.List;

/**
 * Strategy contract for each disaster API source.
 * Implement this to add a new data provider without touching the orchestrator.
 */
public interface DisasterFetchStrategy {

    /** Human-readable name used in logs and event metadata. */
    String getSourceName();

    /** Fetch and map raw API data into Disaster entities (not yet persisted). */
    List<Disaster> fetch();
}
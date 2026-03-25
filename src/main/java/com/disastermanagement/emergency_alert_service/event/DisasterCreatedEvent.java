package com.disastermanagement.emergency_alert_service.event;

import com.disastermanagement.emergency_alert_service.entity.Disaster;
import org.springframework.context.ApplicationEvent;

/**
 * Published whenever a new Disaster is persisted.
 * Listeners receive both the saved entity and the originating API source.
 */
public class DisasterCreatedEvent extends ApplicationEvent {

    private final Disaster disaster;
    private final String apiSource;

    public DisasterCreatedEvent(Object publisher, Disaster disaster, String apiSource) {
        super(publisher);
        this.disaster = disaster;
        this.apiSource = apiSource;
    }

    public Disaster getDisaster() { return disaster; }
    public String getApiSource()  { return apiSource; }
}

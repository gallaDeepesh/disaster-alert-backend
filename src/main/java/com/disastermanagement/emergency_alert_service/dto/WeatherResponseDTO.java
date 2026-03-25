package com.disastermanagement.emergency_alert_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class WeatherResponseDTO {

    private double lat;
    private double lon;
    private String timezone;

    @JsonProperty("alerts")
    private List<Alert> alerts;

    @Data
    public static class Alert {

        @JsonProperty("sender_name")
        private String senderName;

        @JsonProperty("event")
        private String event;          // e.g. "Flood Warning", "Cyclone Alert"

        @JsonProperty("start")
        private long start;            // Unix epoch seconds

        @JsonProperty("end")
        private long end;

        @JsonProperty("description")
        private String description;

        @JsonProperty("tags")
        private List<String> tags;

        public String getSenderName() {
            return senderName;
        }

        public void setSenderName(String senderName) {
            this.senderName = senderName;
        }

        public String getEvent() {
            return event;
        }

        public void setEvent(String event) {
            this.event = event;
        }

        public long getStart() {
            return start;
        }

        public void setStart(long start) {
            this.start = start;
        }

        public long getEnd() {
            return end;
        }

        public void setEnd(long end) {
            this.end = end;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public List<String> getTags() {
            return tags;
        }

        public void setTags(List<String> tags) {
            this.tags = tags;
        }
    }
}

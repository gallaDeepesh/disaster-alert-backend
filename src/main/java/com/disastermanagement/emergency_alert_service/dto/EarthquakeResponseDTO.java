package com.disastermanagement.emergency_alert_service.dto;


import lombok.*;
import java.util.List;


public class EarthquakeResponseDTO {

    private List<Feature> features;

    public List<Feature> getFeatures() {
        return features;
    }

    public void setFeatures(List<Feature> features) {
        this.features = features;
    }

    public static class Feature {
        private String id;
        private Properties properties;
        private Geometry geometry;

        public String getId() {
            return id;
        }
        public void setId(String id) {
            this.id = id;
        }
        public Properties getProperties() {
            return properties;
        }
        public void setProperties(Properties properties) {
            this.properties = properties;
        }

        public Geometry getGeometry() {
            return geometry;
        }

        public void setGeometry(Geometry geometry) {
            this.geometry = geometry;
        }
    }

    public static class Properties {
        private Double mag;
        private String place;
        private Long time;


        public Double getMag() {
            return mag;
        }

        public void setMag(Double mag) {
            this.mag = mag;
        }

        public String getPlace() {
            return place;
        }

        public void setPlace(String place) {
            this.place = place;
        }

        public Long getTime() {
            return time;
        }

        public void setTime(Long time) {
            this.time = time;
        }
    }

    public static class Geometry {
        private List<Double> coordinates; // [long, lat, depth]

        public List<Double> getCoordinates() {
            return coordinates;
        }

        public void setCoordinates(List<Double> coordinates) {
            this.coordinates = coordinates;
        }
    }
}
package com.disastermanagement.emergency_alert_service.config;


import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.time.Duration;

@Configuration
@EnableScheduling
@EnableAsync
public class DisasterFetchConfig {

    @Bean
    public RestTemplate disasterRestTemplate(RestTemplateBuilder builder) {
        // Using setConnectTimeout instead of connectTimeout if the builder is picky
        return builder
                .setConnectTimeout(Duration.ofSeconds(10))
                .setReadTimeout(Duration.ofSeconds(20))
                .build();
    }

    @Bean
    public ObjectMapper disasterObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // Explicitly register the module
        mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }
}

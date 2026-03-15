package com.disastermanagement.emergency_alert_service.controller;

import com.disastermanagement.emergency_alert_service.entity.Disaster;
import com.disastermanagement.emergency_alert_service.repository.DisasterRepository;
import com.project.disaster.events.DisasterDetectedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/test")
public class TestController {

    private final DisasterRepository disasterRepository;
    private final ApplicationEventPublisher eventPublisher;

    public TestController( DisasterRepository disasterRepository, ApplicationEventPublisher eventPublisher) {
        this.disasterRepository = disasterRepository;
        this.eventPublisher = eventPublisher;
    }


    @GetMapping("/hello")
    public String hello() {
        return "Working";
    }

    @GetMapping("/disaster")
    public String createTestDisaster() {

        Disaster disaster = new Disaster();

        disaster.setType("EARTHQUAKE");
        disaster.setMagnitude(6.5);
        disaster.setLatitude(17.385);
        disaster.setLongitude(78.486);
        disaster.setLocation("Hyderabad");
        disaster.setTimestamp(LocalDateTime.now());

        Disaster savedDisaster = disasterRepository.save(disaster);

        eventPublisher.publishEvent(
                new DisasterDetectedEvent(savedDisaster)
        );;

        return "Test disaster triggered!";
    }

}

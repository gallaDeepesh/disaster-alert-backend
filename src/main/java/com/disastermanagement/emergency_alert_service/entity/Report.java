package com.disastermanagement.emergency_alert_service.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;


@Entity
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Disaster disaster;

    @ManyToOne
    private User responder;

    private String details;
    private LocalDateTime submittedAt;
}


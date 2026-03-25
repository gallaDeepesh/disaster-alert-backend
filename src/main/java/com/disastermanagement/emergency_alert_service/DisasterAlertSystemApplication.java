package com.disastermanagement.emergency_alert_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DisasterAlertSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(DisasterAlertSystemApplication.class, args);
	}

}

package com.disastermanagement.emergency_alert_service.service;

import org.springframework.mail.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendAlertEmail(String to, String message) {

        SimpleMailMessage mail = new SimpleMailMessage();
        System.out.println("Sending email to: " + to);
        mail.setTo(to);
        mail.setSubject("Disaster Alert ⚠");
        mail.setText(message);

        mailSender.send(mail);
    }
}

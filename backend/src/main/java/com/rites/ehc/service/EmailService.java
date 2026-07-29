package com.rites.ehc.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender emailSender;

    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendStatusUpdateEmail(String to, String requestNo, String newStatus, String employeeName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("ehc-noreply@rites.com");
            message.setTo(to);
            message.setSubject("EHC Request Update: " + requestNo);
            message.setText("Dear " + employeeName + ",\n\n" +
                    "This is to notify you that the status of your Executive Health Checkup request (" + requestNo + ") " +
                    "has been updated to: " + newStatus + ".\n\n" +
                    "Please log in to the portal for more details.\n\n" +
                    "Regards,\nRITES EHC Portal");
            
            emailSender.send(message);
            System.out.println("Email sent successfully to: " + to);
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
            // We swallow the exception to not block the main transaction if mail fails
        }
    }
}

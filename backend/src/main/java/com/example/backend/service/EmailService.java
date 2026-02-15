package com.example.backend.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailService {

    @Value("${brevo.api.key:}")
    private String brevoApiKey;

    @Value("${brevo.sender.email:eateasy.demo@gmail.com}")
    private String senderEmail;

    @Value("${brevo.sender.name:EatEasy App}")
    private String senderName;

    @Value("${spring.mail.username:}")
    private String smtpUsername;

    @Value("${spring.mail.password:}")
    private String smtpPassword;

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired(required = false)
    private JavaMailSender javaMailSender;

    @jakarta.annotation.PostConstruct
    public void init() {
        System.out.println("--- EmailService Initialized ---");
        boolean brevoConfigured = brevoApiKey != null && !brevoApiKey.isEmpty()
                && !brevoApiKey.equals("placeholder_key");
        boolean smtpConfigured = javaMailSender != null && smtpUsername != null && !smtpUsername.isEmpty()
                && !smtpPassword.equals("placeholder_password");

        if (brevoConfigured) {
            String masked = brevoApiKey.length() > 4 ? brevoApiKey.substring(0, 4) + "..." : "****";
            System.out.println("Brevo API Configured: YES (" + masked + ")");
        } else {
            System.out.println("Brevo API Configured: NO (using placeholder or empty)");
        }
        System.out.println("SMTP Configured: " + smtpConfigured);

        if (!brevoConfigured && !smtpConfigured) {
            System.err.println("WARNING: No valid email configuration found! Email sending will fail.");
            System.err.println("Please set BREVO_API_KEY or (MAIL_USERNAME and MAIL_PASSWORD) environment variables.");
        }
    }

    public boolean sendEmail(String to, String subject, String body) {
        boolean brevoAttempted = false;

        // 1. Try Brevo API if configured
        if (brevoApiKey != null && !brevoApiKey.isEmpty() && !brevoApiKey.equals("placeholder_key")) {
            brevoAttempted = true;
            if (sendViaBrevo(to, subject, body)) {
                return true;
            }
            System.err.println("Brevo API failed. Attempting fallback to SMTP...");
        }

        // 2. Try SMTP (JavaMailSender)
        if (sendViaSmtp(to, subject, body)) {
            return true;
        }

        System.err.println("All email methods failed.");
        return false;
    }

    private boolean sendViaBrevo(String to, String subject, String body) {
        System.out.println("Attempting to send via Brevo API...");
        String key = brevoApiKey.trim();
        if (key.length() > 10) {
            System.out.println("Using Key: " + key.substring(0, 5) + "..." + key.substring(key.length() - 5));
        } else {
            System.out.println("Using Key: (too short)");
        }

        try {
            String url = "https://api.brevo.com/v3/smtp/email";

            // Request Headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", brevoApiKey.trim()); // Trim whitespace!
            headers.set("accept", "application/json");

            // Request Body
            Map<String, Object> requestBody = new HashMap<>();

            // Sender
            Map<String, String> sender = new HashMap<>();
            sender.put("name", senderName);
            sender.put("email", senderEmail);
            requestBody.put("sender", sender);

            // Recipient
            Map<String, String> recipient = new HashMap<>();
            recipient.put("email", to);
            requestBody.put("to", Collections.singletonList(recipient));

            // Content
            requestBody.put("subject", subject);
            requestBody.put("htmlContent", body);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("Email sent successfully to " + to + " via Brevo API.");
                return true;
            } else {
                System.err.println("BREVO FAILURE. Status: " + response.getStatusCode());
                System.err.println("Response Body: " + response.getBody());
                return false;
            }
        } catch (Exception e) {
            System.err.println("BREVO EXCEPTION: " + e.getMessage());
            if (e instanceof org.springframework.web.client.HttpClientErrorException) {
                System.err.println("Server Response: "
                        + ((org.springframework.web.client.HttpClientErrorException) e).getResponseBodyAsString());
            }
            // e.printStackTrace(); // Hide stack trace for clarity
            return false;
        }
    }

    private boolean sendViaSmtp(String to, String subject, String body) {
        if (javaMailSender == null) {
            System.err.println("JavaMailSender is not configured (null).");
            return false;
        }

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String fromEmail = (smtpUsername != null && !smtpUsername.isEmpty()) ? smtpUsername : senderEmail;

            helper.setFrom(fromEmail, senderName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true); // Treat body as HTML

            javaMailSender.send(message);
            System.out.println("Email sent successfully to " + to + " via SMTP.");
            return true;
        } catch (Exception e) {
            System.err.println("Exception sending email via SMTP to " + to + ": " + e.getMessage());
            // e.printStackTrace(); // Reduce noise unless verbose
            return false;
        }
    }
}

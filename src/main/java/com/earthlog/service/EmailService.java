package com.earthlog.service;

import com.earthlog.event.EmailNotificationEvent;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

/**
 * Service for sending emails using JavaMailSender and Thymeleaf templates.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.mail.from:noreply@earthlog.com}")
    private String fromEmail;

    @Value("${app.mail.from-name:EarthLog}")
    private String fromName;

    @Value("${app.mail.enabled:false}")
    private boolean emailEnabled;

    /**
     * Send a templated email based on the event type.
     */
    public void sendTemplatedEmail(EmailNotificationEvent event) {
        if (!emailEnabled) {
            log.info("Email sending is disabled. Would have sent {} email to {}", 
                    event.getType(), event.getTo());
            logEmailContent(event);
            return;
        }

        try {
            String templateName = getTemplateName(event.getType());
            String htmlContent = processTemplate(templateName, event.getTemplateData());

            sendHtmlEmail(event.getTo(), event.getSubject(), htmlContent);

            log.info("Successfully sent {} email to {}", event.getType(), event.getTo());

        } catch (MailException | MessagingException e) {
            log.error("Failed to send email to {}: {}", event.getTo(), e.getMessage());
            throw new RuntimeException("Email sending failed", e);
        }
    }

    /**
     * Send a simple text email.
     */
    public void sendSimpleEmail(String to, String subject, String text) {
        if (!emailEnabled) {
            log.info("Email sending is disabled. Would have sent email to {} with subject: {}", 
                    to, subject);
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, false);

            mailSender.send(message);

            log.info("Sent simple email to {}", to);

        } catch (Exception e) {
            log.error("Failed to send simple email to {}: {}", to, e.getMessage());
            throw new RuntimeException("Email sending failed", e);
        }
    }

    /**
     * Send an HTML email.
     */
    public void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        try {
            helper.setFrom(fromEmail, fromName);
        } catch (Exception e) {
            helper.setFrom(fromEmail);
        }
        
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }

    private String getTemplateName(EmailNotificationEvent.EmailType type) {
        return switch (type) {
            case WELCOME -> "email/welcome";
            case ACTIVITY_LOGGED -> "email/activity-logged";
            case GOAL_APPROACHING -> "email/goal-approaching";
            case GOAL_EXCEEDED -> "email/goal-exceeded";
            case BADGE_EARNED -> "email/badge-earned";
            case WEEKLY_SUMMARY -> "email/weekly-summary";
            case CHALLENGE_JOINED -> "email/challenge-joined";
            case CHALLENGE_COMPLETED -> "email/challenge-completed";
        };
    }

    private String processTemplate(String templateName, Map<String, Object> variables) {
        Context context = new Context();
        if (variables != null) {
            variables.forEach(context::setVariable);
        }
        
        try {
            return templateEngine.process(templateName, context);
        } catch (Exception e) {
            log.warn("Template {} not found, using fallback", templateName);
            return generateFallbackContent(variables);
        }
    }

    private String generateFallbackContent(Map<String, Object> variables) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body>");
        sb.append("<h1>EarthLog Notification</h1>");
        
        if (variables != null) {
            variables.forEach((key, value) -> {
                sb.append("<p><strong>").append(key).append(":</strong> ")
                  .append(value).append("</p>");
            });
        }
        
        sb.append("<hr>");
        sb.append("<p>Track your carbon footprint with EarthLog</p>");
        sb.append("</body></html>");
        
        return sb.toString();
    }

    private void logEmailContent(EmailNotificationEvent event) {
        log.info("=== Email Preview ===");
        log.info("To: {} ({})", event.getTo(), event.getToName());
        log.info("Subject: {}", event.getSubject());
        log.info("Type: {}", event.getType());
        log.info("Data: {}", event.getTemplateData());
        log.info("=====================");
    }
}

package com.earthlog.messaging;

import com.earthlog.config.RabbitMQConfig;
import com.earthlog.event.EmailNotificationEvent;
import com.earthlog.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Listener for email notification events.
 * Processes the email queue and sends emails asynchronously.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmailNotificationListener {

    private final EmailService emailService;

    @RabbitListener(queues = RabbitMQConfig.EMAIL_NOTIFICATION_QUEUE)
    public void handleEmailNotification(EmailNotificationEvent event) {
        log.info("Received email notification event - To: {}, Type: {}, Subject: {}",
                event.getTo(), event.getType(), event.getSubject());

        try {
            emailService.sendTemplatedEmail(event);
            log.info("Successfully sent email to {} - Type: {}", event.getTo(), event.getType());

        } catch (Exception e) {
            log.error("Failed to send email to {} - Type: {}: {}",
                    event.getTo(), event.getType(), e.getMessage(), e);
            throw e; // Re-throw to trigger retry or DLQ
        }
    }
}

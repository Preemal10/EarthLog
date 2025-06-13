package com.earthlog.messaging;

import com.earthlog.config.RabbitMQConfig;
import com.earthlog.event.ActivityCreatedEvent;
import com.earthlog.event.BadgeEvaluationEvent;
import com.earthlog.event.EmailNotificationEvent;
import com.earthlog.event.GoalProgressEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * Central service for publishing events to RabbitMQ.
 * All event publishing should go through this service.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EventPublisher {

    private final RabbitTemplate rabbitTemplate;

    /**
     * Publish an activity created event.
     * This triggers goal progress check, badge evaluation, and potential notifications.
     */
    public void publishActivityCreated(ActivityCreatedEvent event) {
        log.info("Publishing activity created event for user {} - Activity ID: {}", 
                event.getUserId(), event.getActivityId());
        
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ACTIVITY_ROUTING_KEY,
                event
        );
        
        log.debug("Activity created event published successfully");
    }

    /**
     * Publish a goal progress event.
     * Used to notify users about their goal status.
     */
    public void publishGoalProgress(GoalProgressEvent event) {
        log.info("Publishing goal progress event for user {} - Goal ID: {}, Status: {}", 
                event.getUserId(), event.getGoalId(), event.getStatus());
        
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                "goal.progress",
                event
        );
        
        log.debug("Goal progress event published successfully");
    }

    /**
     * Publish a badge evaluation event.
     * Used to check if user qualifies for any new badges.
     */
    public void publishBadgeEvaluation(BadgeEvaluationEvent event) {
        log.info("Publishing badge evaluation event for user {}", event.getUserId());
        
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                "badge.evaluate",
                event
        );
        
        log.debug("Badge evaluation event published successfully");
    }

    /**
     * Publish an email notification event.
     * Used to queue emails for async sending.
     */
    public void publishEmailNotification(EmailNotificationEvent event) {
        log.info("Publishing email notification event to {} - Type: {}", 
                event.getTo(), event.getType());
        
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.EMAIL_ROUTING_KEY,
                event
        );
        
        log.debug("Email notification event published successfully");
    }
}

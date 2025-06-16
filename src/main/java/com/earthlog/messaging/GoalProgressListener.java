package com.earthlog.messaging;

import com.earthlog.config.RabbitMQConfig;
import com.earthlog.event.EmailNotificationEvent;
import com.earthlog.event.GoalProgressEvent;
import com.earthlog.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Listener for goal progress events.
 * Sends notifications when users approach or exceed their goals.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GoalProgressListener {

    private final EventPublisher eventPublisher;
    private final NotificationService notificationService;

    @RabbitListener(queues = RabbitMQConfig.GOAL_PROGRESS_QUEUE)
    public void handleGoalProgress(GoalProgressEvent event) {
        log.info("Received goal progress event - Goal ID: {}, User: {}, Status: {}, Progress: {}%",
                event.getGoalId(), event.getUserId(), event.getStatus(), event.getProgressPercentage());

        try {
            switch (event.getStatus()) {
                case APPROACHING_LIMIT:
                    handleApproachingLimit(event);
                    break;
                case NEAR_LIMIT:
                    handleNearLimit(event);
                    break;
                case EXCEEDED:
                    handleExceeded(event);
                    break;
                case ON_TRACK:
                    log.debug("User {} is on track with goal {}", event.getUserId(), event.getGoalId());
                    break;
            }

            // Save notification to database
            notificationService.createGoalNotification(event);

        } catch (Exception e) {
            log.error("Error processing goal progress event for goal {}: {}",
                    event.getGoalId(), e.getMessage(), e);
            throw e;
        }
    }

    private void handleApproachingLimit(GoalProgressEvent event) {
        log.info("User {} is approaching {} goal limit at {}%",
                event.getUserId(), event.getPeriod(), event.getProgressPercentage());

        // Queue email notification
        EmailNotificationEvent emailEvent = EmailNotificationEvent.goalApproaching(
                event.getUserEmail(),
                event.getUserName(),
                event.getPeriod().name().toLowerCase(),
                event.getProgressPercentage().doubleValue(),
                event.getTargetEmission().doubleValue()
        );

        eventPublisher.publishEmailNotification(emailEvent);
    }

    private void handleNearLimit(GoalProgressEvent event) {
        log.warn("User {} is near {} goal limit at {}%",
                event.getUserId(), event.getPeriod(), event.getProgressPercentage());

        // Queue urgent email notification
        EmailNotificationEvent emailEvent = EmailNotificationEvent.goalApproaching(
                event.getUserEmail(),
                event.getUserName(),
                event.getPeriod().name().toLowerCase(),
                event.getProgressPercentage().doubleValue(),
                event.getTargetEmission().doubleValue()
        );
        emailEvent.setSubject("Urgent: You're almost at your " + 
                event.getPeriod().name().toLowerCase() + " carbon limit!");

        eventPublisher.publishEmailNotification(emailEvent);
    }

    private void handleExceeded(GoalProgressEvent event) {
        log.warn("User {} has EXCEEDED {} goal limit! Current: {} kg, Target: {} kg",
                event.getUserId(), event.getPeriod(), 
                event.getCurrentEmission(), event.getTargetEmission());

        // Queue exceeded notification email
        EmailNotificationEvent emailEvent = EmailNotificationEvent.goalExceeded(
                event.getUserEmail(),
                event.getUserName(),
                event.getPeriod().name().toLowerCase(),
                event.getCurrentEmission().doubleValue(),
                event.getTargetEmission().doubleValue()
        );

        eventPublisher.publishEmailNotification(emailEvent);
    }
}

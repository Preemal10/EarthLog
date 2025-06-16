package com.earthlog.messaging;

import com.earthlog.config.RabbitMQConfig;
import com.earthlog.event.BadgeEvaluationEvent;
import com.earthlog.event.EmailNotificationEvent;
import com.earthlog.service.BadgeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Listener for badge evaluation events.
 * Checks if user qualifies for any new badges and awards them.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BadgeEvaluationListener {

    private final BadgeService badgeService;
    private final EventPublisher eventPublisher;

    @RabbitListener(queues = RabbitMQConfig.BADGE_EVALUATION_QUEUE)
    public void handleBadgeEvaluation(BadgeEvaluationEvent event) {
        log.info("Received badge evaluation event for user {} - Total activities: {}",
                event.getUserId(), event.getTotalActivitiesLogged());

        try {
            // Evaluate badge criteria and get newly awarded badges
            List<BadgeService.AwardedBadge> newBadges = badgeService.evaluateAndAwardBadges(event);

            if (!newBadges.isEmpty()) {
                log.info("User {} earned {} new badge(s)", event.getUserId(), newBadges.size());

                // Send email notification for each new badge
                for (BadgeService.AwardedBadge badge : newBadges) {
                    EmailNotificationEvent emailEvent = EmailNotificationEvent.badgeEarned(
                            event.getUserEmail(),
                            event.getUserName(),
                            badge.getName(),
                            badge.getDescription()
                    );

                    eventPublisher.publishEmailNotification(emailEvent);

                    log.info("User {} earned badge: {}", event.getUserId(), badge.getName());
                }
            } else {
                log.debug("No new badges earned by user {}", event.getUserId());
            }

        } catch (Exception e) {
            log.error("Error evaluating badges for user {}: {}",
                    event.getUserId(), e.getMessage(), e);
            throw e;
        }
    }
}

package com.earthlog.messaging;

import com.earthlog.config.RabbitMQConfig;
import com.earthlog.entity.Goal;
import com.earthlog.event.ActivityCreatedEvent;
import com.earthlog.event.BadgeEvaluationEvent;
import com.earthlog.event.GoalProgressEvent;
import com.earthlog.repository.ActivityRepository;
import com.earthlog.repository.GoalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Listener for activity created events.
 * Triggers downstream processing for goals and badges.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ActivityEventListener {

    private final GoalRepository goalRepository;
    private final ActivityRepository activityRepository;
    private final EventPublisher eventPublisher;

    @RabbitListener(queues = RabbitMQConfig.ACTIVITY_QUEUE)
    @Transactional(readOnly = true)
    public void handleActivityCreated(ActivityCreatedEvent event) {
        log.info("Received activity created event - Activity ID: {}, User ID: {}, CO2: {} kg",
                event.getActivityId(), event.getUserId(), event.getCalculatedCo2());

        try {
            // 1. Check goal progress
            checkGoalProgress(event);

            // 2. Trigger badge evaluation
            triggerBadgeEvaluation(event);

            log.info("Successfully processed activity created event for activity {}", 
                    event.getActivityId());

        } catch (Exception e) {
            log.error("Error processing activity created event for activity {}: {}",
                    event.getActivityId(), e.getMessage(), e);
            throw e; // Re-throw to trigger retry or DLQ
        }
    }

    private void checkGoalProgress(ActivityCreatedEvent event) {
        // Find active goals for the user
        List<Goal> activeGoals = goalRepository.findByUserIdAndIsActiveTrue(event.getUserId());

        if (activeGoals.isEmpty()) {
            log.debug("No active goals found for user {}", event.getUserId());
            return;
        }

        LocalDate today = LocalDate.now();

        for (Goal goal : activeGoals) {
            // Check if activity date falls within goal period
            if (!event.getActivityDate().isBefore(goal.getStartDate()) &&
                    !event.getActivityDate().isAfter(goal.getEndDate())) {

                // Calculate current emissions for the goal period
                BigDecimal currentEmissions = activityRepository
                        .sumEmissionsByUserAndDateRange(
                                event.getUserId(),
                                goal.getStartDate(),
                                goal.getEndDate()
                        );

                if (currentEmissions == null) {
                    currentEmissions = BigDecimal.ZERO;
                }

                // Create and publish goal progress event
                GoalProgressEvent progressEvent = GoalProgressEvent.create(
                        goal.getId(),
                        event.getUserId(),
                        event.getUserEmail(),
                        event.getUserName(),
                        goal.getPeriod(),
                        goal.getTargetEmission(),
                        currentEmissions,
                        goal.getStartDate(),
                        goal.getEndDate()
                );

                eventPublisher.publishGoalProgress(progressEvent);

                log.info("Published goal progress event for goal {} - Status: {}, Progress: {}%",
                        goal.getId(), progressEvent.getStatus(), progressEvent.getProgressPercentage());
            }
        }
    }

    private void triggerBadgeEvaluation(ActivityCreatedEvent event) {
        // Create badge evaluation event with user info
        BadgeEvaluationEvent badgeEvent = BadgeEvaluationEvent.forUser(
                event.getUserId(),
                event.getUserEmail(),
                event.getUserName()
        );

        // Count activities by type for badge criteria
        int bikeCount = activityRepository.countByUserIdAndActivityType(
                event.getUserId(), 
                com.earthlog.enums.ActivityType.BIKE
        );
        int walkCount = activityRepository.countByUserIdAndActivityType(
                event.getUserId(), 
                com.earthlog.enums.ActivityType.WALK
        );
        int veganCount = activityRepository.countByUserIdAndActivityType(
                event.getUserId(), 
                com.earthlog.enums.ActivityType.VEGAN_MEAL
        );
        int vegetarianCount = activityRepository.countByUserIdAndActivityType(
                event.getUserId(), 
                com.earthlog.enums.ActivityType.VEGETARIAN_MEAL
        );

        badgeEvent.setBikeActivities(bikeCount);
        badgeEvent.setWalkActivities(walkCount);
        badgeEvent.setVeganMeals(veganCount);
        badgeEvent.setVegetarianMeals(vegetarianCount);

        // Get total activities count
        long totalActivities = activityRepository.countByUserId(event.getUserId());
        badgeEvent.setTotalActivitiesLogged((int) totalActivities);

        eventPublisher.publishBadgeEvaluation(badgeEvent);

        log.debug("Published badge evaluation event for user {}", event.getUserId());
    }
}

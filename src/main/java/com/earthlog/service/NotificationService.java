package com.earthlog.service;

import com.earthlog.entity.Notification;
import com.earthlog.entity.User;
import com.earthlog.event.GoalProgressEvent;
import com.earthlog.exception.ResourceNotFoundException;
import com.earthlog.repository.NotificationRepository;
import com.earthlog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for managing user notifications.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    // Cooldown period to avoid spam (e.g., don't send same notification type within 1 hour)
    private static final int NOTIFICATION_COOLDOWN_HOURS = 1;

    /**
     * Create a notification from a goal progress event.
     */
    @Transactional
    public Notification createGoalNotification(GoalProgressEvent event) {
        // Check cooldown to avoid notification spam
        if (isInCooldown(event.getUserId(), getNotificationType(event.getStatus()), event.getGoalId())) {
            log.debug("Notification cooldown active for user {} goal {}", 
                    event.getUserId(), event.getGoalId());
            return null;
        }

        User user = userRepository.findById(event.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", event.getUserId()));

        Notification.NotificationType type = getNotificationType(event.getStatus());
        String title = getGoalNotificationTitle(event.getStatus(), event.getPeriod().name());
        String message = getGoalNotificationMessage(event);

        Notification notification = Notification.builder()
                .user(user)
                .type(type)
                .title(title)
                .message(message)
                .referenceId(event.getGoalId())
                .referenceType("GOAL")
                .build();

        notification = notificationRepository.save(notification);
        log.info("Created {} notification for user {}", type, event.getUserId());

        return notification;
    }

    /**
     * Create a badge earned notification.
     */
    @Transactional
    public Notification createBadgeNotification(Long userId, Long badgeId, 
            String badgeName, String badgeDescription) {
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Notification notification = Notification.builder()
                .user(user)
                .type(Notification.NotificationType.BADGE_EARNED)
                .title("New Badge Earned: " + badgeName)
                .message("Congratulations! You've earned the \"" + badgeName + 
                        "\" badge. " + badgeDescription)
                .referenceId(badgeId)
                .referenceType("BADGE")
                .build();

        notification = notificationRepository.save(notification);
        log.info("Created badge notification for user {} - Badge: {}", userId, badgeName);

        return notification;
    }

    /**
     * Create a custom notification.
     */
    @Transactional
    public Notification createNotification(Long userId, Notification.NotificationType type,
            String title, String message, Long referenceId, String referenceType) {
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Notification notification = Notification.builder()
                .user(user)
                .type(type)
                .title(title)
                .message(message)
                .referenceId(referenceId)
                .referenceType(referenceType)
                .build();

        return notificationRepository.save(notification);
    }

    /**
     * Get paginated notifications for a user.
     */
    @Transactional(readOnly = true)
    public Page<Notification> getUserNotifications(Long userId, Pageable pageable) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    /**
     * Get unread notifications for a user.
     */
    @Transactional(readOnly = true)
    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
    }

    /**
     * Get unread notification count.
     */
    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    /**
     * Mark a notification as read.
     */
    @Transactional
    public Notification markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));

        if (!notification.getUser().getId().equals(userId)) {
            throw new RuntimeException("Notification does not belong to user");
        }

        notification.markAsRead();
        return notificationRepository.save(notification);
    }

    /**
     * Mark all notifications as read for a user.
     */
    @Transactional
    public int markAllAsRead(Long userId) {
        return notificationRepository.markAllAsReadByUserId(userId, LocalDateTime.now());
    }

    /**
     * Delete old notifications (cleanup job).
     */
    @Transactional
    public int deleteOldNotifications(int daysOld) {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(daysOld);
        return notificationRepository.deleteOlderThan(cutoff);
    }

    private boolean isInCooldown(Long userId, Notification.NotificationType type, Long referenceId) {
        LocalDateTime cooldownStart = LocalDateTime.now().minusHours(NOTIFICATION_COOLDOWN_HOURS);
        return notificationRepository.existsRecentNotification(userId, type, referenceId, cooldownStart);
    }

    private Notification.NotificationType getNotificationType(GoalProgressEvent.GoalStatus status) {
        return switch (status) {
            case APPROACHING_LIMIT -> Notification.NotificationType.GOAL_APPROACHING;
            case NEAR_LIMIT -> Notification.NotificationType.GOAL_NEAR_LIMIT;
            case EXCEEDED -> Notification.NotificationType.GOAL_EXCEEDED;
            case ON_TRACK -> Notification.NotificationType.SYSTEM;
        };
    }

    private String getGoalNotificationTitle(GoalProgressEvent.GoalStatus status, String period) {
        return switch (status) {
            case APPROACHING_LIMIT -> "Goal Alert: Approaching " + period.toLowerCase() + " limit";
            case NEAR_LIMIT -> "Urgent: Almost at " + period.toLowerCase() + " carbon limit";
            case EXCEEDED -> "Goal Exceeded: " + period.toLowerCase() + " target surpassed";
            case ON_TRACK -> "On Track: " + period.toLowerCase() + " goal";
        };
    }

    private String getGoalNotificationMessage(GoalProgressEvent event) {
        return switch (event.getStatus()) {
            case APPROACHING_LIMIT -> String.format(
                    "You've used %.1f%% of your %s carbon budget (%.2f of %.2f kg CO2). " +
                    "Consider choosing more eco-friendly options.",
                    event.getProgressPercentage().doubleValue(),
                    event.getPeriod().name().toLowerCase(),
                    event.getCurrentEmission().doubleValue(),
                    event.getTargetEmission().doubleValue()
            );
            case NEAR_LIMIT -> String.format(
                    "You're at %.1f%% of your %s carbon budget! Only %.2f kg CO2 remaining.",
                    event.getProgressPercentage().doubleValue(),
                    event.getPeriod().name().toLowerCase(),
                    event.getTargetEmission().subtract(event.getCurrentEmission()).doubleValue()
            );
            case EXCEEDED -> String.format(
                    "You've exceeded your %s carbon target by %.2f kg CO2. " +
                    "Total: %.2f kg (Target: %.2f kg). Consider offsetting or adjusting your goal.",
                    event.getPeriod().name().toLowerCase(),
                    event.getCurrentEmission().subtract(event.getTargetEmission()).doubleValue(),
                    event.getCurrentEmission().doubleValue(),
                    event.getTargetEmission().doubleValue()
            );
            case ON_TRACK -> String.format(
                    "Great job! You're on track with your %s goal at %.1f%%.",
                    event.getPeriod().name().toLowerCase(),
                    event.getProgressPercentage().doubleValue()
            );
        };
    }
}

package com.earthlog.messaging;

import com.earthlog.enums.GoalPeriod;
import com.earthlog.event.EmailNotificationEvent;
import com.earthlog.event.GoalProgressEvent;
import com.earthlog.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Goal Progress Listener Tests")
class GoalProgressListenerTest {

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private NotificationService notificationService;

    private GoalProgressListener goalProgressListener;

    @BeforeEach
    void setUp() {
        goalProgressListener = new GoalProgressListener(eventPublisher, notificationService);
    }

    @Test
    @DisplayName("Should send email when goal is approaching limit")
    void shouldSendEmailWhenGoalApproachingLimit() {
        // Given
        GoalProgressEvent event = GoalProgressEvent.create(
                1L, 1L, "test@example.com", "Test User",
                GoalPeriod.WEEKLY, new BigDecimal("100"), new BigDecimal("80"),
                LocalDate.now().minusDays(7), LocalDate.now()
        );

        // When
        goalProgressListener.handleGoalProgress(event);

        // Then
        ArgumentCaptor<EmailNotificationEvent> emailCaptor = 
                ArgumentCaptor.forClass(EmailNotificationEvent.class);
        verify(eventPublisher).publishEmailNotification(emailCaptor.capture());

        EmailNotificationEvent emailEvent = emailCaptor.getValue();
        assertEquals("test@example.com", emailEvent.getTo());
        assertEquals(EmailNotificationEvent.EmailType.GOAL_APPROACHING, emailEvent.getType());
        assertTrue(emailEvent.getSubject().contains("approaching"));

        verify(notificationService).createGoalNotification(event);
    }

    @Test
    @DisplayName("Should send urgent email when goal is near limit")
    void shouldSendUrgentEmailWhenGoalNearLimit() {
        // Given
        GoalProgressEvent event = GoalProgressEvent.create(
                1L, 1L, "test@example.com", "Test User",
                GoalPeriod.WEEKLY, new BigDecimal("100"), new BigDecimal("95"),
                LocalDate.now().minusDays(7), LocalDate.now()
        );

        // When
        goalProgressListener.handleGoalProgress(event);

        // Then
        ArgumentCaptor<EmailNotificationEvent> emailCaptor = 
                ArgumentCaptor.forClass(EmailNotificationEvent.class);
        verify(eventPublisher).publishEmailNotification(emailCaptor.capture());

        EmailNotificationEvent emailEvent = emailCaptor.getValue();
        assertTrue(emailEvent.getSubject().contains("Urgent"));

        verify(notificationService).createGoalNotification(event);
    }

    @Test
    @DisplayName("Should send exceeded email when goal is exceeded")
    void shouldSendExceededEmailWhenGoalExceeded() {
        // Given
        GoalProgressEvent event = GoalProgressEvent.create(
                1L, 1L, "test@example.com", "Test User",
                GoalPeriod.WEEKLY, new BigDecimal("100"), new BigDecimal("120"),
                LocalDate.now().minusDays(7), LocalDate.now()
        );

        // When
        goalProgressListener.handleGoalProgress(event);

        // Then
        ArgumentCaptor<EmailNotificationEvent> emailCaptor = 
                ArgumentCaptor.forClass(EmailNotificationEvent.class);
        verify(eventPublisher).publishEmailNotification(emailCaptor.capture());

        EmailNotificationEvent emailEvent = emailCaptor.getValue();
        assertEquals(EmailNotificationEvent.EmailType.GOAL_EXCEEDED, emailEvent.getType());
        assertTrue(emailEvent.getSubject().contains("exceeded"));

        verify(notificationService).createGoalNotification(event);
    }

    @Test
    @DisplayName("Should not send email when on track")
    void shouldNotSendEmailWhenOnTrack() {
        // Given
        GoalProgressEvent event = GoalProgressEvent.create(
                1L, 1L, "test@example.com", "Test User",
                GoalPeriod.WEEKLY, new BigDecimal("100"), new BigDecimal("50"),
                LocalDate.now().minusDays(7), LocalDate.now()
        );

        // When
        goalProgressListener.handleGoalProgress(event);

        // Then - no email should be sent for ON_TRACK status
        verify(eventPublisher, never()).publishEmailNotification(any());
        verify(notificationService).createGoalNotification(event);
    }
}

package com.earthlog.messaging;

import com.earthlog.config.RabbitMQConfig;
import com.earthlog.enums.ActivityCategory;
import com.earthlog.enums.ActivityType;
import com.earthlog.enums.GoalPeriod;
import com.earthlog.event.ActivityCreatedEvent;
import com.earthlog.event.BadgeEvaluationEvent;
import com.earthlog.event.EmailNotificationEvent;
import com.earthlog.event.GoalProgressEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Event Publisher Tests")
class EventPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    private EventPublisher eventPublisher;

    @BeforeEach
    void setUp() {
        eventPublisher = new EventPublisher(rabbitTemplate);
    }

    @Test
    @DisplayName("Should publish activity created event")
    void shouldPublishActivityCreatedEvent() {
        // Given
        ActivityCreatedEvent event = ActivityCreatedEvent.of(
                1L, 1L, "test@example.com", "Test User", "DE",
                ActivityCategory.TRANSPORT, ActivityType.CAR_PETROL,
                new BigDecimal("50"), "KM", new BigDecimal("9.5"),
                LocalDate.now(), "Test commute"
        );

        // When
        eventPublisher.publishActivityCreated(event);

        // Then
        ArgumentCaptor<String> exchangeCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> routingKeyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<ActivityCreatedEvent> eventCaptor = ArgumentCaptor.forClass(ActivityCreatedEvent.class);

        verify(rabbitTemplate).convertAndSend(
                exchangeCaptor.capture(),
                routingKeyCaptor.capture(),
                eventCaptor.capture()
        );

        assertEquals(RabbitMQConfig.EXCHANGE_NAME, exchangeCaptor.getValue());
        assertEquals(RabbitMQConfig.ACTIVITY_ROUTING_KEY, routingKeyCaptor.getValue());
        assertEquals(event.getActivityId(), eventCaptor.getValue().getActivityId());
        assertEquals(event.getUserId(), eventCaptor.getValue().getUserId());
    }

    @Test
    @DisplayName("Should publish goal progress event")
    void shouldPublishGoalProgressEvent() {
        // Given
        GoalProgressEvent event = GoalProgressEvent.create(
                1L, 1L, "test@example.com", "Test User",
                GoalPeriod.WEEKLY, new BigDecimal("100"), new BigDecimal("75"),
                LocalDate.now().minusDays(7), LocalDate.now()
        );

        // When
        eventPublisher.publishGoalProgress(event);

        // Then
        verify(rabbitTemplate).convertAndSend(
                eq(RabbitMQConfig.EXCHANGE_NAME),
                eq("goal.progress"),
                eq(event)
        );
    }

    @Test
    @DisplayName("Should publish badge evaluation event")
    void shouldPublishBadgeEvaluationEvent() {
        // Given
        BadgeEvaluationEvent event = BadgeEvaluationEvent.forUser(
                1L, "test@example.com", "Test User"
        );
        event.setTotalActivitiesLogged(10);
        event.setBikeActivities(5);

        // When
        eventPublisher.publishBadgeEvaluation(event);

        // Then
        verify(rabbitTemplate).convertAndSend(
                eq(RabbitMQConfig.EXCHANGE_NAME),
                eq("badge.evaluate"),
                eq(event)
        );
    }

    @Test
    @DisplayName("Should publish email notification event")
    void shouldPublishEmailNotificationEvent() {
        // Given
        EmailNotificationEvent event = EmailNotificationEvent.welcome(
                "test@example.com", "Test User"
        );

        // When
        eventPublisher.publishEmailNotification(event);

        // Then
        verify(rabbitTemplate).convertAndSend(
                eq(RabbitMQConfig.EXCHANGE_NAME),
                eq(RabbitMQConfig.EMAIL_ROUTING_KEY),
                eq(event)
        );
    }

    @Test
    @DisplayName("Goal progress event should calculate correct status")
    void goalProgressEventShouldCalculateCorrectStatus() {
        // Test ON_TRACK (< 75%)
        GoalProgressEvent onTrack = GoalProgressEvent.create(
                1L, 1L, "test@example.com", "Test",
                GoalPeriod.WEEKLY, new BigDecimal("100"), new BigDecimal("50"),
                LocalDate.now(), LocalDate.now()
        );
        assertEquals(GoalProgressEvent.GoalStatus.ON_TRACK, onTrack.getStatus());

        // Test APPROACHING_LIMIT (75-90%)
        GoalProgressEvent approaching = GoalProgressEvent.create(
                1L, 1L, "test@example.com", "Test",
                GoalPeriod.WEEKLY, new BigDecimal("100"), new BigDecimal("80"),
                LocalDate.now(), LocalDate.now()
        );
        assertEquals(GoalProgressEvent.GoalStatus.APPROACHING_LIMIT, approaching.getStatus());

        // Test NEAR_LIMIT (90-100%)
        GoalProgressEvent nearLimit = GoalProgressEvent.create(
                1L, 1L, "test@example.com", "Test",
                GoalPeriod.WEEKLY, new BigDecimal("100"), new BigDecimal("95"),
                LocalDate.now(), LocalDate.now()
        );
        assertEquals(GoalProgressEvent.GoalStatus.NEAR_LIMIT, nearLimit.getStatus());

        // Test EXCEEDED (> 100%)
        GoalProgressEvent exceeded = GoalProgressEvent.create(
                1L, 1L, "test@example.com", "Test",
                GoalPeriod.WEEKLY, new BigDecimal("100"), new BigDecimal("120"),
                LocalDate.now(), LocalDate.now()
        );
        assertEquals(GoalProgressEvent.GoalStatus.EXCEEDED, exceeded.getStatus());
    }
}

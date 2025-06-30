package com.earthlog.service;

import com.earthlog.entity.Badge;
import com.earthlog.entity.User;
import com.earthlog.enums.Role;
import com.earthlog.event.BadgeEvaluationEvent;
import com.earthlog.repository.BadgeRepository;
import com.earthlog.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Badge Service Tests")
class BadgeServiceTest {

    @Mock
    private BadgeRepository badgeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    private BadgeService badgeService;

    private User testUser;

    @BeforeEach
    void setUp() {
        badgeService = new BadgeService(badgeRepository, userRepository, notificationService);

        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .name("Test User")
                .provider("GOOGLE")
                .providerId("test-id")
                .role(Role.USER)
                .badges(new HashSet<>())
                .build();
    }

    @Test
    @DisplayName("Should award first activity badge")
    void shouldAwardFirstActivityBadge() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(badgeRepository.findByName("FIRST_STEP")).thenReturn(Optional.empty());
        when(badgeRepository.save(any(Badge.class))).thenAnswer(inv -> {
            Badge badge = inv.getArgument(0);
            badge.setId(1L);
            return badge;
        });

        BadgeEvaluationEvent event = BadgeEvaluationEvent.forUser(1L, "test@example.com", "Test User");
        event.setTotalActivitiesLogged(1);

        // When
        List<BadgeService.AwardedBadge> awarded = badgeService.evaluateAndAwardBadges(event);

        // Then
        assertFalse(awarded.isEmpty());
        assertEquals("FIRST_STEP", awarded.get(0).getName());
        verify(userRepository).save(testUser);
        verify(notificationService).createBadgeNotification(eq(1L), any(), eq("FIRST_STEP"), any());
    }

    @Test
    @DisplayName("Should award multiple badges when criteria met")
    void shouldAwardMultipleBadgesWhenCriteriaMet() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(badgeRepository.findByName(any())).thenReturn(Optional.empty());
        when(badgeRepository.save(any(Badge.class))).thenAnswer(inv -> {
            Badge badge = inv.getArgument(0);
            badge.setId(System.currentTimeMillis());
            return badge;
        });

        BadgeEvaluationEvent event = BadgeEvaluationEvent.forUser(1L, "test@example.com", "Test User");
        event.setTotalActivitiesLogged(50); // Should earn FIRST_STEP, ACTIVITY_STARTER, ACTIVITY_TRACKER
        event.setBikeActivities(20); // Should earn BIKE_ENTHUSIAST

        // When
        List<BadgeService.AwardedBadge> awarded = badgeService.evaluateAndAwardBadges(event);

        // Then
        assertTrue(awarded.size() >= 4);
        assertTrue(awarded.stream().anyMatch(b -> b.getName().equals("FIRST_STEP")));
        assertTrue(awarded.stream().anyMatch(b -> b.getName().equals("ACTIVITY_STARTER")));
        assertTrue(awarded.stream().anyMatch(b -> b.getName().equals("ACTIVITY_TRACKER")));
        assertTrue(awarded.stream().anyMatch(b -> b.getName().equals("BIKE_ENTHUSIAST")));
    }

    @Test
    @DisplayName("Should not award duplicate badges")
    void shouldNotAwardDuplicateBadges() {
        // Given - user already has FIRST_STEP badge
        Badge existingBadge = Badge.builder()
                .id(1L)
                .name("FIRST_STEP")
                .description("First activity")
                .build();
        testUser.getBadges().add(existingBadge);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        BadgeEvaluationEvent event = BadgeEvaluationEvent.forUser(1L, "test@example.com", "Test User");
        event.setTotalActivitiesLogged(1);

        // When
        List<BadgeService.AwardedBadge> awarded = badgeService.evaluateAndAwardBadges(event);

        // Then
        assertTrue(awarded.stream().noneMatch(b -> b.getName().equals("FIRST_STEP")));
    }

    @Test
    @DisplayName("Should not award badges when criteria not met")
    void shouldNotAwardBadgesWhenCriteriaNotMet() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        BadgeEvaluationEvent event = BadgeEvaluationEvent.forUser(1L, "test@example.com", "Test User");
        event.setTotalActivitiesLogged(0);
        event.setBikeActivities(0);

        // When
        List<BadgeService.AwardedBadge> awarded = badgeService.evaluateAndAwardBadges(event);

        // Then
        assertTrue(awarded.isEmpty());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should award plant lover badge for vegetarian and vegan meals")
    void shouldAwardPlantLoverBadge() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(badgeRepository.findByName(any())).thenReturn(Optional.empty());
        when(badgeRepository.save(any(Badge.class))).thenAnswer(inv -> {
            Badge badge = inv.getArgument(0);
            badge.setId(System.currentTimeMillis());
            return badge;
        });

        BadgeEvaluationEvent event = BadgeEvaluationEvent.forUser(1L, "test@example.com", "Test User");
        event.setTotalActivitiesLogged(1);
        event.setVegetarianMeals(15);
        event.setVeganMeals(10); // Total 25 plant-based meals

        // When
        List<BadgeService.AwardedBadge> awarded = badgeService.evaluateAndAwardBadges(event);

        // Then
        assertTrue(awarded.stream().anyMatch(b -> b.getName().equals("PLANT_LOVER")));
    }

    @Test
    @DisplayName("Should return empty list when user not found")
    void shouldReturnEmptyListWhenUserNotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        BadgeEvaluationEvent event = BadgeEvaluationEvent.forUser(1L, "test@example.com", "Test User");
        event.setTotalActivitiesLogged(100);

        // When
        List<BadgeService.AwardedBadge> awarded = badgeService.evaluateAndAwardBadges(event);

        // Then
        assertTrue(awarded.isEmpty());
    }
}

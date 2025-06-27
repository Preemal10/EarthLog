package com.earthlog.service;

import com.earthlog.dto.request.GoalRequest;
import com.earthlog.dto.response.GoalResponse;
import com.earthlog.entity.Goal;
import com.earthlog.entity.User;
import com.earthlog.enums.GoalPeriod;
import com.earthlog.enums.Role;
import com.earthlog.exception.BadRequestException;
import com.earthlog.exception.ResourceNotFoundException;
import com.earthlog.repository.GoalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Goal Service Tests")
class GoalServiceTest {

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private UserService userService;

    @Mock
    private ActivityService activityService;

    @InjectMocks
    private GoalService goalService;

    private User testUser;
    private Goal testGoal;
    private GoalRequest testRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
            .id(1L)
            .email("test@example.com")
            .name("Test User")
            .country("DE")
            .role(Role.USER)
            .build();

        testGoal = Goal.builder()
            .id(1L)
            .user(testUser)
            .targetEmission(new BigDecimal("200"))
            .period(GoalPeriod.MONTHLY)
            .startDate(LocalDate.now().withDayOfMonth(1))
            .endDate(LocalDate.now().withDayOfMonth(1).plusMonths(1).minusDays(1))
            .isActive(true)
            .build();

        testRequest = GoalRequest.builder()
            .targetEmission(new BigDecimal("200"))
            .period(GoalPeriod.MONTHLY)
            .startDate(LocalDate.now().withDayOfMonth(1))
            .endDate(LocalDate.now().withDayOfMonth(1).plusMonths(1).minusDays(1))
            .build();
    }

    @Test
    @DisplayName("Should create goal successfully")
    void shouldCreateGoalSuccessfully() {
        // Given
        when(userService.findById(1L)).thenReturn(testUser);
        when(goalRepository.save(any(Goal.class))).thenReturn(testGoal);
        when(activityService.getTotalCo2ByDateRange(eq(1L), any(), any()))
            .thenReturn(new BigDecimal("50"));

        // When
        GoalResponse result = goalService.createGoal(1L, testRequest);

        // Then
        assertNotNull(result);
        assertEquals(new BigDecimal("200"), result.getTargetEmission());
        assertEquals(GoalPeriod.MONTHLY, result.getPeriod());
        verify(goalRepository, times(1)).save(any(Goal.class));
    }

    @Test
    @DisplayName("Should throw exception when end date before start date")
    void shouldThrowExceptionWhenEndDateBeforeStartDate() {
        // Given
        when(userService.findById(1L)).thenReturn(testUser);
        
        GoalRequest invalidRequest = GoalRequest.builder()
            .targetEmission(new BigDecimal("200"))
            .period(GoalPeriod.MONTHLY)
            .startDate(LocalDate.now())
            .endDate(LocalDate.now().minusDays(1)) // End before start
            .build();

        // When & Then
        assertThrows(BadRequestException.class, () ->
            goalService.createGoal(1L, invalidRequest)
        );
    }

    @Test
    @DisplayName("Should get all goals for user")
    void shouldGetAllGoalsForUser() {
        // Given
        when(goalRepository.findByUserId(1L)).thenReturn(List.of(testGoal));
        when(activityService.getTotalCo2ByDateRange(eq(1L), any(), any()))
            .thenReturn(new BigDecimal("50"));

        // When
        List<GoalResponse> result = goalService.getGoals(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Should get active goals only")
    void shouldGetActiveGoalsOnly() {
        // Given
        when(goalRepository.findByUserIdAndIsActiveTrue(1L)).thenReturn(List.of(testGoal));
        when(activityService.getTotalCo2ByDateRange(eq(1L), any(), any()))
            .thenReturn(new BigDecimal("50"));

        // When
        List<GoalResponse> result = goalService.getActiveGoals(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getIsActive());
    }

    @Test
    @DisplayName("Should calculate progress percentage correctly")
    void shouldCalculateProgressPercentageCorrectly() {
        // Given
        when(goalRepository.findById(1L)).thenReturn(Optional.of(testGoal));
        when(activityService.getTotalCo2ByDateRange(eq(1L), any(), any()))
            .thenReturn(new BigDecimal("100")); // 50% of 200

        // When
        GoalResponse result = goalService.getGoalById(1L, 1L);

        // Then
        assertNotNull(result);
        assertEquals(new BigDecimal("100"), result.getCurrentEmission());
        assertEquals(new BigDecimal("50.00"), result.getProgressPercentage());
    }

    @Test
    @DisplayName("Should determine if on track correctly")
    void shouldDetermineIfOnTrackCorrectly() {
        // Given
        when(goalRepository.findById(1L)).thenReturn(Optional.of(testGoal));
        when(activityService.getTotalCo2ByDateRange(eq(1L), any(), any()))
            .thenReturn(new BigDecimal("100")); // Under target

        // When
        GoalResponse result = goalService.getGoalById(1L, 1L);

        // Then
        assertTrue(result.getIsOnTrack());
    }

    @Test
    @DisplayName("Should update goal successfully")
    void shouldUpdateGoalSuccessfully() {
        // Given
        when(goalRepository.findById(1L)).thenReturn(Optional.of(testGoal));
        when(goalRepository.save(any(Goal.class))).thenReturn(testGoal);
        when(activityService.getTotalCo2ByDateRange(eq(1L), any(), any()))
            .thenReturn(new BigDecimal("50"));

        GoalRequest updateRequest = GoalRequest.builder()
            .targetEmission(new BigDecimal("250"))
            .period(GoalPeriod.MONTHLY)
            .startDate(LocalDate.now().withDayOfMonth(1))
            .endDate(LocalDate.now().withDayOfMonth(1).plusMonths(1).minusDays(1))
            .build();

        // When
        GoalResponse result = goalService.updateGoal(1L, 1L, updateRequest);

        // Then
        assertNotNull(result);
        verify(goalRepository, times(1)).save(any(Goal.class));
    }

    @Test
    @DisplayName("Should delete goal successfully")
    void shouldDeleteGoalSuccessfully() {
        // Given
        when(goalRepository.findById(1L)).thenReturn(Optional.of(testGoal));
        doNothing().when(goalRepository).delete(testGoal);

        // When
        goalService.deleteGoal(1L, 1L);

        // Then
        verify(goalRepository, times(1)).delete(testGoal);
    }

    @Test
    @DisplayName("Should throw exception when goal not found")
    void shouldThrowExceptionWhenGoalNotFound() {
        // Given
        when(goalRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () ->
            goalService.getGoalById(1L, 999L)
        );
    }
}

package com.earthlog.service;

import com.earthlog.dto.request.ActivityRequest;
import com.earthlog.dto.response.ActivityResponse;
import com.earthlog.entity.Activity;
import com.earthlog.entity.User;
import com.earthlog.enums.ActivityCategory;
import com.earthlog.enums.ActivityType;
import com.earthlog.enums.Role;
import com.earthlog.exception.ResourceNotFoundException;
import com.earthlog.exception.UnauthorizedException;
import com.earthlog.repository.ActivityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Activity Service Tests")
class ActivityServiceTest {

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private EmissionCalculatorService emissionCalculatorService;

    @Mock
    private UserService userService;

    @InjectMocks
    private ActivityService activityService;

    private User testUser;
    private Activity testActivity;
    private ActivityRequest testRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
            .id(1L)
            .email("test@example.com")
            .name("Test User")
            .country("DE")
            .role(Role.USER)
            .build();

        testActivity = Activity.builder()
            .id(1L)
            .user(testUser)
            .category(ActivityCategory.TRANSPORT)
            .activityType(ActivityType.CAR_PETROL)
            .quantity(new BigDecimal("50"))
            .unit("KM")
            .calculatedCo2(new BigDecimal("9.5"))
            .date(LocalDate.now())
            .notes("Commute")
            .build();

        testRequest = ActivityRequest.builder()
            .category(ActivityCategory.TRANSPORT)
            .activityType(ActivityType.CAR_PETROL)
            .quantity(new BigDecimal("50"))
            .unit("KM")
            .date(LocalDate.now())
            .notes("Commute")
            .build();
    }

    @Test
    @DisplayName("Should create activity and calculate CO2")
    void shouldCreateActivityAndCalculateCo2() {
        // Given
        when(userService.findById(1L)).thenReturn(testUser);
        when(emissionCalculatorService.calculateEmission(
            eq(ActivityType.CAR_PETROL),
            eq(new BigDecimal("50")),
            eq("DE")
        )).thenReturn(new BigDecimal("9.5000"));
        when(activityRepository.save(any(Activity.class))).thenReturn(testActivity);

        // When
        ActivityResponse result = activityService.createActivity(1L, testRequest);

        // Then
        assertNotNull(result);
        assertEquals(ActivityCategory.TRANSPORT, result.getCategory());
        assertEquals(ActivityType.CAR_PETROL, result.getActivityType());
        verify(emissionCalculatorService, times(1)).calculateEmission(any(), any(), any());
        verify(activityRepository, times(1)).save(any(Activity.class));
    }

    @Test
    @DisplayName("Should get paginated activities for user")
    void shouldGetPaginatedActivitiesForUser() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Activity> activityPage = new PageImpl<>(List.of(testActivity));
        when(activityRepository.findByUserId(1L, pageable)).thenReturn(activityPage);

        // When
        var result = activityService.getActivities(1L, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
    }

    @Test
    @DisplayName("Should get activity by ID")
    void shouldGetActivityById() {
        // Given
        when(activityRepository.findById(1L)).thenReturn(Optional.of(testActivity));

        // When
        ActivityResponse result = activityService.getActivityById(1L, 1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(ActivityType.CAR_PETROL, result.getActivityType());
    }

    @Test
    @DisplayName("Should throw exception when activity not found")
    void shouldThrowExceptionWhenActivityNotFound() {
        // Given
        when(activityRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () ->
            activityService.getActivityById(1L, 999L)
        );
    }

    @Test
    @DisplayName("Should throw exception when user doesn't own activity")
    void shouldThrowExceptionWhenUserDoesntOwnActivity() {
        // Given
        User otherUser = User.builder().id(2L).build();
        Activity otherActivity = Activity.builder()
            .id(1L)
            .user(otherUser)
            .build();
        when(activityRepository.findById(1L)).thenReturn(Optional.of(otherActivity));

        // When & Then
        assertThrows(UnauthorizedException.class, () ->
            activityService.getActivityById(1L, 1L)
        );
    }

    @Test
    @DisplayName("Should update activity")
    void shouldUpdateActivity() {
        // Given
        when(activityRepository.findById(1L)).thenReturn(Optional.of(testActivity));
        when(emissionCalculatorService.calculateEmission(any(), any(), any()))
            .thenReturn(new BigDecimal("10.0000"));
        when(activityRepository.save(any(Activity.class))).thenReturn(testActivity);

        ActivityRequest updateRequest = ActivityRequest.builder()
            .category(ActivityCategory.TRANSPORT)
            .activityType(ActivityType.CAR_DIESEL)
            .quantity(new BigDecimal("60"))
            .date(LocalDate.now())
            .build();

        // When
        ActivityResponse result = activityService.updateActivity(1L, 1L, updateRequest);

        // Then
        assertNotNull(result);
        verify(activityRepository, times(1)).save(any(Activity.class));
    }

    @Test
    @DisplayName("Should delete activity")
    void shouldDeleteActivity() {
        // Given
        when(activityRepository.findById(1L)).thenReturn(Optional.of(testActivity));
        doNothing().when(activityRepository).delete(testActivity);

        // When
        activityService.deleteActivity(1L, 1L);

        // Then
        verify(activityRepository, times(1)).delete(testActivity);
    }

    @Test
    @DisplayName("Should get total CO2 by date range")
    void shouldGetTotalCo2ByDateRange() {
        // Given
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        when(activityRepository.sumCo2ByUserIdAndDateBetween(1L, startDate, endDate))
            .thenReturn(new BigDecimal("50.5"));

        // When
        BigDecimal result = activityService.getTotalCo2ByDateRange(1L, startDate, endDate);

        // Then
        assertEquals(new BigDecimal("50.5"), result);
    }

    @Test
    @DisplayName("Should return zero when no activities in date range")
    void shouldReturnZeroWhenNoActivitiesInDateRange() {
        // Given
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        when(activityRepository.sumCo2ByUserIdAndDateBetween(1L, startDate, endDate))
            .thenReturn(null);

        // When
        BigDecimal result = activityService.getTotalCo2ByDateRange(1L, startDate, endDate);

        // Then
        assertEquals(BigDecimal.ZERO, result);
    }
}

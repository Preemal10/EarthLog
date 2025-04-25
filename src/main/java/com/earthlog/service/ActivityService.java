package com.earthlog.service;

import com.earthlog.dto.request.ActivityRequest;
import com.earthlog.dto.response.ActivityResponse;
import com.earthlog.dto.response.PageResponse;
import com.earthlog.entity.Activity;
import com.earthlog.entity.User;
import com.earthlog.enums.ActivityCategory;
import com.earthlog.event.ActivityCreatedEvent;
import com.earthlog.exception.ResourceNotFoundException;
import com.earthlog.exception.UnauthorizedException;
import com.earthlog.messaging.EventPublisher;
import com.earthlog.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final EmissionCalculatorService emissionCalculatorService;
    private final UserService userService;
    private final EventPublisher eventPublisher;

    @Transactional
    public ActivityResponse createActivity(Long userId, ActivityRequest request) {
        User user = userService.findById(userId);

        // Calculate CO2 emission
        BigDecimal calculatedCo2 = emissionCalculatorService.calculateEmission(
            request.getActivityType(),
            request.getQuantity(),
            user.getCountry()
        );

        // Determine unit (use default if not provided)
        String unit = request.getUnit() != null 
            ? request.getUnit() 
            : request.getActivityType().getDefaultUnit();

        Activity activity = Activity.builder()
            .user(user)
            .category(request.getCategory())
            .activityType(request.getActivityType())
            .quantity(request.getQuantity())
            .unit(unit)
            .calculatedCo2(calculatedCo2)
            .date(request.getDate())
            .notes(request.getNotes())
            .build();

        Activity saved = activityRepository.save(activity);
        log.info("Created activity {} for user {}", saved.getId(), userId);

        // Publish event for async processing (goal monitoring, badges, etc.)
        publishActivityCreatedEvent(saved, user);

        return toResponse(saved);
    }

    /**
     * Publish an event when an activity is created.
     * This triggers async processing for goals, badges, and notifications.
     */
    private void publishActivityCreatedEvent(Activity activity, User user) {
        try {
            ActivityCreatedEvent event = ActivityCreatedEvent.of(
                activity.getId(),
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getCountry(),
                activity.getCategory(),
                activity.getActivityType(),
                activity.getQuantity(),
                activity.getUnit(),
                activity.getCalculatedCo2(),
                activity.getDate(),
                activity.getNotes()
            );

            eventPublisher.publishActivityCreated(event);
            log.debug("Published activity created event for activity {}", activity.getId());

        } catch (Exception e) {
            // Don't fail the main transaction if event publishing fails
            log.error("Failed to publish activity created event for activity {}: {}", 
                activity.getId(), e.getMessage());
        }
    }

    public PageResponse<ActivityResponse> getActivities(Long userId, Pageable pageable) {
        Page<Activity> page = activityRepository.findByUserId(userId, pageable);
        List<ActivityResponse> responses = page.getContent().stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
        return PageResponse.of(page, responses);
    }

    public PageResponse<ActivityResponse> getActivitiesByCategory(
            Long userId, ActivityCategory category, Pageable pageable) {
        Page<Activity> page = activityRepository.findByUserIdAndCategory(userId, category, pageable);
        List<ActivityResponse> responses = page.getContent().stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
        return PageResponse.of(page, responses);
    }

    public PageResponse<ActivityResponse> getActivitiesByDateRange(
            Long userId, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        Page<Activity> page = activityRepository.findByUserIdAndDateBetween(
            userId, startDate, endDate, pageable);
        List<ActivityResponse> responses = page.getContent().stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
        return PageResponse.of(page, responses);
    }

    public ActivityResponse getActivityById(Long userId, Long activityId) {
        Activity activity = findByIdAndValidateOwnership(userId, activityId);
        return toResponse(activity);
    }

    @Transactional
    public ActivityResponse updateActivity(Long userId, Long activityId, ActivityRequest request) {
        Activity activity = findByIdAndValidateOwnership(userId, activityId);
        User user = activity.getUser();

        // Recalculate CO2 if activity type or quantity changed
        BigDecimal calculatedCo2 = emissionCalculatorService.calculateEmission(
            request.getActivityType(),
            request.getQuantity(),
            user.getCountry()
        );

        String unit = request.getUnit() != null 
            ? request.getUnit() 
            : request.getActivityType().getDefaultUnit();

        activity.setCategory(request.getCategory());
        activity.setActivityType(request.getActivityType());
        activity.setQuantity(request.getQuantity());
        activity.setUnit(unit);
        activity.setCalculatedCo2(calculatedCo2);
        activity.setDate(request.getDate());
        activity.setNotes(request.getNotes());

        Activity saved = activityRepository.save(activity);
        log.info("Updated activity {} for user {}", activityId, userId);

        return toResponse(saved);
    }

    @Transactional
    public void deleteActivity(Long userId, Long activityId) {
        Activity activity = findByIdAndValidateOwnership(userId, activityId);
        activityRepository.delete(activity);
        log.info("Deleted activity {} for user {}", activityId, userId);
    }

    public BigDecimal getTotalCo2ByDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        BigDecimal total = activityRepository.sumCo2ByUserIdAndDateBetween(userId, startDate, endDate);
        return total != null ? total : BigDecimal.ZERO;
    }

    public Long getActivityCountByDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        return activityRepository.countByUserIdAndDateBetween(userId, startDate, endDate);
    }

    private Activity findByIdAndValidateOwnership(Long userId, Long activityId) {
        Activity activity = activityRepository.findById(activityId)
            .orElseThrow(() -> new ResourceNotFoundException("Activity", "id", activityId));

        if (!activity.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You don't have permission to access this activity");
        }

        return activity;
    }

    private ActivityResponse toResponse(Activity activity) {
        return ActivityResponse.builder()
            .id(activity.getId())
            .category(activity.getCategory())
            .activityType(activity.getActivityType())
            .quantity(activity.getQuantity())
            .unit(activity.getUnit())
            .calculatedCo2(activity.getCalculatedCo2())
            .date(activity.getDate())
            .notes(activity.getNotes())
            .createdAt(activity.getCreatedAt())
            .build();
    }
}

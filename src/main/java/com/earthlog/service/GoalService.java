package com.earthlog.service;

import com.earthlog.dto.request.GoalRequest;
import com.earthlog.dto.response.GoalResponse;
import com.earthlog.entity.Goal;
import com.earthlog.entity.User;
import com.earthlog.exception.BadRequestException;
import com.earthlog.exception.ResourceNotFoundException;
import com.earthlog.exception.UnauthorizedException;
import com.earthlog.repository.GoalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoalService {

    private final GoalRepository goalRepository;
    private final UserService userService;
    private final ActivityService activityService;

    @Transactional
    public GoalResponse createGoal(Long userId, GoalRequest request) {
        User user = userService.findById(userId);

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BadRequestException("End date must be after start date");
        }

        Goal goal = Goal.builder()
            .user(user)
            .targetEmission(request.getTargetEmission())
            .period(request.getPeriod())
            .startDate(request.getStartDate())
            .endDate(request.getEndDate())
            .isActive(true)
            .build();

        Goal saved = goalRepository.save(goal);
        log.info("Created goal {} for user {}", saved.getId(), userId);

        return toResponse(saved, userId);
    }

    public List<GoalResponse> getGoals(Long userId) {
        return goalRepository.findByUserId(userId).stream()
            .map(goal -> toResponse(goal, userId))
            .collect(Collectors.toList());
    }

    public List<GoalResponse> getActiveGoals(Long userId) {
        return goalRepository.findByUserIdAndIsActiveTrue(userId).stream()
            .map(goal -> toResponse(goal, userId))
            .collect(Collectors.toList());
    }

    public GoalResponse getGoalById(Long userId, Long goalId) {
        Goal goal = findByIdAndValidateOwnership(userId, goalId);
        return toResponse(goal, userId);
    }

    @Transactional
    public GoalResponse updateGoal(Long userId, Long goalId, GoalRequest request) {
        Goal goal = findByIdAndValidateOwnership(userId, goalId);

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BadRequestException("End date must be after start date");
        }

        goal.setTargetEmission(request.getTargetEmission());
        goal.setPeriod(request.getPeriod());
        goal.setStartDate(request.getStartDate());
        goal.setEndDate(request.getEndDate());

        Goal saved = goalRepository.save(goal);
        log.info("Updated goal {} for user {}", goalId, userId);

        return toResponse(saved, userId);
    }

    @Transactional
    public void deleteGoal(Long userId, Long goalId) {
        Goal goal = findByIdAndValidateOwnership(userId, goalId);
        goalRepository.delete(goal);
        log.info("Deleted goal {} for user {}", goalId, userId);
    }

    public int countActiveGoals(Long userId) {
        return goalRepository.findByUserIdAndIsActiveTrue(userId).size();
    }

    private Goal findByIdAndValidateOwnership(Long userId, Long goalId) {
        Goal goal = goalRepository.findById(goalId)
            .orElseThrow(() -> new ResourceNotFoundException("Goal", "id", goalId));

        if (!goal.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You don't have permission to access this goal");
        }

        return goal;
    }

    private GoalResponse toResponse(Goal goal, Long userId) {
        LocalDate today = LocalDate.now();
        LocalDate effectiveEndDate = goal.getEndDate().isBefore(today) ? goal.getEndDate() : today;
        LocalDate effectiveStartDate = goal.getStartDate().isAfter(today) ? today : goal.getStartDate();

        // Calculate current emission within goal period
        BigDecimal currentEmission = activityService.getTotalCo2ByDateRange(
            userId, 
            goal.getStartDate(), 
            effectiveEndDate
        );

        // Calculate progress percentage
        BigDecimal progressPercentage = BigDecimal.ZERO;
        if (goal.getTargetEmission().compareTo(BigDecimal.ZERO) > 0) {
            progressPercentage = currentEmission
                .divide(goal.getTargetEmission(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
        }

        // Calculate days remaining
        long daysRemaining = ChronoUnit.DAYS.between(today, goal.getEndDate());
        if (daysRemaining < 0) daysRemaining = 0;

        // Determine if on track (simple: if current < target, we're on track)
        boolean isOnTrack = currentEmission.compareTo(goal.getTargetEmission()) <= 0;

        return GoalResponse.builder()
            .id(goal.getId())
            .targetEmission(goal.getTargetEmission())
            .currentEmission(currentEmission)
            .period(goal.getPeriod())
            .startDate(goal.getStartDate())
            .endDate(goal.getEndDate())
            .progressPercentage(progressPercentage)
            .isOnTrack(isOnTrack)
            .daysRemaining(daysRemaining)
            .isActive(goal.getIsActive())
            .createdAt(goal.getCreatedAt())
            .build();
    }
}

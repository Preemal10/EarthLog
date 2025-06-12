package com.earthlog.event;

import com.earthlog.enums.GoalPeriod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Event for monitoring goal progress.
 * Triggered when an activity is logged to check if user is approaching/exceeding goals.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalProgressEvent implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private Long goalId;
    private Long userId;
    private String userEmail;
    private String userName;
    
    private GoalPeriod period;
    private BigDecimal targetEmission;
    private BigDecimal currentEmission;
    private BigDecimal progressPercentage;
    
    private LocalDate periodStart;
    private LocalDate periodEnd;
    
    private GoalStatus status;
    private LocalDateTime timestamp;
    
    public enum GoalStatus {
        ON_TRACK,           // < 75% of target
        APPROACHING_LIMIT,  // 75-90% of target
        NEAR_LIMIT,         // 90-100% of target
        EXCEEDED            // > 100% of target
    }
    
    public static GoalProgressEvent create(Long goalId, Long userId, String userEmail,
            String userName, GoalPeriod period, BigDecimal targetEmission,
            BigDecimal currentEmission, LocalDate periodStart, LocalDate periodEnd) {
        
        BigDecimal percentage = currentEmission
                .divide(targetEmission, 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
        
        GoalStatus status;
        if (percentage.compareTo(BigDecimal.valueOf(100)) > 0) {
            status = GoalStatus.EXCEEDED;
        } else if (percentage.compareTo(BigDecimal.valueOf(90)) >= 0) {
            status = GoalStatus.NEAR_LIMIT;
        } else if (percentage.compareTo(BigDecimal.valueOf(75)) >= 0) {
            status = GoalStatus.APPROACHING_LIMIT;
        } else {
            status = GoalStatus.ON_TRACK;
        }
        
        return GoalProgressEvent.builder()
                .goalId(goalId)
                .userId(userId)
                .userEmail(userEmail)
                .userName(userName)
                .period(period)
                .targetEmission(targetEmission)
                .currentEmission(currentEmission)
                .progressPercentage(percentage)
                .periodStart(periodStart)
                .periodEnd(periodEnd)
                .status(status)
                .timestamp(LocalDateTime.now())
                .build();
    }
}

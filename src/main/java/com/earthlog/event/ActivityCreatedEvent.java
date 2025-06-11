package com.earthlog.event;

import com.earthlog.enums.ActivityCategory;
import com.earthlog.enums.ActivityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Event published when a new activity is logged.
 * Used to trigger async processing for analytics, goal monitoring, and badge evaluation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityCreatedEvent implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private Long activityId;
    private Long userId;
    private String userEmail;
    private String userName;
    private String userCountry;
    
    private ActivityCategory category;
    private ActivityType activityType;
    private BigDecimal quantity;
    private String unit;
    private BigDecimal calculatedCo2;
    private LocalDate activityDate;
    private String notes;
    
    private LocalDateTime timestamp;
    
    public static ActivityCreatedEvent of(Long activityId, Long userId, String userEmail, 
            String userName, String userCountry, ActivityCategory category, 
            ActivityType activityType, BigDecimal quantity, String unit, 
            BigDecimal calculatedCo2, LocalDate activityDate, String notes) {
        return ActivityCreatedEvent.builder()
                .activityId(activityId)
                .userId(userId)
                .userEmail(userEmail)
                .userName(userName)
                .userCountry(userCountry)
                .category(category)
                .activityType(activityType)
                .quantity(quantity)
                .unit(unit)
                .calculatedCo2(calculatedCo2)
                .activityDate(activityDate)
                .notes(notes)
                .timestamp(LocalDateTime.now())
                .build();
    }
}

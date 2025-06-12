package com.earthlog.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Event for evaluating badge/achievement criteria.
 * Triggered after activity logging to check if user qualifies for any badges.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BadgeEvaluationEvent implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private Long userId;
    private String userEmail;
    private String userName;
    
    // User statistics for badge evaluation
    private int totalActivitiesLogged;
    private BigDecimal totalCo2ThisWeek;
    private BigDecimal totalCo2ThisMonth;
    private BigDecimal totalCo2AllTime;
    
    // Activity counts by category
    private Map<String, Integer> activitiesByCategory;
    
    // Streak information
    private int currentStreak;  // consecutive days with activities
    private int longestStreak;
    
    // Eco-friendly activities
    private int bikeActivities;
    private int walkActivities;
    private int vegetarianMeals;
    private int veganMeals;
    private int publicTransportTrips;
    
    // Goal achievements
    private int goalsAchieved;
    private int challengesCompleted;
    
    private LocalDateTime timestamp;
    
    public static BadgeEvaluationEvent forUser(Long userId, String userEmail, String userName) {
        return BadgeEvaluationEvent.builder()
                .userId(userId)
                .userEmail(userEmail)
                .userName(userName)
                .timestamp(LocalDateTime.now())
                .build();
    }
}

package com.earthlog.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Event for sending email notifications.
 * Supports different email types with template-based content.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailNotificationEvent implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private String to;
    private String toName;
    private String subject;
    private EmailType type;
    private Map<String, Object> templateData;
    private LocalDateTime timestamp;
    
    public enum EmailType {
        WELCOME,
        ACTIVITY_LOGGED,
        GOAL_APPROACHING,
        GOAL_EXCEEDED,
        BADGE_EARNED,
        WEEKLY_SUMMARY,
        CHALLENGE_JOINED,
        CHALLENGE_COMPLETED
    }
    
    public static EmailNotificationEvent welcome(String to, String toName) {
        return EmailNotificationEvent.builder()
                .to(to)
                .toName(toName)
                .subject("Welcome to EarthLog!")
                .type(EmailType.WELCOME)
                .templateData(Map.of("name", toName))
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    public static EmailNotificationEvent goalApproaching(String to, String toName, 
            String period, double currentPercentage, double targetKg) {
        return EmailNotificationEvent.builder()
                .to(to)
                .toName(toName)
                .subject("Goal Alert: You're approaching your " + period + " limit")
                .type(EmailType.GOAL_APPROACHING)
                .templateData(Map.of(
                        "name", toName,
                        "period", period,
                        "percentage", currentPercentage,
                        "target", targetKg
                ))
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    public static EmailNotificationEvent goalExceeded(String to, String toName,
            String period, double currentKg, double targetKg) {
        return EmailNotificationEvent.builder()
                .to(to)
                .toName(toName)
                .subject("Goal Alert: You've exceeded your " + period + " target")
                .type(EmailType.GOAL_EXCEEDED)
                .templateData(Map.of(
                        "name", toName,
                        "period", period,
                        "current", currentKg,
                        "target", targetKg,
                        "excess", currentKg - targetKg
                ))
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    public static EmailNotificationEvent badgeEarned(String to, String toName,
            String badgeName, String badgeDescription) {
        return EmailNotificationEvent.builder()
                .to(to)
                .toName(toName)
                .subject("Congratulations! You've earned a new badge: " + badgeName)
                .type(EmailType.BADGE_EARNED)
                .templateData(Map.of(
                        "name", toName,
                        "badgeName", badgeName,
                        "badgeDescription", badgeDescription
                ))
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    public static EmailNotificationEvent weeklySummary(String to, String toName,
            Map<String, Object> summaryData) {
        return EmailNotificationEvent.builder()
                .to(to)
                .toName(toName)
                .subject("Your Weekly Carbon Footprint Summary")
                .type(EmailType.WEEKLY_SUMMARY)
                .templateData(summaryData)
                .timestamp(LocalDateTime.now())
                .build();
    }
}

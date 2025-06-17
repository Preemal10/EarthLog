package com.earthlog.service;

import com.earthlog.entity.Badge;
import com.earthlog.entity.User;
import com.earthlog.event.BadgeEvaluationEvent;
import com.earthlog.repository.BadgeRepository;
import com.earthlog.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Service for evaluating and awarding badges to users.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BadgeService {

    private final BadgeRepository badgeRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    // Badge criteria constants
    private static final int FIRST_ACTIVITY_COUNT = 1;
    private static final int ACTIVITY_STARTER_COUNT = 10;
    private static final int ACTIVITY_TRACKER_COUNT = 50;
    private static final int ACTIVITY_MASTER_COUNT = 100;
    private static final int ECO_WARRIOR_COUNT = 500;
    
    private static final int BIKE_ENTHUSIAST_COUNT = 20;
    private static final int WALKING_CHAMPION_COUNT = 30;
    private static final int PLANT_LOVER_COUNT = 25;  // vegetarian + vegan meals
    
    private static final int STREAK_STARTER_DAYS = 7;
    private static final int STREAK_MASTER_DAYS = 30;

    /**
     * Evaluate badge criteria and award new badges to user.
     * Returns list of newly awarded badges.
     */
    @Transactional
    public List<AwardedBadge> evaluateAndAwardBadges(BadgeEvaluationEvent event) {
        List<AwardedBadge> newlyAwarded = new ArrayList<>();

        Optional<User> userOpt = userRepository.findById(event.getUserId());
        if (userOpt.isEmpty()) {
            log.warn("User {} not found for badge evaluation", event.getUserId());
            return newlyAwarded;
        }

        User user = userOpt.get();
        Set<Badge> existingBadges = user.getBadges();

        // Activity count badges
        checkAndAwardActivityBadges(event, user, existingBadges, newlyAwarded);

        // Eco-friendly activity badges
        checkAndAwardEcoBadges(event, user, existingBadges, newlyAwarded);

        // Streak badges
        checkAndAwardStreakBadges(event, user, existingBadges, newlyAwarded);

        // Save user if badges were awarded
        if (!newlyAwarded.isEmpty()) {
            userRepository.save(user);

            // Create in-app notifications
            for (AwardedBadge badge : newlyAwarded) {
                notificationService.createBadgeNotification(
                        user.getId(),
                        badge.getId(),
                        badge.getName(),
                        badge.getDescription()
                );
            }
        }

        return newlyAwarded;
    }

    private void checkAndAwardActivityBadges(BadgeEvaluationEvent event, User user,
            Set<Badge> existingBadges, List<AwardedBadge> newlyAwarded) {
        
        int totalActivities = event.getTotalActivitiesLogged();

        // First Activity
        if (totalActivities >= FIRST_ACTIVITY_COUNT) {
            awardBadgeIfNotExists("FIRST_STEP", "First Step", 
                    "Logged your first activity", user, existingBadges, newlyAwarded);
        }

        // 10 activities
        if (totalActivities >= ACTIVITY_STARTER_COUNT) {
            awardBadgeIfNotExists("ACTIVITY_STARTER", "Activity Starter",
                    "Logged 10 activities", user, existingBadges, newlyAwarded);
        }

        // 50 activities
        if (totalActivities >= ACTIVITY_TRACKER_COUNT) {
            awardBadgeIfNotExists("ACTIVITY_TRACKER", "Activity Tracker",
                    "Logged 50 activities - You're building great habits!", 
                    user, existingBadges, newlyAwarded);
        }

        // 100 activities
        if (totalActivities >= ACTIVITY_MASTER_COUNT) {
            awardBadgeIfNotExists("ACTIVITY_MASTER", "Activity Master",
                    "Logged 100 activities - A true environmental champion!", 
                    user, existingBadges, newlyAwarded);
        }

        // 500 activities
        if (totalActivities >= ECO_WARRIOR_COUNT) {
            awardBadgeIfNotExists("ECO_WARRIOR", "Eco Warrior",
                    "Logged 500 activities - You're a carbon tracking legend!", 
                    user, existingBadges, newlyAwarded);
        }
    }

    private void checkAndAwardEcoBadges(BadgeEvaluationEvent event, User user,
            Set<Badge> existingBadges, List<AwardedBadge> newlyAwarded) {

        // Bike enthusiast
        if (event.getBikeActivities() >= BIKE_ENTHUSIAST_COUNT) {
            awardBadgeIfNotExists("BIKE_ENTHUSIAST", "Bike Enthusiast",
                    "Logged 20 bike trips - Pedaling towards a greener future!",
                    user, existingBadges, newlyAwarded);
        }

        // Walking champion
        if (event.getWalkActivities() >= WALKING_CHAMPION_COUNT) {
            awardBadgeIfNotExists("WALKING_CHAMPION", "Walking Champion",
                    "Logged 30 walks - Every step counts for the planet!",
                    user, existingBadges, newlyAwarded);
        }

        // Plant lover (vegetarian + vegan meals)
        int plantBasedMeals = event.getVegetarianMeals() + event.getVeganMeals();
        if (plantBasedMeals >= PLANT_LOVER_COUNT) {
            awardBadgeIfNotExists("PLANT_LOVER", "Plant Lover",
                    "Logged 25 plant-based meals - Your diet choices make a difference!",
                    user, existingBadges, newlyAwarded);
        }

        // Public transport hero
        if (event.getPublicTransportTrips() >= 15) {
            awardBadgeIfNotExists("PUBLIC_TRANSPORT_HERO", "Public Transport Hero",
                    "Logged 15 public transport trips - Sharing the ride, saving the planet!",
                    user, existingBadges, newlyAwarded);
        }
    }

    private void checkAndAwardStreakBadges(BadgeEvaluationEvent event, User user,
            Set<Badge> existingBadges, List<AwardedBadge> newlyAwarded) {

        int currentStreak = event.getCurrentStreak();

        // 7-day streak
        if (currentStreak >= STREAK_STARTER_DAYS) {
            awardBadgeIfNotExists("STREAK_STARTER", "Streak Starter",
                    "Logged activities for 7 consecutive days - Consistency is key!",
                    user, existingBadges, newlyAwarded);
        }

        // 30-day streak
        if (currentStreak >= STREAK_MASTER_DAYS) {
            awardBadgeIfNotExists("STREAK_MASTER", "Streak Master",
                    "Logged activities for 30 consecutive days - Incredible dedication!",
                    user, existingBadges, newlyAwarded);
        }
    }

    private void awardBadgeIfNotExists(String badgeName, String displayName, String description,
            User user, Set<Badge> existingBadges, List<AwardedBadge> newlyAwarded) {

        // Check if user already has this badge
        boolean alreadyHas = existingBadges.stream()
                .anyMatch(b -> b.getName().equals(badgeName));

        if (alreadyHas) {
            return;
        }

        // Find or create badge
        Badge badge = badgeRepository.findByName(badgeName)
                .orElseGet(() -> {
                    Badge newBadge = Badge.builder()
                            .name(badgeName)
                            .description(description)
                            .criteria(displayName)
                            .build();
                    return badgeRepository.save(newBadge);
                });

        // Award badge to user
        user.getBadges().add(badge);
        newlyAwarded.add(new AwardedBadge(badge.getId(), badgeName, displayName, description));

        log.info("Awarded badge '{}' to user {}", displayName, user.getId());
    }

    /**
     * Get all badges for a user.
     */
    @Transactional(readOnly = true)
    public Set<Badge> getUserBadges(Long userId) {
        return userRepository.findById(userId)
                .map(User::getBadges)
                .orElse(Set.of());
    }

    /**
     * Get all available badges.
     */
    @Transactional(readOnly = true)
    public List<Badge> getAllBadges() {
        return badgeRepository.findAll();
    }

    /**
     * DTO for newly awarded badge.
     */
    @Data
    @AllArgsConstructor
    public static class AwardedBadge {
        private Long id;
        private String name;
        private String displayName;
        private String description;
    }
}

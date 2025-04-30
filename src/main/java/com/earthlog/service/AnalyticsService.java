package com.earthlog.service;

import com.earthlog.dto.response.BreakdownResponse;
import com.earthlog.dto.response.CompareResponse;
import com.earthlog.dto.response.DashboardResponse;
import com.earthlog.dto.response.TrendResponse;
import com.earthlog.enums.ActivityCategory;
import com.earthlog.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsService {

    private final ActivityRepository activityRepository;
    private final ActivityService activityService;
    private final GoalService goalService;

    public DashboardResponse getDashboard(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate monthStart = today.withDayOfMonth(1);

        // Today's summary
        BigDecimal todayCo2 = activityService.getTotalCo2ByDateRange(userId, today, today);
        Long todayCount = activityService.getActivityCountByDateRange(userId, today, today);

        // This week's summary
        BigDecimal weekCo2 = activityService.getTotalCo2ByDateRange(userId, weekStart, today);
        Long weekCount = activityService.getActivityCountByDateRange(userId, weekStart, today);

        // This month's summary
        BigDecimal monthCo2 = activityService.getTotalCo2ByDateRange(userId, monthStart, today);
        Long monthCount = activityService.getActivityCountByDateRange(userId, monthStart, today);

        // Find top category this month
        List<Object[]> categoryBreakdown = activityRepository
            .sumCo2ByUserIdAndDateBetweenGroupByCategory(userId, monthStart, today);
        
        String topCategory = "N/A";
        if (!categoryBreakdown.isEmpty()) {
            topCategory = categoryBreakdown.stream()
                .max((a, b) -> ((BigDecimal) a[1]).compareTo((BigDecimal) b[1]))
                .map(arr -> ((ActivityCategory) arr[0]).name())
                .orElse("N/A");
        }

        // Count active goals
        int activeGoals = goalService.countActiveGoals(userId);

        return DashboardResponse.builder()
            .today(DashboardResponse.PeriodSummary.builder()
                .totalCo2(todayCo2)
                .activitiesCount(todayCount)
                .build())
            .thisWeek(DashboardResponse.PeriodSummary.builder()
                .totalCo2(weekCo2)
                .activitiesCount(weekCount)
                .build())
            .thisMonth(DashboardResponse.PeriodSummary.builder()
                .totalCo2(monthCo2)
                .activitiesCount(monthCount)
                .build())
            .topCategory(topCategory)
            .streak(0) // TODO: Implement streak calculation
            .activeGoals(activeGoals)
            .build();
    }

    public BreakdownResponse getBreakdown(Long userId, LocalDate startDate, LocalDate endDate) {
        List<Object[]> categoryData = activityRepository
            .sumCo2ByUserIdAndDateBetweenGroupByCategory(userId, startDate, endDate);

        BigDecimal totalCo2 = categoryData.stream()
            .map(arr -> (BigDecimal) arr[1])
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<BreakdownResponse.CategoryBreakdown> breakdown = categoryData.stream()
            .map(arr -> {
                ActivityCategory category = (ActivityCategory) arr[0];
                BigDecimal co2 = (BigDecimal) arr[1];
                BigDecimal percentage = totalCo2.compareTo(BigDecimal.ZERO) > 0
                    ? co2.divide(totalCo2, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .setScale(2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;

                return BreakdownResponse.CategoryBreakdown.builder()
                    .category(category)
                    .co2(co2)
                    .percentage(percentage)
                    .build();
            })
            .collect(Collectors.toList());

        return BreakdownResponse.builder()
            .period(startDate + " to " + endDate)
            .totalCo2(totalCo2)
            .breakdown(breakdown)
            .build();
    }

    public TrendResponse getTrends(Long userId, LocalDate startDate, LocalDate endDate) {
        List<Object[]> dailyData = activityRepository
            .sumCo2ByUserIdAndDateBetweenGroupByDate(userId, startDate, endDate);

        // Convert to map for easy lookup
        Map<LocalDate, BigDecimal> dataMap = dailyData.stream()
            .collect(Collectors.toMap(
                arr -> (LocalDate) arr[0],
                arr -> (BigDecimal) arr[1]
            ));

        // Fill in all dates (including zeros)
        List<TrendResponse.TrendPoint> trends = new ArrayList<>();
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            BigDecimal co2 = dataMap.getOrDefault(current, BigDecimal.ZERO);
            trends.add(TrendResponse.TrendPoint.builder()
                .date(current)
                .co2(co2)
                .build());
            current = current.plusDays(1);
        }

        return TrendResponse.builder()
            .startDate(startDate)
            .endDate(endDate)
            .trends(trends)
            .build();
    }

    public CompareResponse compare(Long userId, LocalDate startDate, LocalDate endDate) {
        // Calculate user's daily average
        BigDecimal userTotal = activityService.getTotalCo2ByDateRange(userId, startDate, endDate);
        long days = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
        BigDecimal userDailyAverage = userTotal.divide(BigDecimal.valueOf(days), 4, RoundingMode.HALF_UP);

        // Get platform average
        BigDecimal platformAverage = activityRepository.findAverageDailyCo2(startDate, endDate);
        if (platformAverage == null) {
            platformAverage = BigDecimal.ZERO;
        }

        // Calculate difference
        BigDecimal difference = BigDecimal.ZERO;
        String comparison = "AVERAGE";
        
        if (platformAverage.compareTo(BigDecimal.ZERO) > 0) {
            difference = userDailyAverage.subtract(platformAverage)
                .divide(platformAverage, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);

            if (difference.compareTo(BigDecimal.valueOf(-5)) < 0) {
                comparison = "BELOW_AVERAGE";
            } else if (difference.compareTo(BigDecimal.valueOf(5)) > 0) {
                comparison = "ABOVE_AVERAGE";
            }
        }

        return CompareResponse.builder()
            .userDailyAverage(userDailyAverage)
            .platformDailyAverage(platformAverage)
            .differencePercentage(difference)
            .comparison(comparison)
            .build();
    }
}

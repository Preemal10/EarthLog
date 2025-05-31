package com.earthlog.controller;

import com.earthlog.dto.response.*;
import com.earthlog.entity.User;
import com.earthlog.security.CurrentUser;
import com.earthlog.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics", description = "Carbon footprint analytics and insights")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/dashboard")
    @Operation(summary = "Get dashboard summary", description = "Get overview of today, this week, and this month's emissions")
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard(@CurrentUser User user) {
        DashboardResponse response = analyticsService.getDashboard(user.getId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/breakdown")
    @Operation(summary = "Get category breakdown", description = "Get emissions breakdown by category for a date range")
    public ResponseEntity<ApiResponse<BreakdownResponse>> getBreakdown(
            @CurrentUser User user,
            @Parameter(description = "Start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        BreakdownResponse response = analyticsService.getBreakdown(user.getId(), startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/trends")
    @Operation(summary = "Get emission trends", description = "Get daily emission trends for a date range")
    public ResponseEntity<ApiResponse<TrendResponse>> getTrends(
            @CurrentUser User user,
            @Parameter(description = "Start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        TrendResponse response = analyticsService.getTrends(user.getId(), startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/compare")
    @Operation(summary = "Compare with platform average", description = "Compare your emissions with the platform average")
    public ResponseEntity<ApiResponse<CompareResponse>> compare(
            @CurrentUser User user,
            @Parameter(description = "Start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        CompareResponse response = analyticsService.compare(user.getId(), startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}

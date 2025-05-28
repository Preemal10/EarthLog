package com.earthlog.controller;

import com.earthlog.dto.request.ActivityRequest;
import com.earthlog.dto.response.ActivityResponse;
import com.earthlog.dto.response.ApiResponse;
import com.earthlog.dto.response.PageResponse;
import com.earthlog.entity.User;
import com.earthlog.enums.ActivityCategory;
import com.earthlog.security.CurrentUser;
import com.earthlog.service.ActivityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
@Tag(name = "Activities", description = "Activity logging and management")
public class ActivityController {

    private final ActivityService activityService;

    @PostMapping
    @Operation(summary = "Log a new activity", description = "Log a new activity and automatically calculate CO2 emission")
    public ResponseEntity<ApiResponse<ActivityResponse>> createActivity(
            @CurrentUser User user,
            @Valid @RequestBody ActivityRequest request) {
        ActivityResponse response = activityService.createActivity(user.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Activity logged successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get activities", description = "Get paginated list of activities with optional filters")
    public ResponseEntity<ApiResponse<PageResponse<ActivityResponse>>> getActivities(
            @CurrentUser User user,
            @Parameter(description = "Filter by category") @RequestParam(required = false) ActivityCategory category,
            @Parameter(description = "Start date filter") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date filter") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "date") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("asc") 
            ? Sort.by(sortBy).ascending() 
            : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        PageResponse<ActivityResponse> response;
        
        if (category != null) {
            response = activityService.getActivitiesByCategory(user.getId(), category, pageable);
        } else if (startDate != null && endDate != null) {
            response = activityService.getActivitiesByDateRange(user.getId(), startDate, endDate, pageable);
        } else {
            response = activityService.getActivities(user.getId(), pageable);
        }

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get activity by ID", description = "Get a specific activity by its ID")
    public ResponseEntity<ApiResponse<ActivityResponse>> getActivityById(
            @CurrentUser User user,
            @PathVariable Long id) {
        ActivityResponse response = activityService.getActivityById(user.getId(), id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an activity", description = "Update an existing activity")
    public ResponseEntity<ApiResponse<ActivityResponse>> updateActivity(
            @CurrentUser User user,
            @PathVariable Long id,
            @Valid @RequestBody ActivityRequest request) {
        ActivityResponse response = activityService.updateActivity(user.getId(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Activity updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an activity", description = "Delete an existing activity")
    public ResponseEntity<ApiResponse<Void>> deleteActivity(
            @CurrentUser User user,
            @PathVariable Long id) {
        activityService.deleteActivity(user.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Activity deleted successfully", null));
    }
}

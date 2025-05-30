package com.earthlog.controller;

import com.earthlog.dto.request.GoalRequest;
import com.earthlog.dto.response.ApiResponse;
import com.earthlog.dto.response.GoalResponse;
import com.earthlog.entity.User;
import com.earthlog.security.CurrentUser;
import com.earthlog.service.GoalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
@Tag(name = "Goals", description = "Goal setting and progress tracking")
public class GoalController {

    private final GoalService goalService;

    @PostMapping
    @Operation(summary = "Create a new goal", description = "Create a new emission reduction goal")
    public ResponseEntity<ApiResponse<GoalResponse>> createGoal(
            @CurrentUser User user,
            @Valid @RequestBody GoalRequest request) {
        GoalResponse response = goalService.createGoal(user.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Goal created successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get all goals", description = "Get all goals for the authenticated user")
    public ResponseEntity<ApiResponse<List<GoalResponse>>> getGoals(
            @CurrentUser User user,
            @RequestParam(required = false, defaultValue = "false") boolean activeOnly) {
        List<GoalResponse> response = activeOnly 
            ? goalService.getActiveGoals(user.getId())
            : goalService.getGoals(user.getId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get goal by ID", description = "Get a specific goal with progress information")
    public ResponseEntity<ApiResponse<GoalResponse>> getGoalById(
            @CurrentUser User user,
            @PathVariable Long id) {
        GoalResponse response = goalService.getGoalById(user.getId(), id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a goal", description = "Update an existing goal")
    public ResponseEntity<ApiResponse<GoalResponse>> updateGoal(
            @CurrentUser User user,
            @PathVariable Long id,
            @Valid @RequestBody GoalRequest request) {
        GoalResponse response = goalService.updateGoal(user.getId(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Goal updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a goal", description = "Delete an existing goal")
    public ResponseEntity<ApiResponse<Void>> deleteGoal(
            @CurrentUser User user,
            @PathVariable Long id) {
        goalService.deleteGoal(user.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Goal deleted successfully", null));
    }
}

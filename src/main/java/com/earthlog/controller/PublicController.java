package com.earthlog.controller;

import com.earthlog.dto.response.ApiResponse;
import com.earthlog.entity.User;
import com.earthlog.enums.ActivityCategory;
import com.earthlog.enums.ActivityType;
import com.earthlog.repository.UserRepository;
import com.earthlog.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
@Tag(name = "Public", description = "Public endpoints that don't require authentication")
public class PublicController {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @GetMapping("/categories")
    @Operation(summary = "Get activity categories", description = "Get list of all available activity categories")
    public ResponseEntity<ApiResponse<List<Map<String, String>>>> getCategories() {
        List<Map<String, String>> categories = Arrays.stream(ActivityCategory.values())
            .map(cat -> Map.of(
                "name", cat.name(),
                "displayName", cat.getDisplayName(),
                "description", cat.getDescription()
            ))
            .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(categories));
    }

    @GetMapping("/activity-types")
    @Operation(summary = "Get activity types", description = "Get list of activity types, optionally filtered by category")
    public ResponseEntity<ApiResponse<List<Map<String, String>>>> getActivityTypes(
            @RequestParam(required = false) ActivityCategory category) {
        List<Map<String, String>> types = Arrays.stream(ActivityType.values())
            .filter(type -> category == null || type.getCategory() == category)
            .map(type -> Map.of(
                "name", type.name(),
                "displayName", type.getDisplayName(),
                "category", type.getCategory().name(),
                "defaultUnit", type.getDefaultUnit()
            ))
            .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(types));
    }

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Check if the API is running")
    public ResponseEntity<ApiResponse<Map<String, String>>> healthCheck() {
        return ResponseEntity.ok(ApiResponse.success(Map.of(
            "status", "UP",
            "service", "EarthLog API"
        )));
    }

    @GetMapping("/test-token/{userId}")
    @Operation(summary = "Generate test JWT token (DEV ONLY)", description = "Generate a JWT token for testing purposes")
    public ResponseEntity<ApiResponse<Map<String, String>>> generateTestToken(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail());
        
        return ResponseEntity.ok(ApiResponse.success(Map.of(
            "token", token,
            "userId", user.getId().toString(),
            "email", user.getEmail(),
            "expiresIn", String.valueOf(jwtTokenProvider.getExpirationMs())
        )));
    }
}

package com.earthlog.controller;

import com.earthlog.dto.request.UserProfileRequest;
import com.earthlog.dto.response.ApiResponse;
import com.earthlog.dto.response.UserResponse;
import com.earthlog.entity.User;
import com.earthlog.security.CurrentUser;
import com.earthlog.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User authentication and profile management")
public class AuthController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Get current user profile", description = "Returns the profile of the authenticated user")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(@CurrentUser User user) {
        UserResponse response = userService.getUserResponse(user);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/profile")
    @Operation(summary = "Update user profile", description = "Update the profile of the authenticated user")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @CurrentUser User user,
            @Valid @RequestBody UserProfileRequest request) {
        UserResponse response = userService.updateProfile(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", response));
    }
}

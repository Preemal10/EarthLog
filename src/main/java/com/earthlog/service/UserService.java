package com.earthlog.service;

import com.earthlog.dto.request.UserProfileRequest;
import com.earthlog.dto.response.UserResponse;
import com.earthlog.entity.User;
import com.earthlog.exception.ResourceNotFoundException;
import com.earthlog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public User findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    public UserResponse getUserResponse(User user) {
        return UserResponse.builder()
            .id(user.getId())
            .email(user.getEmail())
            .name(user.getName())
            .country(user.getCountry())
            .householdSize(user.getHouseholdSize())
            .role(user.getRole())
            .profileImageUrl(user.getProfileImageUrl())
            .createdAt(user.getCreatedAt())
            .build();
    }

    @Transactional
    public UserResponse updateProfile(Long userId, UserProfileRequest request) {
        User user = findById(userId);

        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getCountry() != null) {
            user.setCountry(request.getCountry());
        }
        if (request.getHouseholdSize() != null) {
            user.setHouseholdSize(request.getHouseholdSize());
        }

        User savedUser = userRepository.save(user);
        log.info("Updated profile for user: {}", userId);

        return getUserResponse(savedUser);
    }
}

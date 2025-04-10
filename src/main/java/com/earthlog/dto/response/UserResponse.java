package com.earthlog.dto.response;

import com.earthlog.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;
    private String email;
    private String name;
    private String country;
    private Integer householdSize;
    private Role role;
    private String profileImageUrl;
    private LocalDateTime createdAt;
}

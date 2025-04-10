package com.earthlog.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileRequest {

    @Size(max = 255, message = "Name cannot exceed 255 characters")
    private String name;

    @Size(min = 2, max = 10, message = "Country code must be between 2 and 10 characters")
    private String country;

    @Min(value = 1, message = "Household size must be at least 1")
    @Max(value = 20, message = "Household size cannot exceed 20")
    private Integer householdSize;
}

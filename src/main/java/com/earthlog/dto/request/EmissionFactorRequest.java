package com.earthlog.dto.request;

import com.earthlog.enums.ActivityCategory;
import com.earthlog.enums.ActivityType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmissionFactorRequest {

    @NotNull(message = "Category is required")
    private ActivityCategory category;

    @NotNull(message = "Activity type is required")
    private ActivityType activityType;

    @NotNull(message = "Factor is required")
    @Positive(message = "Factor must be positive")
    private BigDecimal factor;

    @NotNull(message = "Unit is required")
    @Size(max = 20, message = "Unit cannot exceed 20 characters")
    private String unit;

    @Size(max = 10, message = "Region cannot exceed 10 characters")
    private String region;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    @Size(max = 255, message = "Source cannot exceed 255 characters")
    private String source;
}

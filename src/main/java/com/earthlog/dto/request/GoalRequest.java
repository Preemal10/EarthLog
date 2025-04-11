package com.earthlog.dto.request;

import com.earthlog.enums.GoalPeriod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoalRequest {

    @NotNull(message = "Target emission is required")
    @Positive(message = "Target emission must be positive")
    private BigDecimal targetEmission;

    @NotNull(message = "Period is required")
    private GoalPeriod period;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;
}

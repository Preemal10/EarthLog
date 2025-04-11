package com.earthlog.dto.response;

import com.earthlog.enums.GoalPeriod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoalResponse {

    private Long id;
    private BigDecimal targetEmission;
    private BigDecimal currentEmission;
    private GoalPeriod period;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal progressPercentage;
    private Boolean isOnTrack;
    private Long daysRemaining;
    private Boolean isActive;
    private LocalDateTime createdAt;
}

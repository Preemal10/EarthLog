package com.earthlog.dto.response;

import com.earthlog.enums.ActivityCategory;
import com.earthlog.enums.ActivityType;
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
public class ActivityResponse {

    private Long id;
    private ActivityCategory category;
    private ActivityType activityType;
    private BigDecimal quantity;
    private String unit;
    private BigDecimal calculatedCo2;
    private LocalDate date;
    private String notes;
    private LocalDateTime createdAt;
}

package com.earthlog.dto.response;

import com.earthlog.enums.ActivityCategory;
import com.earthlog.enums.ActivityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmissionFactorResponse {

    private Long id;
    private ActivityCategory category;
    private ActivityType activityType;
    private BigDecimal factor;
    private String unit;
    private String region;
    private String description;
    private String source;
    private LocalDateTime lastUpdated;
}

package com.earthlog.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardResponse {

    private PeriodSummary today;
    private PeriodSummary thisWeek;
    private PeriodSummary thisMonth;
    private String topCategory;
    private Integer streak;
    private Integer activeGoals;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PeriodSummary {
        private BigDecimal totalCo2;
        private Long activitiesCount;
    }
}

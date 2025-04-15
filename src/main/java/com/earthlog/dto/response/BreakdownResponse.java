package com.earthlog.dto.response;

import com.earthlog.enums.ActivityCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BreakdownResponse {

    private String period;
    private BigDecimal totalCo2;
    private List<CategoryBreakdown> breakdown;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CategoryBreakdown {
        private ActivityCategory category;
        private BigDecimal co2;
        private BigDecimal percentage;
    }
}

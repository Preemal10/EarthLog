package com.earthlog.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrendResponse {

    private LocalDate startDate;
    private LocalDate endDate;
    private List<TrendPoint> trends;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TrendPoint {
        private LocalDate date;
        private BigDecimal co2;
    }
}

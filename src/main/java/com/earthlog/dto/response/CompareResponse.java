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
public class CompareResponse {

    private BigDecimal userDailyAverage;
    private BigDecimal platformDailyAverage;
    private BigDecimal differencePercentage;
    private String comparison; // "BELOW_AVERAGE", "ABOVE_AVERAGE", "AVERAGE"
}

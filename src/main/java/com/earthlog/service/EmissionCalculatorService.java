package com.earthlog.service;

import com.earthlog.enums.ActivityType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmissionCalculatorService {

    private final EmissionFactorService emissionFactorService;

    /**
     * Calculates the CO2 emission for an activity.
     *
     * @param activityType The type of activity
     * @param quantity     The quantity (e.g., kilometers, kWh, meals)
     * @param userRegion   The user's region for region-specific factors
     * @return The calculated CO2 in kg
     */
    public BigDecimal calculateEmission(ActivityType activityType, BigDecimal quantity, String userRegion) {
        BigDecimal factor = emissionFactorService.getEmissionFactor(activityType, userRegion);
        
        BigDecimal emission = quantity.multiply(factor).setScale(4, RoundingMode.HALF_UP);
        
        log.debug("Calculated emission for {}: {} * {} = {} kg CO2",
            activityType, quantity, factor, emission);
        
        return emission;
    }
}

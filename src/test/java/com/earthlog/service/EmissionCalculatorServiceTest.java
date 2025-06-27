package com.earthlog.service;

import com.earthlog.enums.ActivityType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Emission Calculator Service Tests")
class EmissionCalculatorServiceTest {

    @Mock
    private EmissionFactorService emissionFactorService;

    @InjectMocks
    private EmissionCalculatorService emissionCalculatorService;

    @Test
    @DisplayName("Should calculate CO2 emission for car travel")
    void shouldCalculateCo2ForCarTravel() {
        // Given
        ActivityType activityType = ActivityType.CAR_PETROL;
        BigDecimal quantity = new BigDecimal("100"); // 100 km
        String region = "DE";
        BigDecimal factor = new BigDecimal("0.19"); // kg CO2 per km

        when(emissionFactorService.getEmissionFactor(activityType, region)).thenReturn(factor);

        // When
        BigDecimal result = emissionCalculatorService.calculateEmission(activityType, quantity, region);

        // Then
        assertEquals(new BigDecimal("19.0000"), result);
        verify(emissionFactorService, times(1)).getEmissionFactor(activityType, region);
    }

    @Test
    @DisplayName("Should calculate CO2 emission for beef meal")
    void shouldCalculateCo2ForBeefMeal() {
        // Given
        ActivityType activityType = ActivityType.BEEF_MEAL;
        BigDecimal quantity = new BigDecimal("2"); // 2 meals
        String region = "GLOBAL";
        BigDecimal factor = new BigDecimal("6.5"); // kg CO2 per meal

        when(emissionFactorService.getEmissionFactor(activityType, region)).thenReturn(factor);

        // When
        BigDecimal result = emissionCalculatorService.calculateEmission(activityType, quantity, region);

        // Then
        assertEquals(new BigDecimal("13.0000"), result);
    }

    @Test
    @DisplayName("Should calculate zero CO2 for bike travel")
    void shouldCalculateZeroCo2ForBikeTravel() {
        // Given
        ActivityType activityType = ActivityType.BIKE;
        BigDecimal quantity = new BigDecimal("50"); // 50 km
        String region = "DE";
        BigDecimal factor = BigDecimal.ZERO;

        when(emissionFactorService.getEmissionFactor(activityType, region)).thenReturn(factor);

        // When
        BigDecimal result = emissionCalculatorService.calculateEmission(activityType, quantity, region);

        // Then
        assertEquals(new BigDecimal("0.0000"), result);
    }

    @Test
    @DisplayName("Should calculate CO2 for electricity usage")
    void shouldCalculateCo2ForElectricity() {
        // Given
        ActivityType activityType = ActivityType.ELECTRICITY;
        BigDecimal quantity = new BigDecimal("150"); // 150 kWh
        String region = "DE";
        BigDecimal factor = new BigDecimal("0.380"); // German grid factor

        when(emissionFactorService.getEmissionFactor(activityType, region)).thenReturn(factor);

        // When
        BigDecimal result = emissionCalculatorService.calculateEmission(activityType, quantity, region);

        // Then
        assertEquals(new BigDecimal("57.0000"), result);
    }

    @Test
    @DisplayName("Should handle decimal quantities correctly")
    void shouldHandleDecimalQuantities() {
        // Given
        ActivityType activityType = ActivityType.CAR_DIESEL;
        BigDecimal quantity = new BigDecimal("25.5"); // 25.5 km
        String region = "DE";
        BigDecimal factor = new BigDecimal("0.17");

        when(emissionFactorService.getEmissionFactor(activityType, region)).thenReturn(factor);

        // When
        BigDecimal result = emissionCalculatorService.calculateEmission(activityType, quantity, region);

        // Then
        assertEquals(new BigDecimal("4.3350"), result);
    }
}

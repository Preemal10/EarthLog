package com.earthlog.repository;

import com.earthlog.entity.EmissionFactor;
import com.earthlog.enums.ActivityCategory;
import com.earthlog.enums.ActivityType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Emission Factor Repository Tests")
class EmissionFactorRepositoryTest {

    @Autowired
    private EmissionFactorRepository emissionFactorRepository;

    @BeforeEach
    void setUp() {
        emissionFactorRepository.deleteAll();
        
        // Seed test data
        emissionFactorRepository.save(EmissionFactor.builder()
            .category(ActivityCategory.TRANSPORT)
            .activityType(ActivityType.CAR_PETROL)
            .factor(new BigDecimal("0.19"))
            .unit("KG_CO2_PER_KM")
            .region("DE")
            .description("German petrol car")
            .source("DEFRA")
            .build());

        emissionFactorRepository.save(EmissionFactor.builder()
            .category(ActivityCategory.TRANSPORT)
            .activityType(ActivityType.CAR_PETROL)
            .factor(new BigDecimal("0.21"))
            .unit("KG_CO2_PER_KM")
            .region("GLOBAL")
            .description("Global petrol car")
            .source("DEFRA")
            .build());

        emissionFactorRepository.save(EmissionFactor.builder()
            .category(ActivityCategory.FOOD)
            .activityType(ActivityType.BEEF_MEAL)
            .factor(new BigDecimal("6.5"))
            .unit("KG_CO2_PER_MEAL")
            .region("GLOBAL")
            .description("Beef meal")
            .source("DEFRA")
            .build());
    }

    @Test
    @DisplayName("Should find emission factor by activity type and region")
    void shouldFindByActivityTypeAndRegion() {
        // When
        Optional<EmissionFactor> result = emissionFactorRepository
            .findByActivityTypeAndRegion(ActivityType.CAR_PETROL, "DE");

        // Then
        assertTrue(result.isPresent());
        assertEquals(0, new BigDecimal("0.19").compareTo(result.get().getFactor()));
        assertEquals("DE", result.get().getRegion());
    }

    @Test
    @DisplayName("Should find emission factor with fallback to GLOBAL")
    void shouldFindWithFallbackToGlobal() {
        // When - searching for a region that doesn't exist
        List<EmissionFactor> result = emissionFactorRepository
            .findByActivityTypeAndRegionWithFallback(ActivityType.CAR_PETROL, "FR");

        // Then - should return GLOBAL factor
        assertFalse(result.isEmpty());
        assertEquals("GLOBAL", result.get(0).getRegion());
    }

    @Test
    @DisplayName("Should prioritize region-specific factor over GLOBAL")
    void shouldPrioritizeRegionSpecificFactor() {
        // When
        List<EmissionFactor> result = emissionFactorRepository
            .findByActivityTypeAndRegionWithFallback(ActivityType.CAR_PETROL, "DE");

        // Then - should return DE factor first
        assertFalse(result.isEmpty());
        assertEquals("DE", result.get(0).getRegion());
        assertEquals(0, new BigDecimal("0.19").compareTo(result.get(0).getFactor()));
    }

    @Test
    @DisplayName("Should find all factors by category")
    void shouldFindByCategory() {
        // When
        List<EmissionFactor> result = emissionFactorRepository
            .findByCategory(ActivityCategory.TRANSPORT);

        // Then
        assertEquals(2, result.size()); // DE and GLOBAL for CAR_PETROL
        assertTrue(result.stream().allMatch(f -> f.getCategory() == ActivityCategory.TRANSPORT));
    }

    @Test
    @DisplayName("Should find all factors by region")
    void shouldFindByRegion() {
        // When
        List<EmissionFactor> result = emissionFactorRepository.findByRegion("GLOBAL");

        // Then
        assertEquals(2, result.size()); // CAR_PETROL GLOBAL and BEEF_MEAL GLOBAL
    }

    @Test
    @DisplayName("Should check if emission factor exists")
    void shouldCheckIfExists() {
        // When & Then
        assertTrue(emissionFactorRepository.existsByActivityTypeAndRegion(ActivityType.CAR_PETROL, "DE"));
        assertFalse(emissionFactorRepository.existsByActivityTypeAndRegion(ActivityType.CAR_DIESEL, "DE"));
    }

    @Test
    @DisplayName("Should return empty when factor not found")
    void shouldReturnEmptyWhenNotFound() {
        // When
        Optional<EmissionFactor> result = emissionFactorRepository
            .findByActivityTypeAndRegion(ActivityType.FLIGHT_SHORT, "XX");

        // Then
        assertTrue(result.isEmpty());
    }
}

package com.earthlog.service;

import com.earthlog.dto.request.EmissionFactorRequest;
import com.earthlog.dto.response.EmissionFactorResponse;
import com.earthlog.entity.EmissionFactor;
import com.earthlog.enums.ActivityCategory;
import com.earthlog.enums.ActivityType;
import com.earthlog.exception.BadRequestException;
import com.earthlog.exception.ResourceNotFoundException;
import com.earthlog.repository.EmissionFactorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Emission Factor Service Tests")
class EmissionFactorServiceTest {

    @Mock
    private EmissionFactorRepository emissionFactorRepository;

    @InjectMocks
    private EmissionFactorService emissionFactorService;

    private EmissionFactor testFactor;

    @BeforeEach
    void setUp() {
        testFactor = EmissionFactor.builder()
            .id(1L)
            .category(ActivityCategory.TRANSPORT)
            .activityType(ActivityType.CAR_PETROL)
            .factor(new BigDecimal("0.19"))
            .unit("KG_CO2_PER_KM")
            .region("DE")
            .description("Average petrol car")
            .source("DEFRA 2023")
            .build();
    }

    @Test
    @DisplayName("Should return emission factor for activity type and region")
    void shouldReturnEmissionFactorForActivityTypeAndRegion() {
        // Given
        when(emissionFactorRepository.findByActivityTypeAndRegionWithFallback(
            ActivityType.CAR_PETROL, "DE"))
            .thenReturn(List.of(testFactor));

        // When
        BigDecimal factor = emissionFactorService.getEmissionFactor(ActivityType.CAR_PETROL, "DE");

        // Then
        assertEquals(new BigDecimal("0.19"), factor);
    }

    @Test
    @DisplayName("Should throw exception when emission factor not found")
    void shouldThrowExceptionWhenEmissionFactorNotFound() {
        // Given
        when(emissionFactorRepository.findByActivityTypeAndRegionWithFallback(
            ActivityType.CAR_PETROL, "XX"))
            .thenReturn(Collections.emptyList());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () ->
            emissionFactorService.getEmissionFactor(ActivityType.CAR_PETROL, "XX")
        );
    }

    @Test
    @DisplayName("Should return all emission factors")
    void shouldReturnAllEmissionFactors() {
        // Given
        EmissionFactor factor2 = EmissionFactor.builder()
            .id(2L)
            .category(ActivityCategory.FOOD)
            .activityType(ActivityType.BEEF_MEAL)
            .factor(new BigDecimal("6.5"))
            .unit("KG_CO2_PER_MEAL")
            .region("GLOBAL")
            .build();

        when(emissionFactorRepository.findAll()).thenReturn(Arrays.asList(testFactor, factor2));

        // When
        List<EmissionFactorResponse> result = emissionFactorService.getAllEmissionFactors();

        // Then
        assertEquals(2, result.size());
        assertEquals(ActivityType.CAR_PETROL, result.get(0).getActivityType());
        assertEquals(ActivityType.BEEF_MEAL, result.get(1).getActivityType());
    }

    @Test
    @DisplayName("Should create new emission factor")
    void shouldCreateNewEmissionFactor() {
        // Given
        EmissionFactorRequest request = EmissionFactorRequest.builder()
            .category(ActivityCategory.TRANSPORT)
            .activityType(ActivityType.CAR_PETROL)
            .factor(new BigDecimal("0.19"))
            .unit("KG_CO2_PER_KM")
            .region("DE")
            .description("Test factor")
            .source("Test")
            .build();

        when(emissionFactorRepository.existsByActivityTypeAndRegion(
            ActivityType.CAR_PETROL, "DE")).thenReturn(false);
        when(emissionFactorRepository.save(any(EmissionFactor.class))).thenReturn(testFactor);

        // When
        EmissionFactorResponse result = emissionFactorService.createEmissionFactor(request);

        // Then
        assertNotNull(result);
        assertEquals(ActivityType.CAR_PETROL, result.getActivityType());
        verify(emissionFactorRepository, times(1)).save(any(EmissionFactor.class));
    }

    @Test
    @DisplayName("Should throw exception when creating duplicate emission factor")
    void shouldThrowExceptionWhenCreatingDuplicateFactor() {
        // Given
        EmissionFactorRequest request = EmissionFactorRequest.builder()
            .category(ActivityCategory.TRANSPORT)
            .activityType(ActivityType.CAR_PETROL)
            .factor(new BigDecimal("0.19"))
            .unit("KG_CO2_PER_KM")
            .region("DE")
            .build();

        when(emissionFactorRepository.existsByActivityTypeAndRegion(
            ActivityType.CAR_PETROL, "DE")).thenReturn(true);

        // When & Then
        assertThrows(BadRequestException.class, () ->
            emissionFactorService.createEmissionFactor(request)
        );
    }

    @Test
    @DisplayName("Should update emission factor")
    void shouldUpdateEmissionFactor() {
        // Given
        EmissionFactorRequest request = EmissionFactorRequest.builder()
            .category(ActivityCategory.TRANSPORT)
            .activityType(ActivityType.CAR_PETROL)
            .factor(new BigDecimal("0.20"))
            .unit("KG_CO2_PER_KM")
            .region("DE")
            .build();

        when(emissionFactorRepository.findById(1L)).thenReturn(Optional.of(testFactor));
        when(emissionFactorRepository.save(any(EmissionFactor.class))).thenReturn(testFactor);

        // When
        EmissionFactorResponse result = emissionFactorService.updateEmissionFactor(1L, request);

        // Then
        assertNotNull(result);
        verify(emissionFactorRepository, times(1)).save(any(EmissionFactor.class));
    }

    @Test
    @DisplayName("Should delete emission factor")
    void shouldDeleteEmissionFactor() {
        // Given
        when(emissionFactorRepository.existsById(1L)).thenReturn(true);
        doNothing().when(emissionFactorRepository).deleteById(1L);

        // When
        emissionFactorService.deleteEmissionFactor(1L);

        // Then
        verify(emissionFactorRepository, times(1)).deleteById(1L);
    }
}

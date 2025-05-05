package com.earthlog.service;

import com.earthlog.dto.request.EmissionFactorRequest;
import com.earthlog.dto.response.EmissionFactorResponse;
import com.earthlog.entity.EmissionFactor;
import com.earthlog.enums.ActivityCategory;
import com.earthlog.enums.ActivityType;
import com.earthlog.exception.BadRequestException;
import com.earthlog.exception.ResourceNotFoundException;
import com.earthlog.repository.EmissionFactorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmissionFactorService {

    private final EmissionFactorRepository emissionFactorRepository;

    @Cacheable(value = "emissionFactors", key = "#activityType.name() + '_' + #region")
    public BigDecimal getEmissionFactor(ActivityType activityType, String region) {
        List<EmissionFactor> factors = emissionFactorRepository
            .findByActivityTypeAndRegionWithFallback(activityType, region);

        if (factors.isEmpty()) {
            log.warn("No emission factor found for activity type: {} and region: {}", activityType, region);
            throw new ResourceNotFoundException(
                String.format("Emission factor not found for activity type: %s", activityType)
            );
        }

        return factors.get(0).getFactor();
    }

    public List<EmissionFactorResponse> getAllEmissionFactors() {
        return emissionFactorRepository.findAll().stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    public List<EmissionFactorResponse> getEmissionFactorsByCategory(ActivityCategory category) {
        return emissionFactorRepository.findByCategory(category).stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    public EmissionFactorResponse getEmissionFactorById(Long id) {
        EmissionFactor factor = emissionFactorRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("EmissionFactor", "id", id));
        return toResponse(factor);
    }

    @Transactional
    @CacheEvict(value = "emissionFactors", allEntries = true)
    public EmissionFactorResponse createEmissionFactor(EmissionFactorRequest request) {
        String region = request.getRegion() != null ? request.getRegion() : "GLOBAL";

        if (emissionFactorRepository.existsByActivityTypeAndRegion(request.getActivityType(), region)) {
            throw new BadRequestException(
                String.format("Emission factor already exists for activity type: %s and region: %s",
                    request.getActivityType(), region)
            );
        }

        EmissionFactor factor = EmissionFactor.builder()
            .category(request.getCategory())
            .activityType(request.getActivityType())
            .factor(request.getFactor())
            .unit(request.getUnit())
            .region(region)
            .description(request.getDescription())
            .source(request.getSource())
            .build();

        EmissionFactor saved = emissionFactorRepository.save(factor);
        log.info("Created emission factor: {}", saved.getId());

        return toResponse(saved);
    }

    @Transactional
    @CacheEvict(value = "emissionFactors", allEntries = true)
    public EmissionFactorResponse updateEmissionFactor(Long id, EmissionFactorRequest request) {
        EmissionFactor factor = emissionFactorRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("EmissionFactor", "id", id));

        factor.setCategory(request.getCategory());
        factor.setActivityType(request.getActivityType());
        factor.setFactor(request.getFactor());
        factor.setUnit(request.getUnit());
        if (request.getRegion() != null) {
            factor.setRegion(request.getRegion());
        }
        factor.setDescription(request.getDescription());
        factor.setSource(request.getSource());

        EmissionFactor saved = emissionFactorRepository.save(factor);
        log.info("Updated emission factor: {}", saved.getId());

        return toResponse(saved);
    }

    @Transactional
    @CacheEvict(value = "emissionFactors", allEntries = true)
    public void deleteEmissionFactor(Long id) {
        if (!emissionFactorRepository.existsById(id)) {
            throw new ResourceNotFoundException("EmissionFactor", "id", id);
        }
        emissionFactorRepository.deleteById(id);
        log.info("Deleted emission factor: {}", id);
    }

    private EmissionFactorResponse toResponse(EmissionFactor factor) {
        return EmissionFactorResponse.builder()
            .id(factor.getId())
            .category(factor.getCategory())
            .activityType(factor.getActivityType())
            .factor(factor.getFactor())
            .unit(factor.getUnit())
            .region(factor.getRegion())
            .description(factor.getDescription())
            .source(factor.getSource())
            .lastUpdated(factor.getLastUpdated())
            .build();
    }
}

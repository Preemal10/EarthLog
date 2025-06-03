package com.earthlog.controller;

import com.earthlog.dto.request.EmissionFactorRequest;
import com.earthlog.dto.response.ApiResponse;
import com.earthlog.dto.response.EmissionFactorResponse;
import com.earthlog.enums.ActivityCategory;
import com.earthlog.service.EmissionFactorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin", description = "Admin operations for managing emission factors")
public class AdminController {

    private final EmissionFactorService emissionFactorService;

    @GetMapping("/emission-factors")
    @Operation(summary = "Get all emission factors", description = "List all emission factors in the system")
    public ResponseEntity<ApiResponse<List<EmissionFactorResponse>>> getAllEmissionFactors(
            @RequestParam(required = false) ActivityCategory category) {
        List<EmissionFactorResponse> response = category != null
            ? emissionFactorService.getEmissionFactorsByCategory(category)
            : emissionFactorService.getAllEmissionFactors();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/emission-factors/{id}")
    @Operation(summary = "Get emission factor by ID", description = "Get a specific emission factor by its ID")
    public ResponseEntity<ApiResponse<EmissionFactorResponse>> getEmissionFactorById(@PathVariable Long id) {
        EmissionFactorResponse response = emissionFactorService.getEmissionFactorById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/emission-factors")
    @Operation(summary = "Create emission factor", description = "Create a new emission factor")
    public ResponseEntity<ApiResponse<EmissionFactorResponse>> createEmissionFactor(
            @Valid @RequestBody EmissionFactorRequest request) {
        EmissionFactorResponse response = emissionFactorService.createEmissionFactor(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Emission factor created successfully", response));
    }

    @PutMapping("/emission-factors/{id}")
    @Operation(summary = "Update emission factor", description = "Update an existing emission factor")
    public ResponseEntity<ApiResponse<EmissionFactorResponse>> updateEmissionFactor(
            @PathVariable Long id,
            @Valid @RequestBody EmissionFactorRequest request) {
        EmissionFactorResponse response = emissionFactorService.updateEmissionFactor(id, request);
        return ResponseEntity.ok(ApiResponse.success("Emission factor updated successfully", response));
    }

    @DeleteMapping("/emission-factors/{id}")
    @Operation(summary = "Delete emission factor", description = "Delete an emission factor")
    public ResponseEntity<ApiResponse<Void>> deleteEmissionFactor(@PathVariable Long id) {
        emissionFactorService.deleteEmissionFactor(id);
        return ResponseEntity.ok(ApiResponse.success("Emission factor deleted successfully", null));
    }
}

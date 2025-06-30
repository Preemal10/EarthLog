package com.earthlog.controller;

import com.earthlog.config.TestConfig;
import com.earthlog.entity.EmissionFactor;
import com.earthlog.entity.User;
import com.earthlog.enums.ActivityCategory;
import com.earthlog.enums.ActivityType;
import com.earthlog.enums.Role;
import com.earthlog.repository.EmissionFactorRepository;
import com.earthlog.repository.UserRepository;
import com.earthlog.security.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestConfig.class)
@Transactional
@DisplayName("Activity Controller Integration Tests")
class ActivityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmissionFactorRepository emissionFactorRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private User testUser;
    private String authToken;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = userRepository.save(User.builder()
            .email("test@example.com")
            .name("Test User")
            .provider("GOOGLE")
            .providerId("test-provider-id")
            .country("DE")
            .householdSize(1)
            .role(Role.USER)
            .build());

        // Generate JWT token
        authToken = "Bearer " + jwtTokenProvider.generateToken(testUser.getId(), testUser.getEmail());

        // Seed emission factor for testing
        if (!emissionFactorRepository.existsByActivityTypeAndRegion(ActivityType.CAR_PETROL, "DE")) {
            emissionFactorRepository.save(EmissionFactor.builder()
                .category(ActivityCategory.TRANSPORT)
                .activityType(ActivityType.CAR_PETROL)
                .factor(new BigDecimal("0.19"))
                .unit("KG_CO2_PER_KM")
                .region("DE")
                .description("Test factor")
                .source("Test")
                .build());
        }

        if (!emissionFactorRepository.existsByActivityTypeAndRegion(ActivityType.CAR_PETROL, "GLOBAL")) {
            emissionFactorRepository.save(EmissionFactor.builder()
                .category(ActivityCategory.TRANSPORT)
                .activityType(ActivityType.CAR_PETROL)
                .factor(new BigDecimal("0.21"))
                .unit("KG_CO2_PER_KM")
                .region("GLOBAL")
                .description("Global test factor")
                .source("Test")
                .build());
        }
    }

    @Test
    @DisplayName("POST /api/activities - Should create activity successfully")
    void shouldCreateActivitySuccessfully() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("category", "TRANSPORT");
        request.put("activityType", "CAR_PETROL");
        request.put("quantity", 50);
        request.put("date", LocalDate.now().toString());
        request.put("notes", "Test commute");

        mockMvc.perform(post("/api/activities")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Activity logged successfully"))
            .andExpect(jsonPath("$.data.category").value("TRANSPORT"))
            .andExpect(jsonPath("$.data.activityType").value("CAR_PETROL"))
            .andExpect(jsonPath("$.data.quantity").value(50))
            .andExpect(jsonPath("$.data.calculatedCo2").isNumber());
    }

    @Test
    @DisplayName("POST /api/activities - Should return 401 without auth token")
    void shouldReturn401WithoutAuthToken() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("category", "TRANSPORT");
        request.put("activityType", "CAR_PETROL");
        request.put("quantity", 50);
        request.put("date", LocalDate.now().toString());

        mockMvc.perform(post("/api/activities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().is3xxRedirection()); // OAuth2 redirects unauthenticated requests
    }

    @Test
    @DisplayName("POST /api/activities - Should return 400 for invalid request")
    void shouldReturn400ForInvalidRequest() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("category", "TRANSPORT");
        // Missing required fields: activityType, quantity, date

        mockMvc.perform(post("/api/activities")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/activities - Should return empty list for new user")
    void shouldReturnEmptyListForNewUser() throws Exception {
        mockMvc.perform(get("/api/activities")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.content").isArray())
            .andExpect(jsonPath("$.data.totalElements").value(0));
    }

    @Test
    @DisplayName("GET /api/activities - Should return paginated activities")
    void shouldReturnPaginatedActivities() throws Exception {
        // First create an activity
        Map<String, Object> request = new HashMap<>();
        request.put("category", "TRANSPORT");
        request.put("activityType", "CAR_PETROL");
        request.put("quantity", 50);
        request.put("date", LocalDate.now().toString());

        mockMvc.perform(post("/api/activities")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());

        // Then get activities
        mockMvc.perform(get("/api/activities")
                .header("Authorization", authToken)
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.content").isArray())
            .andExpect(jsonPath("$.data.totalElements").value(1))
            .andExpect(jsonPath("$.data.content[0].category").value("TRANSPORT"));
    }

    @Test
    @DisplayName("GET /api/activities/{id} - Should return specific activity")
    void shouldReturnSpecificActivity() throws Exception {
        // First create an activity
        Map<String, Object> request = new HashMap<>();
        request.put("category", "TRANSPORT");
        request.put("activityType", "CAR_PETROL");
        request.put("quantity", 50);
        request.put("date", LocalDate.now().toString());

        String response = mockMvc.perform(post("/api/activities")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();

        // Extract activity ID from response
        Long activityId = objectMapper.readTree(response).get("data").get("id").asLong();

        // Then get specific activity
        mockMvc.perform(get("/api/activities/" + activityId)
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(activityId));
    }

    @Test
    @DisplayName("PUT /api/activities/{id} - Should update activity")
    void shouldUpdateActivity() throws Exception {
        // First create an activity
        Map<String, Object> createRequest = new HashMap<>();
        createRequest.put("category", "TRANSPORT");
        createRequest.put("activityType", "CAR_PETROL");
        createRequest.put("quantity", 50);
        createRequest.put("date", LocalDate.now().toString());

        String response = mockMvc.perform(post("/api/activities")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();

        Long activityId = objectMapper.readTree(response).get("data").get("id").asLong();

        // Update the activity
        Map<String, Object> updateRequest = new HashMap<>();
        updateRequest.put("category", "TRANSPORT");
        updateRequest.put("activityType", "CAR_PETROL");
        updateRequest.put("quantity", 100); // Changed quantity
        updateRequest.put("date", LocalDate.now().toString());
        updateRequest.put("notes", "Updated commute");

        mockMvc.perform(put("/api/activities/" + activityId)
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.quantity").value(100))
            .andExpect(jsonPath("$.data.notes").value("Updated commute"));
    }

    @Test
    @DisplayName("DELETE /api/activities/{id} - Should delete activity")
    void shouldDeleteActivity() throws Exception {
        // First create an activity
        Map<String, Object> request = new HashMap<>();
        request.put("category", "TRANSPORT");
        request.put("activityType", "CAR_PETROL");
        request.put("quantity", 50);
        request.put("date", LocalDate.now().toString());

        String response = mockMvc.perform(post("/api/activities")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();

        Long activityId = objectMapper.readTree(response).get("data").get("id").asLong();

        // Delete the activity
        mockMvc.perform(delete("/api/activities/" + activityId)
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Activity deleted successfully"));

        // Verify it's deleted
        mockMvc.perform(get("/api/activities/" + activityId)
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }
}

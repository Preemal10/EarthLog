package com.earthlog.controller;

import com.earthlog.config.TestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestConfig.class)
@DisplayName("Public Controller Integration Tests")
class PublicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/public/health - Should return health status")
    void shouldReturnHealthStatus() throws Exception {
        mockMvc.perform(get("/api/public/health")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.status").value("UP"))
            .andExpect(jsonPath("$.data.service").value("EarthLog API"));
    }

    @Test
    @DisplayName("GET /api/public/categories - Should return all activity categories")
    void shouldReturnAllActivityCategories() throws Exception {
        mockMvc.perform(get("/api/public/categories")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data", hasSize(5))) // TRANSPORT, FOOD, ENERGY, SHOPPING, WASTE
            .andExpect(jsonPath("$.data[?(@.name=='TRANSPORT')]").exists())
            .andExpect(jsonPath("$.data[?(@.name=='FOOD')]").exists())
            .andExpect(jsonPath("$.data[?(@.name=='ENERGY')]").exists())
            .andExpect(jsonPath("$.data[?(@.name=='SHOPPING')]").exists())
            .andExpect(jsonPath("$.data[?(@.name=='WASTE')]").exists());
    }

    @Test
    @DisplayName("GET /api/public/activity-types - Should return all activity types")
    void shouldReturnAllActivityTypes() throws Exception {
        mockMvc.perform(get("/api/public/activity-types")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data[?(@.name=='CAR_PETROL')]").exists())
            .andExpect(jsonPath("$.data[?(@.name=='BEEF_MEAL')]").exists())
            .andExpect(jsonPath("$.data[?(@.name=='ELECTRICITY')]").exists());
    }

    @Test
    @DisplayName("GET /api/public/activity-types?category=TRANSPORT - Should return transport activity types only")
    void shouldReturnTransportActivityTypesOnly() throws Exception {
        mockMvc.perform(get("/api/public/activity-types")
                .param("category", "TRANSPORT")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data[*].category", everyItem(equalTo("TRANSPORT"))))
            .andExpect(jsonPath("$.data[?(@.name=='CAR_PETROL')]").exists())
            .andExpect(jsonPath("$.data[?(@.name=='BIKE')]").exists())
            .andExpect(jsonPath("$.data[?(@.name=='BEEF_MEAL')]").doesNotExist());
    }

    @Test
    @DisplayName("GET /api/public/activity-types?category=FOOD - Should return food activity types only")
    void shouldReturnFoodActivityTypesOnly() throws Exception {
        mockMvc.perform(get("/api/public/activity-types")
                .param("category", "FOOD")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data[*].category", everyItem(equalTo("FOOD"))))
            .andExpect(jsonPath("$.data[?(@.name=='BEEF_MEAL')]").exists())
            .andExpect(jsonPath("$.data[?(@.name=='VEGAN_MEAL')]").exists());
    }
}

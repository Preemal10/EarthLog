package com.earthlog.config;

import com.earthlog.entity.EmissionFactor;
import com.earthlog.enums.ActivityCategory;
import com.earthlog.enums.ActivityType;
import com.earthlog.repository.EmissionFactorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final EmissionFactorRepository emissionFactorRepository;

    @Override
    public void run(String... args) {
        if (emissionFactorRepository.count() == 0) {
            log.info("Seeding emission factors...");
            seedEmissionFactors();
            log.info("Emission factors seeded successfully!");
        } else {
            log.info("Emission factors already exist, skipping seed.");
        }
    }

    private void seedEmissionFactors() {
        List<EmissionFactor> factors = Arrays.asList(
            // TRANSPORT - Germany specific
            createFactor(ActivityCategory.TRANSPORT, ActivityType.CAR_PETROL, "0.19", "KG_CO2_PER_KM", "DE", "Average petrol car in Germany", "DEFRA 2023"),
            createFactor(ActivityCategory.TRANSPORT, ActivityType.CAR_DIESEL, "0.17", "KG_CO2_PER_KM", "DE", "Average diesel car in Germany", "DEFRA 2023"),
            createFactor(ActivityCategory.TRANSPORT, ActivityType.CAR_ELECTRIC, "0.05", "KG_CO2_PER_KM", "DE", "Electric car with German grid", "Calculated"),
            createFactor(ActivityCategory.TRANSPORT, ActivityType.BUS, "0.089", "KG_CO2_PER_KM", "GLOBAL", "Average bus transport", "DEFRA 2023"),
            createFactor(ActivityCategory.TRANSPORT, ActivityType.TRAIN_REGIONAL, "0.035", "KG_CO2_PER_KM", "DE", "Regional train in Germany", "UBA Germany"),
            createFactor(ActivityCategory.TRANSPORT, ActivityType.TRAIN_ICE, "0.029", "KG_CO2_PER_KM", "DE", "ICE long-distance train", "Deutsche Bahn"),
            createFactor(ActivityCategory.TRANSPORT, ActivityType.FLIGHT_SHORT, "0.255", "KG_CO2_PER_KM", "GLOBAL", "Short-haul flight (<3500km)", "DEFRA 2023"),
            createFactor(ActivityCategory.TRANSPORT, ActivityType.FLIGHT_LONG, "0.195", "KG_CO2_PER_KM", "GLOBAL", "Long-haul flight (>3500km)", "DEFRA 2023"),
            createFactor(ActivityCategory.TRANSPORT, ActivityType.BIKE, "0.0", "KG_CO2_PER_KM", "GLOBAL", "Bicycle - zero emissions", "N/A"),
            createFactor(ActivityCategory.TRANSPORT, ActivityType.WALK, "0.0", "KG_CO2_PER_KM", "GLOBAL", "Walking - zero emissions", "N/A"),

            // FOOD - Global
            createFactor(ActivityCategory.FOOD, ActivityType.BEEF_MEAL, "6.5", "KG_CO2_PER_MEAL", "GLOBAL", "Beef-based meal", "DEFRA 2023"),
            createFactor(ActivityCategory.FOOD, ActivityType.LAMB_MEAL, "5.5", "KG_CO2_PER_MEAL", "GLOBAL", "Lamb-based meal", "DEFRA 2023"),
            createFactor(ActivityCategory.FOOD, ActivityType.PORK_MEAL, "1.8", "KG_CO2_PER_MEAL", "GLOBAL", "Pork-based meal", "DEFRA 2023"),
            createFactor(ActivityCategory.FOOD, ActivityType.CHICKEN_MEAL, "1.5", "KG_CO2_PER_MEAL", "GLOBAL", "Chicken-based meal", "DEFRA 2023"),
            createFactor(ActivityCategory.FOOD, ActivityType.FISH_MEAL, "1.2", "KG_CO2_PER_MEAL", "GLOBAL", "Fish-based meal", "DEFRA 2023"),
            createFactor(ActivityCategory.FOOD, ActivityType.VEGETARIAN_MEAL, "0.5", "KG_CO2_PER_MEAL", "GLOBAL", "Vegetarian meal", "DEFRA 2023"),
            createFactor(ActivityCategory.FOOD, ActivityType.VEGAN_MEAL, "0.3", "KG_CO2_PER_MEAL", "GLOBAL", "Vegan meal", "DEFRA 2023"),

            // ENERGY - Germany specific
            createFactor(ActivityCategory.ENERGY, ActivityType.ELECTRICITY, "0.380", "KG_CO2_PER_KWH", "DE", "German grid electricity", "Carbon Footprint 2025"),
            createFactor(ActivityCategory.ENERGY, ActivityType.NATURAL_GAS, "2.02", "KG_CO2_PER_M3", "DE", "Natural gas for heating", "DEFRA 2023"),
            createFactor(ActivityCategory.ENERGY, ActivityType.HEATING_OIL, "2.96", "KG_CO2_PER_LITER", "GLOBAL", "Heating oil", "DEFRA 2023"),

            // SHOPPING - Global estimates
            createFactor(ActivityCategory.SHOPPING, ActivityType.CLOTHING_NEW, "15.0", "KG_CO2_PER_ITEM", "GLOBAL", "New clothing item", "Estimate"),
            createFactor(ActivityCategory.SHOPPING, ActivityType.CLOTHING_SECONDHAND, "2.0", "KG_CO2_PER_ITEM", "GLOBAL", "Secondhand clothing", "Estimate"),
            createFactor(ActivityCategory.SHOPPING, ActivityType.ELECTRONICS, "50.0", "KG_CO2_PER_ITEM", "GLOBAL", "Electronic device", "Estimate"),
            createFactor(ActivityCategory.SHOPPING, ActivityType.FURNITURE, "100.0", "KG_CO2_PER_ITEM", "GLOBAL", "Furniture item", "Estimate"),

            // WASTE
            createFactor(ActivityCategory.WASTE, ActivityType.LANDFILL, "0.58", "KG_CO2_PER_KG", "GLOBAL", "Waste to landfill", "DEFRA 2023"),
            createFactor(ActivityCategory.WASTE, ActivityType.RECYCLED, "0.02", "KG_CO2_PER_KG", "GLOBAL", "Recycled waste", "DEFRA 2023"),
            createFactor(ActivityCategory.WASTE, ActivityType.COMPOSTED, "0.01", "KG_CO2_PER_KG", "GLOBAL", "Composted waste", "DEFRA 2023")
        );

        emissionFactorRepository.saveAll(factors);
    }

    private EmissionFactor createFactor(ActivityCategory category, ActivityType activityType, 
            String factor, String unit, String region, String description, String source) {
        return EmissionFactor.builder()
            .category(category)
            .activityType(activityType)
            .factor(new BigDecimal(factor))
            .unit(unit)
            .region(region)
            .description(description)
            .source(source)
            .build();
    }
}

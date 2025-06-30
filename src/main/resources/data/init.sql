-- EarthLog Database Initialization Script
-- This script runs when the PostgreSQL container starts for the first time

-- Note: Tables are created by Hibernate (ddl-auto: update)
-- This script only seeds the emission_factors table

-- Wait for tables to be created by the application before inserting data
-- The data will be inserted on first application start

-- Create a function to seed emission factors if table exists
CREATE OR REPLACE FUNCTION seed_emission_factors()
RETURNS void AS $$
BEGIN
    -- Check if emission_factors table exists and is empty
    IF EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'emission_factors') THEN
        IF NOT EXISTS (SELECT 1 FROM emission_factors LIMIT 1) THEN
            -- TRANSPORT - Germany specific
            INSERT INTO emission_factors (category, activity_type, factor, unit, region, description, source, last_updated) VALUES
            ('TRANSPORT', 'CAR_PETROL', 0.19, 'KG_CO2_PER_KM', 'DE', 'Average petrol car in Germany', 'DEFRA 2023', NOW()),
            ('TRANSPORT', 'CAR_DIESEL', 0.17, 'KG_CO2_PER_KM', 'DE', 'Average diesel car in Germany', 'DEFRA 2023', NOW()),
            ('TRANSPORT', 'CAR_ELECTRIC', 0.05, 'KG_CO2_PER_KM', 'DE', 'Electric car with German grid electricity', 'Calculated', NOW()),
            ('TRANSPORT', 'BUS', 0.089, 'KG_CO2_PER_KM', 'GLOBAL', 'Average bus transport', 'DEFRA 2023', NOW()),
            ('TRANSPORT', 'TRAIN_REGIONAL', 0.035, 'KG_CO2_PER_KM', 'DE', 'Regional train in Germany', 'UBA Germany', NOW()),
            ('TRANSPORT', 'TRAIN_ICE', 0.029, 'KG_CO2_PER_KM', 'DE', 'ICE long-distance train', 'Deutsche Bahn', NOW()),
            ('TRANSPORT', 'FLIGHT_SHORT', 0.255, 'KG_CO2_PER_KM', 'GLOBAL', 'Short-haul flight (<3500km)', 'DEFRA 2023', NOW()),
            ('TRANSPORT', 'FLIGHT_LONG', 0.195, 'KG_CO2_PER_KM', 'GLOBAL', 'Long-haul flight (>3500km)', 'DEFRA 2023', NOW()),
            ('TRANSPORT', 'BIKE', 0.0, 'KG_CO2_PER_KM', 'GLOBAL', 'Bicycle - zero emissions', 'N/A', NOW()),
            ('TRANSPORT', 'WALK', 0.0, 'KG_CO2_PER_KM', 'GLOBAL', 'Walking - zero emissions', 'N/A', NOW()),

            -- FOOD - Global
            ('FOOD', 'BEEF_MEAL', 6.5, 'KG_CO2_PER_MEAL', 'GLOBAL', 'Beef-based meal', 'DEFRA 2023', NOW()),
            ('FOOD', 'LAMB_MEAL', 5.5, 'KG_CO2_PER_MEAL', 'GLOBAL', 'Lamb-based meal', 'DEFRA 2023', NOW()),
            ('FOOD', 'PORK_MEAL', 1.8, 'KG_CO2_PER_MEAL', 'GLOBAL', 'Pork-based meal', 'DEFRA 2023', NOW()),
            ('FOOD', 'CHICKEN_MEAL', 1.5, 'KG_CO2_PER_MEAL', 'GLOBAL', 'Chicken-based meal', 'DEFRA 2023', NOW()),
            ('FOOD', 'FISH_MEAL', 1.2, 'KG_CO2_PER_MEAL', 'GLOBAL', 'Fish-based meal', 'DEFRA 2023', NOW()),
            ('FOOD', 'VEGETARIAN_MEAL', 0.5, 'KG_CO2_PER_MEAL', 'GLOBAL', 'Vegetarian meal', 'DEFRA 2023', NOW()),
            ('FOOD', 'VEGAN_MEAL', 0.3, 'KG_CO2_PER_MEAL', 'GLOBAL', 'Vegan meal', 'DEFRA 2023', NOW()),

            -- ENERGY - Germany specific
            ('ENERGY', 'ELECTRICITY', 0.380, 'KG_CO2_PER_KWH', 'DE', 'German grid electricity', 'Carbon Footprint 2025', NOW()),
            ('ENERGY', 'NATURAL_GAS', 2.02, 'KG_CO2_PER_M3', 'DE', 'Natural gas for heating', 'DEFRA 2023', NOW()),
            ('ENERGY', 'HEATING_OIL', 2.96, 'KG_CO2_PER_LITER', 'GLOBAL', 'Heating oil', 'DEFRA 2023', NOW()),

            -- SHOPPING - Global estimates
            ('SHOPPING', 'CLOTHING_NEW', 15.0, 'KG_CO2_PER_ITEM', 'GLOBAL', 'New clothing item (average)', 'Estimate', NOW()),
            ('SHOPPING', 'CLOTHING_SECONDHAND', 2.0, 'KG_CO2_PER_ITEM', 'GLOBAL', 'Secondhand clothing item', 'Estimate', NOW()),
            ('SHOPPING', 'ELECTRONICS', 50.0, 'KG_CO2_PER_ITEM', 'GLOBAL', 'Electronic device (average)', 'Estimate', NOW()),
            ('SHOPPING', 'FURNITURE', 100.0, 'KG_CO2_PER_ITEM', 'GLOBAL', 'Furniture item (average)', 'Estimate', NOW()),

            -- WASTE
            ('WASTE', 'LANDFILL', 0.58, 'KG_CO2_PER_KG', 'GLOBAL', 'Waste sent to landfill', 'DEFRA 2023', NOW()),
            ('WASTE', 'RECYCLED', 0.02, 'KG_CO2_PER_KG', 'GLOBAL', 'Recycled waste', 'DEFRA 2023', NOW()),
            ('WASTE', 'COMPOSTED', 0.01, 'KG_CO2_PER_KG', 'GLOBAL', 'Composted organic waste', 'DEFRA 2023', NOW());
        END IF;
    END IF;
END;
$$ LANGUAGE plpgsql;

-- Note: The function above will be called by the application's DataLoader

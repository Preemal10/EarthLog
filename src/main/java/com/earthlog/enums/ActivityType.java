package com.earthlog.enums;

public enum ActivityType {
    // Transport
    CAR_PETROL(ActivityCategory.TRANSPORT, "Petrol Car", "KM"),
    CAR_DIESEL(ActivityCategory.TRANSPORT, "Diesel Car", "KM"),
    CAR_ELECTRIC(ActivityCategory.TRANSPORT, "Electric Car", "KM"),
    BUS(ActivityCategory.TRANSPORT, "Bus", "KM"),
    TRAIN_REGIONAL(ActivityCategory.TRANSPORT, "Regional Train", "KM"),
    TRAIN_ICE(ActivityCategory.TRANSPORT, "ICE/Long Distance Train", "KM"),
    FLIGHT_SHORT(ActivityCategory.TRANSPORT, "Short-haul Flight (<3500km)", "KM"),
    FLIGHT_LONG(ActivityCategory.TRANSPORT, "Long-haul Flight (>3500km)", "KM"),
    BIKE(ActivityCategory.TRANSPORT, "Bicycle", "KM"),
    WALK(ActivityCategory.TRANSPORT, "Walking", "KM"),

    // Food
    BEEF_MEAL(ActivityCategory.FOOD, "Beef Meal", "MEAL"),
    LAMB_MEAL(ActivityCategory.FOOD, "Lamb Meal", "MEAL"),
    PORK_MEAL(ActivityCategory.FOOD, "Pork Meal", "MEAL"),
    CHICKEN_MEAL(ActivityCategory.FOOD, "Chicken Meal", "MEAL"),
    FISH_MEAL(ActivityCategory.FOOD, "Fish Meal", "MEAL"),
    VEGETARIAN_MEAL(ActivityCategory.FOOD, "Vegetarian Meal", "MEAL"),
    VEGAN_MEAL(ActivityCategory.FOOD, "Vegan Meal", "MEAL"),

    // Energy
    ELECTRICITY(ActivityCategory.ENERGY, "Electricity", "KWH"),
    NATURAL_GAS(ActivityCategory.ENERGY, "Natural Gas", "M3"),
    HEATING_OIL(ActivityCategory.ENERGY, "Heating Oil", "LITER"),

    // Shopping
    CLOTHING_NEW(ActivityCategory.SHOPPING, "New Clothing", "ITEM"),
    CLOTHING_SECONDHAND(ActivityCategory.SHOPPING, "Secondhand Clothing", "ITEM"),
    ELECTRONICS(ActivityCategory.SHOPPING, "Electronics", "ITEM"),
    FURNITURE(ActivityCategory.SHOPPING, "Furniture", "ITEM"),

    // Waste
    LANDFILL(ActivityCategory.WASTE, "Landfill Waste", "KG"),
    RECYCLED(ActivityCategory.WASTE, "Recycled Waste", "KG"),
    COMPOSTED(ActivityCategory.WASTE, "Composted Waste", "KG");

    private final ActivityCategory category;
    private final String displayName;
    private final String defaultUnit;

    ActivityType(ActivityCategory category, String displayName, String defaultUnit) {
        this.category = category;
        this.displayName = displayName;
        this.defaultUnit = defaultUnit;
    }

    public ActivityCategory getCategory() {
        return category;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDefaultUnit() {
        return defaultUnit;
    }
}

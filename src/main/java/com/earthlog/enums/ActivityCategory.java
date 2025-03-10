package com.earthlog.enums;

public enum ActivityCategory {
    TRANSPORT("Transport", "Activities related to transportation"),
    FOOD("Food", "Food consumption activities"),
    ENERGY("Energy", "Home energy usage"),
    SHOPPING("Shopping", "Consumer purchases"),
    WASTE("Waste", "Waste disposal activities");

    private final String displayName;
    private final String description;

    ActivityCategory(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}

package com.earthlog.enums;

public enum Role {
    USER("Regular User"),
    PREMIUM("Premium User"),
    ADMIN("Administrator");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

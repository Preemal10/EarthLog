package com.earthlog.enums;

public enum GoalPeriod {
    WEEKLY("Weekly", 7),
    MONTHLY("Monthly", 30),
    YEARLY("Yearly", 365);

    private final String displayName;
    private final int days;

    GoalPeriod(String displayName, int days) {
        this.displayName = displayName;
        this.days = days;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getDays() {
        return days;
    }
}

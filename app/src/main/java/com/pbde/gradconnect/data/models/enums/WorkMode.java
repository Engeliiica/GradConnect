package com.pbde.gradconnect.data.models.enums;

import com.google.firebase.firestore.PropertyName;

public enum WorkMode {
    @PropertyName("Remote")
    REMOTE("Remote"),
    
    @PropertyName("On-site")
    ON_SITE("On-site"),
    
    @PropertyName("Hybrid")
    HYBRID("Hybrid");

    private final String value;

    WorkMode(String value) {
        this.value = value;
    }

    @PropertyName("workMode")
    public String getValue() {
        return value;
    }

    public static WorkMode fromString(String text) {
        for (WorkMode mode : WorkMode.values()) {
            if (mode.value.equalsIgnoreCase(text)) {
                return mode;
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }
}

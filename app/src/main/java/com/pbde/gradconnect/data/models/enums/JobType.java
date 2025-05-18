package com.pbde.gradconnect.data.models.enums;

import com.google.firebase.firestore.PropertyName;

public enum JobType {
    @PropertyName("Full-time")
    FULL_TIME("Full-time"),
    
    @PropertyName("Part-time")
    PART_TIME("Part-time"),
    
    @PropertyName("Contract")
    CONTRACT("Contract");

    private final String value;

    JobType(String value) {
        this.value = value;
    }

    @PropertyName("jobType")
    public String getValue() {
        return value;
    }

    public static JobType fromString(String text) {
        for (JobType type : JobType.values()) {
            if (type.value.equalsIgnoreCase(text)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }
}

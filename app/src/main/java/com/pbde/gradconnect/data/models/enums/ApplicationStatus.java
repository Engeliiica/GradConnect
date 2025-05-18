package com.pbde.gradconnect.data.models.enums;

public enum ApplicationStatus {
    IN_REVIEW("In Review"),
    INTERVIEW("Interview"),
    REJECTED("Rejected"),
    ACCEPTED("Accepted"),
    WITHDRAWN("Withdrawn"),
    SUBMITTED("Submitted");

    private final String value;

    ApplicationStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ApplicationStatus fromString(String text) {
        for (ApplicationStatus status : ApplicationStatus.values()) {
            if (status.value.equalsIgnoreCase(text)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }
}

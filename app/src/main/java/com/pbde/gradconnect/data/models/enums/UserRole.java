package com.pbde.gradconnect.data.models.enums;

public enum UserRole {
    GUEST("guest"),
    CANDIDATE("candidate"),
    EMPLOYER("employer");

    private final String roleString;

    UserRole(String roleString) {
        this.roleString = roleString;
    }

    public String getRoleString() {
        return roleString;
    }

    public static UserRole fromString(String roleString) {
        for (UserRole role : values()) {
            if (role.roleString.equalsIgnoreCase(roleString)) {
                return role;
            }
        }
        throw new IllegalArgumentException("No enum constant for role: " + roleString);
    }
}

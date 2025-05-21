package com.pbde.gradconnect.data.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.pbde.gradconnect.data.models.enums.UserRole;
import java.util.Date;

public class Graduate extends User {
    @Nullable
    private GraduateProfile profile;

    public Graduate(@NonNull String id, @NonNull Date createdAt, @NonNull Date updatedAt,
                     @NonNull String uid, @NonNull String fullName, @NonNull String email,
                     @Nullable GraduateProfile profile) {
        super(id, createdAt, updatedAt, uid, fullName, email, UserRole.CANDIDATE, profile, null);
        this.profile = profile;
    }

    @Nullable
    public GraduateProfile getProfile() {
        return profile;
    }

    public void setProfile(@Nullable GraduateProfile profile) {
        this.profile = profile;
    }
}

package com.pbde.gradconnect.data.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.pbde.gradconnect.data.models.enums.UserRole;
import java.util.Date;
import java.util.Map;

public class Candidate extends User {
    @Nullable
    private CandidateProfile profile;

    public Candidate(@NonNull String id, @NonNull Date createdAt, @NonNull Date updatedAt,
                    @NonNull String uid, @NonNull String fullName, @NonNull String email,
                    @Nullable CandidateProfile profile) {
        super(id, createdAt, updatedAt, uid, fullName, email, UserRole.CANDIDATE, profile, null);
        this.profile = profile;
    }

    @Nullable
    public CandidateProfile getProfile() {
        return profile;
    }

    public void setProfile(@Nullable CandidateProfile profile) {
        this.profile = profile;
    }
}

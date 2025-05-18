package com.pbde.gradconnect.data.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class EmployerProfile {
    @NonNull
    private String companyName;
    @Nullable
    private String companyDescription;
    @Nullable
    private String website;

    public EmployerProfile(@NonNull String companyName) {
        this.companyName = companyName;
    }

    public EmployerProfile(@NonNull String companyName, @Nullable String companyDescription, @Nullable String website) {
        this.companyName = companyName;
        this.companyDescription = companyDescription;
        this.website = website;
    }

    @NonNull
    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(@NonNull String companyName) {
        this.companyName = companyName;
    }

    @Nullable
    public String getCompanyDescription() {
        return companyDescription;
    }

    public void setCompanyDescription(@Nullable String companyDescription) {
        this.companyDescription = companyDescription;
    }

    @Nullable
    public String getWebsite() {
        return website;
    }

    public void setWebsite(@Nullable String website) {
        this.website = website;
    }
}

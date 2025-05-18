package com.pbde.gradconnect.data.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.Date;

public class ApplicationInterview {
    @NonNull
    private String title;
    @NonNull
    private Date date;
    @Nullable
    private String platform;

    public ApplicationInterview(@NonNull String title, @NonNull Date date) {
        this.title = title;
        this.date = date;
    }

    public ApplicationInterview(@NonNull String title, @NonNull Date date, @Nullable String platform) {
        this.title = title;
        this.date = date;
        this.platform = platform;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    @NonNull
    public Date getDate() {
        return date;
    }

    public void setDate(@NonNull Date date) {
        this.date = date;
    }

    @Nullable
    public String getPlatform() {
        return platform;
    }

    public void setPlatform(@Nullable String platform) {
        this.platform = platform;
    }
}

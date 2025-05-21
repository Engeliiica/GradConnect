package com.pbde.gradconnect.data.models;

import androidx.annotation.NonNull;
import java.util.Date;

public class Experience {
    @NonNull
    private String title;
    @NonNull
    private String company;
    @NonNull
    private Date startDate;
    private Date endDate;
    private String description;

    public Experience(@NonNull String title, @NonNull String company,
                      @NonNull Date startDate, Date endDate, String description) {
        this.title = title;
        this.company = company;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    @NonNull
    public String getCompany() {
        return company;
    }

    public void setCompany(@NonNull String company) {
        this.company = company;
    }

    @NonNull
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(@NonNull Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

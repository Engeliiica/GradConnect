package com.pbde.gradconnect.data.models;

import androidx.annotation.NonNull;
import java.util.Date;

public class Education {
    @NonNull
    private String degree;
    @NonNull
    private String institution;
    @NonNull
    private Date startDate;
    private Date endDate;
    private String description;

    public Education(@NonNull String degree, @NonNull String institution,
                     @NonNull Date startDate, Date endDate, String description) {
        this.degree = degree;
        this.institution = institution;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
    }

    @NonNull
    public String getDegree() {
        return degree;
    }

    public void setDegree(@NonNull String degree) {
        this.degree = degree;
    }

    @NonNull
    public String getInstitution() {
        return institution;
    }

    public void setInstitution(@NonNull String institution) {
        this.institution = institution;
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

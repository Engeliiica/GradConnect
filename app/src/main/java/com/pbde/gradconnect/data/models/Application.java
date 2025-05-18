package com.pbde.gradconnect.data.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.pbde.gradconnect.data.models.enums.ApplicationStatus;
import java.util.Date;

public class Application extends BaseModel {
    @NonNull
    private String jobId;
    @NonNull
    private String jobTitle;
    @NonNull
    private String candidateId;
    @NonNull
    private String employerId;
    @NonNull
    private ApplicationStatus status;
    @NonNull
    private Date appliedAt;
    @Nullable
    private String resumeUrl;
    @Nullable
    private String coverLetter;
    @Nullable
    private String portfolioUrl;
    @Nullable
    private String linkedinUrl;
    @Nullable
    private Integer progress;
    @Nullable
    private ApplicationInterview applicationInterview;

    public Application(@NonNull String id, @NonNull Date createdAt, @NonNull Date updatedAt,
                      @NonNull String jobId, @NonNull String candidateId,
                      @NonNull String employerId, @NonNull ApplicationStatus status, @NonNull Date appliedAt,
                      @Nullable String resumeUrl, @Nullable String coverLetter,
                      @Nullable String portfolioUrl, @Nullable String linkedinUrl,
                      @Nullable Integer progress, @Nullable ApplicationInterview applicationInterview) {
        super(id, createdAt, updatedAt);
        this.jobId = jobId;
        this.candidateId = candidateId;
        this.employerId = employerId;
        this.status = status;
        this.appliedAt = appliedAt;
        this.resumeUrl = resumeUrl;
        this.coverLetter = coverLetter;
        this.portfolioUrl = portfolioUrl;
        this.linkedinUrl = linkedinUrl;
        this.progress = progress;
        this.applicationInterview = applicationInterview;
    }

    // Getters and setters
    @NonNull
    public String getJobId() {
        return jobId;
    }

    public void setJobId(@NonNull String jobId) {
        this.jobId = jobId;
    }

    @NonNull
    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(@NonNull String jobTitle) {
        this.jobTitle = jobTitle;
    }

    @NonNull
    public String getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(@NonNull String candidateId) {
        this.candidateId = candidateId;
    }

    @NonNull
    public String getEmployerId() {
        return employerId;
    }

    public void setEmployerId(@NonNull String employerId) {
        this.employerId = employerId;
    }

    @NonNull
    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(@NonNull ApplicationStatus status) {
        this.status = status;
    }

    @NonNull
    public Date getAppliedAt() {
        return appliedAt;
    }

    public void setAppliedAt(@NonNull Date appliedAt) {
        this.appliedAt = appliedAt;
    }

    @Nullable
    public String getResumeUrl() {
        return resumeUrl;
    }

    public void setResumeUrl(@Nullable String resumeUrl) {
        this.resumeUrl = resumeUrl;
    }

    @Nullable
    public String getCoverLetter() {
        return coverLetter;
    }

    public void setCoverLetter(@Nullable String coverLetter) {
        this.coverLetter = coverLetter;
    }

    @Nullable
    public String getPortfolioUrl() {
        return portfolioUrl;
    }

    public void setPortfolioUrl(@Nullable String portfolioUrl) {
        this.portfolioUrl = portfolioUrl;
    }

    @Nullable
    public String getLinkedinUrl() {
        return linkedinUrl;
    }

    public void setLinkedinUrl(@Nullable String linkedinUrl) {
        this.linkedinUrl = linkedinUrl;
    }

    @Nullable
    public Integer getProgress() {
        return progress;
    }

    public void setProgress(@Nullable Integer progress) {
        this.progress = progress;
    }

    @Nullable
    public ApplicationInterview getInterview() {
        return applicationInterview;
    }

    public void setInterview(@Nullable ApplicationInterview applicationInterview) {
        this.applicationInterview = applicationInterview;
    }
}

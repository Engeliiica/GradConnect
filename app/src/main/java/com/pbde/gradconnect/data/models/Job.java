package com.pbde.gradconnect.data.models;

import androidx.annotation.NonNull;
import com.pbde.gradconnect.data.models.enums.JobType;
import com.pbde.gradconnect.data.models.enums.WorkMode;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

public class Job extends BaseModel {
    @NonNull
    private String title = "";
    @NonNull
    private String companyId = "";
    @NonNull
    private String companyName = "";
    @NonNull
    private String location = "";
    @NonNull
    private JobType type = JobType.FULL_TIME;
    @NonNull
    private WorkMode workMode = WorkMode.ON_SITE;
    @NonNull
    private String level = "";
    @NonNull
    private String description = "";
    @NonNull
    private List<String> requirements = new ArrayList<>();
    @NonNull
    private List<String> responsibilities = new ArrayList<>();
    @NonNull
    private List<String> benefits = new ArrayList<>();
    @NonNull
    private List<String> candidateIds = new ArrayList<>();
    @NonNull
    private String salary = "";
    private boolean active = true;

    // Required no-argument constructor for Firebase
    public Job() {
        super();
    }

    public Job(@NonNull String id, @NonNull Date createdAt, @NonNull Date updatedAt,
              @NonNull String title, @NonNull String companyId, @NonNull String companyName,
              @NonNull String location, @NonNull JobType type, @NonNull WorkMode workMode,
              @NonNull String level, @NonNull String description, @NonNull List<String> requirements,
              @NonNull List<String> responsibilities, @NonNull List<String> benefits,
              @NonNull List<String> candidateIds, @NonNull String salary, boolean active) {
        super(id, createdAt, updatedAt);
        this.title = title;
        this.companyId = companyId;
        this.companyName = companyName;
        this.location = location;
        this.type = type;
        this.workMode = workMode;
        this.level = level;
        this.description = description;
        this.requirements = requirements != null ? requirements : new ArrayList<>();
        this.responsibilities = responsibilities != null ? responsibilities : new ArrayList<>();
        this.benefits = benefits != null ? benefits : new ArrayList<>();
        this.candidateIds = candidateIds != null ? candidateIds : new ArrayList<>();
        this.salary = salary;
        this.active = active;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    @NonNull
    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(@NonNull String companyId) {
        this.companyId = companyId;
    }

    @NonNull
    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(@NonNull String companyName) {
        this.companyName = companyName;
    }

    @NonNull
    public String getLocation() {
        return location;
    }

    public void setLocation(@NonNull String location) {
        this.location = location;
    }

    @NonNull
    public JobType getType() {
        return type;
    }

    public void setType(@NonNull JobType type) {
        this.type = type;
    }

    @NonNull
    public WorkMode getWorkMode() {
        return workMode;
    }

    public void setWorkMode(@NonNull WorkMode workMode) {
        this.workMode = workMode;
    }

    @NonNull
    public String getLevel() {
        return level;
    }

    public void setLevel(@NonNull String level) {
        this.level = level;
    }

    @NonNull
    public String getDescription() {
        return description;
    }

    public void setDescription(@NonNull String description) {
        this.description = description;
    }

    @NonNull
    public List<String> getRequirements() {
        return requirements;
    }

    public void setRequirements(@NonNull List<String> requirements) {
        this.requirements = requirements != null ? requirements : new ArrayList<>();
    }

    @NonNull
    public List<String> getResponsibilities() {
        return responsibilities;
    }

    public void setResponsibilities(@NonNull List<String> responsibilities) {
        this.responsibilities = responsibilities != null ? responsibilities : new ArrayList<>();
    }

    @NonNull
    public List<String> getBenefits() {
        return benefits;
    }

    public void setBenefits(@NonNull List<String> benefits) {
        this.benefits = benefits != null ? benefits : new ArrayList<>();
    }

    @NonNull
    public List<String> getCandidateIds() {
        return candidateIds;
    }

    public void setCandidateIds(@NonNull List<String> candidateIds) {
        this.candidateIds = candidateIds != null ? candidateIds : new ArrayList<>();
    }

    @NonNull
    public String getSalary() {
        return salary;
    }

    public void setSalary(@NonNull String salary) {
        this.salary = salary;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isValid() {
        return !title.trim().isEmpty() &&
               !companyId.trim().isEmpty() &&
               !location.trim().isEmpty() &&
               !description.trim().isEmpty();
    }

    public static class Builder {
        private final Job job;

        public Builder() {
            this.job = new Job();
        }

        public Builder withId(String id) {
            job.setId(id);
            return this;
        }

        public Builder withTitle(String title) {
            job.setTitle(title);
            return this;
        }

        public Builder withCompanyId(String companyId) {
            job.setCompanyId(companyId);
            return this;
        }

        public Builder withCompanyName(String companyName) {
            job.setCompanyName(companyName);
            return this;
        }

        public Builder withLocation(String location) {
            job.setLocation(location);
            return this;
        }

        public Builder withType(JobType type) {
            job.setType(type);
            return this;
        }

        public Builder withWorkMode(WorkMode workMode) {
            job.setWorkMode(workMode);
            return this;
        }

        public Builder withDescription(String description) {
            job.setDescription(description);
            return this;
        }

        public Builder withLevel(String level) {
            job.setLevel(level);
            return this;
        }

        public Builder withRequirements(List<String> requirements) {
            job.setRequirements(requirements);
            return this;
        }

        public Builder withResponsibilities(List<String> responsibilities) {
            job.setResponsibilities(responsibilities);
            return this;
        }

        public Builder withBenefits(List<String> benefits) {
            job.setBenefits(benefits);
            return this;
        }

        public Builder withSalary(String salary) {
            job.setSalary(salary);
            return this;
        }

        public Builder withActive(boolean active) {
            job.setActive(active);
            return this;
        }

        public Job build() {
            return job;
        }
    }
}

package com.pbde.gradconnect.ui.employer.jobs;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.pbde.gradconnect.data.models.Employer;
import com.pbde.gradconnect.data.models.Job;
import com.pbde.gradconnect.data.models.enums.JobType;
import com.pbde.gradconnect.data.models.enums.WorkMode;
import com.pbde.gradconnect.data.repository.JobsRepository;
import com.pbde.gradconnect.data.repository.UserRepository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EmployerCreateEditJobViewModel extends ViewModel {
    private final JobsRepository jobsRepository;
    private final UserRepository userRepository;
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Employer> employerData = new MutableLiveData<>();
    private final MutableLiveData<Job> jobData = new MutableLiveData<>();
    private boolean isEditMode = false;

    public EmployerCreateEditJobViewModel() {
        this.jobsRepository = new JobsRepository();
        this.userRepository = new UserRepository();

        loadEmployer();
    }

    public void loadEmployer() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRepository.getUserByUid(userId)
            .addOnSuccessListener(user -> {
                if (user instanceof Employer) {
                    employerData.setValue((Employer) user);
                }
            });
    }

    public void loadJob(String jobId) {
        if (jobId == null || jobId.isEmpty()) return;
        
        isEditMode = true;
        jobsRepository.getJob(jobId).observeForever(job -> {
            if (job != null) {
                jobData.setValue(job);
            } else {
                errorMessage.setValue("Job not found");
            }
        });
    }

    public LiveData<Job> getJobData() {
        return jobData;
    }

    public boolean isEditMode() {
        return isEditMode;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Job> createJob(String title, String location, String type, String level,
                                   String workMode, String description, List<String> requirements,
                                   List<String> responsibilities, List<String> benefits,
                                   String salary) {
        if (employerData == null) {
            errorMessage.setValue("Employer information not set");
            return null;
        }

        if (title.trim().isEmpty()) {
            errorMessage.setValue("Title is required");
            return new MutableLiveData<>(null);
        }

        if (location.trim().isEmpty()) {
            errorMessage.setValue("Location is required");
            return new MutableLiveData<>(null);
        }

        if (description.trim().isEmpty()) {
            errorMessage.setValue("Description is required");
            return new MutableLiveData<>(null);
        }

        Job job;
        if (isEditMode && jobData.getValue() != null) {
            // Update existing job
            job = jobData.getValue();
            job.setTitle(title);
            job.setLocation(location);
            job.setType(JobType.fromString(type));
            job.setWorkMode(WorkMode.fromString(workMode));
            job.setDescription(description);
            job.setLevel(level);
            job.setRequirements(requirements);
            job.setResponsibilities(responsibilities);
            job.setBenefits(benefits);
            job.setSalary(salary);
            job.setUpdatedAt(new Date());
        } else {
            // Create new job
            job = new Job.Builder()
                .withId(UUID.randomUUID().toString())
                .withTitle(title)
                .withCompanyId(employerData.getValue().getId())
                .withCompanyName(employerData.getValue().getProfile().getCompanyName())
                .withLocation(location)
                .withType(JobType.fromString(type))
                .withWorkMode(WorkMode.fromString(workMode))
                .withDescription(description)
                .withLevel(level)
                .withRequirements(requirements)
                .withResponsibilities(responsibilities)
                .withBenefits(benefits)
                .withSalary(salary)
                .withActive(true)
                .build();
            job.setCreatedAt(new Date());
            job.setUpdatedAt(new Date());
        }

        if (!job.isValid()) {
            errorMessage.setValue("Please fill in all required fields");
            return null;
        }
        
        if (isEditMode) {
            return jobsRepository.updateJob(job.getId(), jobToMap(job));
        } else {
            return jobsRepository.createJob(job);
        }
    }

    public Map<String, Object> jobToMap(Job job) {
        Map<String, Object> jobMap = new HashMap<>();
        jobMap.put("title", job.getTitle());
        jobMap.put("location", job.getLocation());
        jobMap.put("type", job.getType().getValue());
        jobMap.put("workMode", job.getWorkMode().getValue());
        jobMap.put("description", job.getDescription());
        jobMap.put("level", job.getLevel());
        jobMap.put("requirements", job.getRequirements());
        jobMap.put("responsibilities", job.getResponsibilities());
        jobMap.put("benefits", job.getBenefits());
        jobMap.put("salary", job.getSalary());
        return jobMap;
    }
}
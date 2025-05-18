package com.pbde.gradconnect.ui.candidate.jobs;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.pbde.gradconnect.data.models.Job;
import com.pbde.gradconnect.data.models.Application;
import com.pbde.gradconnect.data.repository.JobsRepository;
import com.pbde.gradconnect.data.repository.ApplicationsRepository;
import com.pbde.gradconnect.util.AuthManager;

public class CandidateSingleJobViewModel extends ViewModel {
    private final JobsRepository jobsRepository;
    private final ApplicationsRepository applicationsRepository;
    private final MutableLiveData<Job> job = new MutableLiveData<>();
    private final MutableLiveData<Application> application = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private String currentJobId;

    public CandidateSingleJobViewModel() {
        jobsRepository = new JobsRepository();
        applicationsRepository = new ApplicationsRepository();
    }

    public void loadJob(String jobId) {
        if (jobId == null) {
            error.setValue("Job ID is required");
            return;
        }

        currentJobId = jobId;
        isLoading.setValue(true);
        error.setValue(null);

        // Load job details
        jobsRepository.getJob(jobId).observeForever(result -> {
            isLoading.setValue(false);
            if (result != null) {
                job.setValue(result);
                // Once we have the job, check for existing application
                checkExistingApplication(jobId);
            } else {
                error.setValue("Failed to load job details");
            }
        });
    }

    private void checkExistingApplication(String jobId) {
        String candidateId = AuthManager.getInstance().getCurrentUser().getValue().getId();
        applicationsRepository.getApplicationByJobAndCandidateId(jobId, candidateId)
            .observeForever(result -> {
                application.setValue(result);
            });
    }

    public LiveData<Job> getJob() {
        return job;
    }

    public LiveData<Application> getApplication() {
        return application;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public String getApplicationId() {
        Application app = application.getValue();
        return app != null ? app.getId() : null;
    }
}
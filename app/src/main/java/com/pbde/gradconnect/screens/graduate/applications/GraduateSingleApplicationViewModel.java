package com.pbde.gradconnect.screens.graduate.applications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.pbde.gradconnect.data.models.Application;
import com.pbde.gradconnect.data.models.Job;
import com.pbde.gradconnect.data.repository.ApplicationsRepository;
import com.pbde.gradconnect.data.repository.JobsRepository;

public class GraduateSingleApplicationViewModel extends ViewModel {
    private final ApplicationsRepository applicationsRepository;
    private final JobsRepository jobsRepository;

    public GraduateSingleApplicationViewModel() {
        applicationsRepository = new ApplicationsRepository();
        jobsRepository = new JobsRepository();
    }

    public LiveData<Application> getApplication(String applicationId) {
        return applicationsRepository.getApplication(applicationId);
    }

    public LiveData<Job> getJob(String jobId) {
        return jobsRepository.getJob(jobId);
    }
}
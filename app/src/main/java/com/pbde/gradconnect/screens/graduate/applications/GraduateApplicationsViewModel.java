package com.pbde.gradconnect.screens.graduate.applications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.annotation.NonNull;

import com.pbde.gradconnect.data.models.Job;
import com.pbde.gradconnect.data.models.Application;
import com.pbde.gradconnect.data.repository.ApplicationsRepository;
import com.pbde.gradconnect.data.repository.JobsRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class GraduateApplicationsViewModel extends ViewModel {
    private final ApplicationsRepository applicationsRepository;
    private final JobsRepository jobsRepository;
    
    private final MutableLiveData<List<Application>> applications = new MutableLiveData<>();
    private final MutableLiveData<Map<String, List<Job>>> applicationsJobs = new MutableLiveData<>(new HashMap<>());
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public GraduateApplicationsViewModel() {
        applicationsRepository = new ApplicationsRepository();
        jobsRepository = new JobsRepository();
        loadApplicationsAndJobs();
    }

    public void refresh() {
        loadApplicationsAndJobs();
    }

    private void loadApplicationsAndJobs() {
        loading.setValue(true);
        error.setValue(null);

        applicationsRepository.getCandidateApplications().observeForever(applicationList -> {
            if (applicationList != null) {
                applications.setValue(applicationList);
                
                if (applicationList.isEmpty()) {
                    // If there are no applications, we're done loading
                    loading.setValue(false);
                } else {
                    loadJobsForApplications(applicationList);
                }
            } else {
                error.setValue("Failed to load applications");
                loading.setValue(false);
            }
        });
    }

    private void loadJobsForApplications(@NonNull List<Application> applicationList) {
        Map<String, List<Job>> jobsMap = new HashMap<>();
        int totalApplications = applicationList.size();
        AtomicInteger loadedJobs = new AtomicInteger(0);

        for (Application application : applicationList) {
            String jobId = application.getJobId();
            jobsRepository.getJob(jobId).observeForever(job -> {
                if (job != null) {
                    List<Job> jobs = jobsMap.get(application.getId());
                    if (jobs == null) {
                        jobs = new ArrayList<>();
                        jobsMap.put(application.getId(), jobs);
                    }
                    jobs.add(job);
                } else {
                    // Handle case where job couldn't be loaded
                    String errorMsg = error.getValue();
                    String newError = "Failed to load job details for application: " + application.getId();
                    error.setValue(errorMsg == null ? newError : errorMsg + "\n" + newError);
                }

                if (loadedJobs.incrementAndGet() == totalApplications) {
                    applicationsJobs.setValue(jobsMap);
                    loading.setValue(false);
                }
            });
        }
    }

    public LiveData<List<Application>> getApplications() {
        return applications;
    }

    public MutableLiveData<Map<String, List<Job>>> getApplicationsJobs() {
        return applicationsJobs;
    }

    public LiveData<Boolean> isLoading() {
        return loading;
    }

    public LiveData<String> getError() {
        return error;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Clean up observers if needed
    }
}
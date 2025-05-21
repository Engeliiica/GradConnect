package com.pbde.gradconnect.screens.graduate.jobs;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.pbde.gradconnect.data.repository.JobsRepository;
import com.pbde.gradconnect.data.models.Job;
import java.util.List;

public class GraduateJobsViewModel extends ViewModel {
    private final JobsRepository jobsRepository;
    private LiveData<List<Job>> activeJobs;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>(null);
    private final MutableLiveData<FilterState> currentFilters = new MutableLiveData<>(new FilterState());

    public static class FilterState {
        private String workMode = "";
        private String jobType = "";

        public String getWorkMode() { return workMode; }
        public void setWorkMode(String workMode) { this.workMode = workMode; }
        
        public String getJobType() { return jobType; }
        public void setJobType(String jobType) { this.jobType = jobType; }
        
        public boolean hasActiveFilters() {
            return !workMode.isEmpty() || !jobType.isEmpty();
        }
    }

    public GraduateJobsViewModel() {
        jobsRepository = new JobsRepository();
        loadActiveJobs();
    }

    private void loadActiveJobs() {
        isLoading.setValue(true);
        errorMessage.setValue(null);
        try {
            activeJobs = jobsRepository.getActiveJobs();
            isLoading.setValue(false);
        } catch (Exception e) {
            isLoading.setValue(false);
            errorMessage.setValue("Failed to load jobs: " + e.getMessage());
        }
    }

    public LiveData<List<Job>> getActiveJobs() {
        if (activeJobs == null) {
            loadActiveJobs();
        }
        return activeJobs;
    }

    public LiveData<List<Job>> getCandidateJobs(String candidateId) {
        isLoading.setValue(true);
        errorMessage.setValue(null);
        try {
            LiveData<List<Job>> jobs = jobsRepository.getCandidateJobs(candidateId);
            isLoading.setValue(false);
            return jobs;
        } catch (Exception e) {
            isLoading.setValue(false);
            errorMessage.setValue("Failed to load candidate jobs: " + e.getMessage());
            return new MutableLiveData<>();
        }
    }

    public LiveData<List<Job>> getFilteredJobs(String workMode, String jobType) {
        isLoading.setValue(true);
        errorMessage.setValue(null);
        
        // Update current filters state
        FilterState filterState = new FilterState();
        filterState.setWorkMode(workMode != null ? workMode : "");
        filterState.setJobType(jobType != null ? jobType : "");
        currentFilters.setValue(filterState);
        
        try {
            LiveData<List<Job>> jobs = jobsRepository.getFilteredJobs(workMode, jobType);
            isLoading.setValue(false);
            return jobs;
        } catch (Exception e) {
            isLoading.setValue(false);
            errorMessage.setValue("Failed to apply filters: " + e.getMessage());
            return new MutableLiveData<>();
        }
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    
    public LiveData<FilterState> getCurrentFilters() {
        return currentFilters;
    }
    
    public void resetFilters() {
        currentFilters.setValue(new FilterState());
    }
}

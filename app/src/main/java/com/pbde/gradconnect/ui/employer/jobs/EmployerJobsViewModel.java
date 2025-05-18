package com.pbde.gradconnect.ui.employer.jobs;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.pbde.gradconnect.data.models.Employer;
import com.pbde.gradconnect.data.models.User;
import com.pbde.gradconnect.data.repository.JobsRepository;
import com.pbde.gradconnect.data.models.Job;
import com.pbde.gradconnect.data.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

public class EmployerJobsViewModel extends ViewModel {
    private final JobsRepository jobsRepository;
    private final UserRepository userRepository;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Employer> employerData = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private String currentEmployerId;
    private LiveData<List<Job>> employerJobs = new MutableLiveData<>(new ArrayList<>());

    public EmployerJobsViewModel() {
        jobsRepository = new JobsRepository();
        userRepository = new UserRepository();
        loadEmployer();
    }
    
    public LiveData<Employer> getEmployerData() {
        return employerData;
    }

    public void loadEmployer() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRepository.getUserByUid(userId)
            .addOnSuccessListener(user -> {
                if (user instanceof Employer) {                    
                    employerData.setValue((Employer) user);
                } else {
                    isLoading.setValue(false);
                }
            })
            .addOnFailureListener(e -> {
                isLoading.setValue(false);
            });
    } 

    public LiveData<List<Job>> getEmployerJobs(String employerId) {
        currentEmployerId = employerId;
        setLoading(true);
        try {
            employerJobs = jobsRepository.getEmployerJobs(employerId);
            employerJobs.observeForever(jobs -> {
                if (jobs == null) {
                    error.setValue("Failed to load jobs");
                }
                setLoading(false);
            });
        } catch (Exception e) {
            error.setValue(e.getMessage());
            setLoading(false);
        }
        return employerJobs;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    public LiveData<List<Job>> getAllEmployerJobs() {
        return employerJobs;
    }

    public void createJob(Job job) {
        setLoading(true);
        jobsRepository.createJob(job);
    }

    public void deleteJob(String jobId) {
        setLoading(true);
        jobsRepository.deleteJob(jobId);
    }

    public LiveData<List<Job>> getFilteredEmployerJobs(String workMode, String jobType) {
        setLoading(true);
        try {
            LiveData<List<Job>> jobs = jobsRepository.getFilteredEmployerJobs(currentEmployerId, workMode, jobType);
            jobs.observeForever(jobList -> {
                if (jobList == null) {
                    error.setValue("Failed to apply filters");
                }
                setLoading(false);
            });
            return jobs;
        } catch (Exception e) {
            error.setValue(e.getMessage());
            setLoading(false);
            return new MutableLiveData<>(new ArrayList<>());
        }
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void setLoading(boolean loading) {
        isLoading.setValue(loading);
    }
}

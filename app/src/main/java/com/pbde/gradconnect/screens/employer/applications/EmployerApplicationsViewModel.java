package com.pbde.gradconnect.screens.employer.applications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.pbde.gradconnect.data.models.Graduate;
import com.pbde.gradconnect.data.models.Employer;
import com.pbde.gradconnect.data.repository.ApplicationsRepository;
import com.pbde.gradconnect.data.repository.JobsRepository;
import com.pbde.gradconnect.data.models.Application;
import com.pbde.gradconnect.data.models.Job;
import com.pbde.gradconnect.data.repository.UserRepository;

import java.util.*;

public class EmployerApplicationsViewModel extends ViewModel {
    private final ApplicationsRepository applicationsRepository;
    private final JobsRepository jobsRepository;

    private final UserRepository userRepository;
    private final MutableLiveData<Employer> employerData = new MutableLiveData<>();
    
    private final MutableLiveData<List<Application>> applications = new MutableLiveData<>();
    private final MutableLiveData<Map<String, List<Job>>> applicationsJobs = new MutableLiveData<>(new HashMap<>());
    private final MutableLiveData<Map<String, List<Graduate>>> applicationsCandidates = new MutableLiveData<>(new HashMap<>());

    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public EmployerApplicationsViewModel() {
        applicationsRepository = new ApplicationsRepository();
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
                }
            });
    }

    public void loadApplicationsJobsAndCandidates(String employerId) {
        loading.setValue(true);
        error.setValue(null);

        applicationsRepository.getEmployerApplications(employerId).observeForever(applicationList -> {
            if (applicationList != null) {
                applications.setValue(applicationList);
                loadJobsForApplications(applicationList);
                loadCandidatesForApplications(applicationList);
            } else {
                error.setValue("Failed to load applications");
                loading.setValue(false);
            }
        });
    }
    
    private void loadJobsForApplications(List<Application> applicationList) {
        Map<String, List<Job>> jobsMap = new HashMap<>();
        for (Application application : applicationList) {
            String jobId = application.getJobId();
            jobsRepository.getJob(jobId).observeForever(job -> {
                if (job != null) {
                    if (!jobsMap.containsKey(jobId)) {
                        jobsMap.put(jobId, new ArrayList<>());
                    }
                    jobsMap.get(jobId).add(job);
                }
                applicationsJobs.setValue(jobsMap);
            });
        }
        loading.setValue(false);
    }

    private void loadCandidatesForApplications(List<Application> applicationList) {
        Map<String, List<Graduate>> candidatesMap = new HashMap<>();
        for (Application application : applicationList) {
            String jobId = application.getJobId();
            applicationsRepository.getCandidatesForJob(jobId, userRepository).observeForever(candidates -> {
                if (candidates != null) {
                    candidatesMap.put(jobId, candidates);
                }
                applicationsCandidates.setValue(candidatesMap);
            });
        }
        loading.setValue(false);
    }



    public LiveData<List<Application>> getApplications() {
        return applications;
    }

    public LiveData<Map<String, List<Job>>> getApplicationsJobs() {
        return applicationsJobs;
    }

    public LiveData<Map<String, List<Graduate>>> getApplicationsCandidates() {
        return applicationsCandidates;
    }

    public LiveData<Boolean> isLoading() {
        return loading;
    }

    public LiveData<String> getError() {
        return error;
    }
}

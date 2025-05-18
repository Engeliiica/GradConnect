package com.pbde.gradconnect.ui.employer.applications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pbde.gradconnect.data.models.Application;
import com.pbde.gradconnect.data.models.Chat;
import com.pbde.gradconnect.data.models.Job;
import com.pbde.gradconnect.data.models.Candidate;
import com.pbde.gradconnect.data.models.User;
import com.pbde.gradconnect.data.models.enums.ApplicationStatus;
import com.pbde.gradconnect.data.repository.ApplicationsRepository;
import com.pbde.gradconnect.data.repository.JobsRepository;
import com.pbde.gradconnect.data.repository.UserRepository;
import com.pbde.gradconnect.data.repository.ChatsRepository;
import com.pbde.gradconnect.util.AuthManager;

public class EmployerSingleApplicationViewModel extends ViewModel {
    private final ApplicationsRepository repository;
    private final JobsRepository jobsRepository;
    private final UserRepository userRepository;
    private final ChatsRepository chatsRepository;
    private final AuthManager authManager;

    // Objects to store loaded data
    private MutableLiveData<Application> applicationLiveData = new MutableLiveData<>();
    private MutableLiveData<Job> jobLiveData = new MutableLiveData<>();
    private MutableLiveData<Candidate> candidateLiveData = new MutableLiveData<>();
    private MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> loadingLiveData = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> actionSuccessLiveData = new MutableLiveData<>();

    public EmployerSingleApplicationViewModel() {
        repository = new ApplicationsRepository();
        jobsRepository = new JobsRepository();
        userRepository = new UserRepository();
        chatsRepository = new ChatsRepository();
        authManager = AuthManager.getInstance();
    }

    public LiveData<Application> getApplication(String applicationId) {
        if (applicationId == null) {
            errorLiveData.setValue("Invalid application ID");
            return applicationLiveData;
        }
        
        loadingLiveData.setValue(true);
        repository.getApplication(applicationId).observeForever(application -> {
            applicationLiveData.setValue(application);
            loadingLiveData.setValue(false);
            
            if (application != null) {
                // Load related job and candidate data
                loadJobData(application.getJobId());
                loadCandidateData(application.getCandidateId());
            } else {
                errorLiveData.setValue("Application not found");
            }
        });
        
        return applicationLiveData;
    }

    private void loadJobData(String jobId) {
        if (jobId == null) return;
        
        jobsRepository.getJob(jobId).observeForever(job -> {
            jobLiveData.setValue(job);
            if (job == null) {
                errorLiveData.setValue("Job not found");
            }
        });
    }

    private void loadCandidateData(String candidateId) {
        if (candidateId == null) return;
        
        userRepository.getUserById(candidateId).addOnSuccessListener(user -> {
            if (user instanceof Candidate) {
                candidateLiveData.setValue((Candidate) user);
            } else {
                errorLiveData.setValue("Candidate information not available");
            }
        }).addOnFailureListener(e -> {
            errorLiveData.setValue("Failed to load candidate: " + e.getMessage());
        });
    }

    public LiveData<Job> getJobData() {
        return jobLiveData;
    }

    public LiveData<Candidate> getCandidateData() {
        return candidateLiveData;
    }

    public LiveData<String> getError() {
        return errorLiveData;
    }

    public LiveData<Boolean> isLoading() {
        return loadingLiveData;
    }

    public LiveData<Boolean> getActionSuccess() {
        return actionSuccessLiveData;
    }

    public void setApplicationStatus(String applicationId, ApplicationStatus status) {
        if (applicationId == null) {
            errorLiveData.setValue("Invalid application ID");
            actionSuccessLiveData.setValue(false);
            return;
        }
        
        loadingLiveData.setValue(true);
        repository.updateApplicationStatus(applicationId, status);
        
        // Refresh application data after update
        repository.getApplication(applicationId).observeForever(updatedApplication -> {
            applicationLiveData.setValue(updatedApplication);
            loadingLiveData.setValue(false);
            actionSuccessLiveData.setValue(true);
        });
    }

    public void scheduleInterview(String applicationId) {
        setApplicationStatus(applicationId, ApplicationStatus.INTERVIEW);
    }

    public void rejectApplication(String applicationId) {
        setApplicationStatus(applicationId, ApplicationStatus.REJECTED);
    }

    public LiveData<Chat> findOrCreateChat(String employerId, String candidateId) {
        if (employerId == null || candidateId == null) {
            MutableLiveData<Chat> errorResult = new MutableLiveData<>(null);
            errorLiveData.setValue("Invalid user information for chat");
            return errorResult;
        }
        
        loadingLiveData.setValue(true);
        LiveData<Chat> chatLiveData = chatsRepository.findOrCreateChat(employerId, candidateId);
        
        chatLiveData.observeForever(chat -> {
            loadingLiveData.setValue(false);
            if (chat == null) {
                errorLiveData.setValue("Failed to create chat");
            }
        });
        
        return chatLiveData;
    }

    public String getCurrentEmployerId() {
        User currentUser = authManager.getCurrentUser().getValue();
        return currentUser != null ? currentUser.getId() : null;
    }
}
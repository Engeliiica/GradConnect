package com.pbde.gradconnect.screens.employer.jobs;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pbde.gradconnect.data.models.Application;
import com.pbde.gradconnect.data.models.Graduate;
import com.pbde.gradconnect.data.models.Chat;
import com.pbde.gradconnect.data.models.Job;
import com.pbde.gradconnect.data.repository.ApplicationsRepository;
import com.pbde.gradconnect.data.repository.ChatsRepository;
import com.pbde.gradconnect.data.repository.JobsRepository;

public class EmployerSingleJobViewModel extends ViewModel {
    private final JobsRepository jobsRepository;
    private final ApplicationsRepository applicationsRepository;
    private final ChatsRepository chatsRepository;
    private final MutableLiveData<Application> application = new MutableLiveData<>();
    private final MutableLiveData<Job> job = new MutableLiveData<>();
    private final MutableLiveData<Graduate> candidate = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private String currentJobId;

    public EmployerSingleJobViewModel() {
        jobsRepository = new JobsRepository();
        applicationsRepository = new ApplicationsRepository();
        chatsRepository = new ChatsRepository();
    }

    public LiveData<Application> getApplication() { return application; }
    public LiveData<Graduate> getCandidate() { return candidate; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getError() { return error; }
    public LiveData<Job> getJob() { return job; }

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
            } else {
                error.setValue("Failed to load job details");
            }
        });
    }

    public void deleteJob(String jobId) {
        if (jobId == null) {
            error.setValue("Job ID is required");
            return;
        }

        isLoading.setValue(true);
        error.setValue(null);

        jobsRepository.deleteJob(jobId).addOnSuccessListener(aVoid -> {
            isLoading.setValue(false);
            // Navigate back to jobs list
            // This will be handled by the fragment
        }).addOnFailureListener(e -> {
            isLoading.setValue(false);
            error.setValue("Failed to delete job: " + e.getMessage());
        });
    }

//    private void checkExistingApplication(String jobId) {
//        String candidateId = AuthManager.getInstance().getCurrentUser().getValue().getId();
//        applicationsRepository.getApplicationByJobAndCandidateId(jobId, candidateId)
//                .observeForever(result -> {
//                    application.setValue(result);
//                });
//    }

    public LiveData<Chat> createChat(String employerId, String candidateId) {
        if (employerId == null || candidateId == null) {
            MutableLiveData<Chat> errorResult = new MutableLiveData<>();
            errorResult.setValue(null);
            error.setValue("Invalid user IDs for chat creation");
            return errorResult;
        }
        
        isLoading.setValue(true);
        LiveData<Chat> result = chatsRepository.findOrCreateChat(employerId, candidateId);
        result.observeForever(chat -> isLoading.setValue(false));
        return result;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}

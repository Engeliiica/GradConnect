package com.pbde.gradconnect.screens.graduate.jobs;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pbde.gradconnect.data.models.Application;
import com.pbde.gradconnect.data.models.Chat;
import com.pbde.gradconnect.data.models.Job;
import com.pbde.gradconnect.data.models.enums.ApplicationStatus;
import com.pbde.gradconnect.data.repository.ApplicationsRepository;
import com.pbde.gradconnect.data.repository.ChatsRepository;
import com.pbde.gradconnect.data.repository.JobsRepository;
import com.pbde.gradconnect.data.repository.UserRepository;

import java.util.UUID;
import java.util.Date;

public class GraduateJobApplicationViewModel extends ViewModel {
    private final JobsRepository jobsRepository;
    private final ApplicationsRepository applicationsRepository;
    private final UserRepository userRepository;
    private final ChatsRepository chatsRepository;

    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();private final MutableLiveData<Job> job = new MutableLiveData<>();
    private final MutableLiveData<Application> application = new MutableLiveData<>();
    private final MutableLiveData<Chat> chat = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isEditMode = new MutableLiveData<>(false);

    private String jobId;
    private String userId;

    public static class JobDetail {
        public String id;
        public String title;
        public String company;
        public String companyId;
        public String location;
        public String type;
        public String level;
        public String salary;
        public String posted;
        public String description;
        // Add other fields as needed
    }

    public static class ApplicationData {
        public String id;
        public String jobId;
        public String candidateId;
        public String employerId;
        public String status;
        public int progress;
        public String appliedAt;
        public String coverLetter;
        public String portfolioUrl;
        public String linkedinUrl;
    }

    public static class ChatData {
        public String id;
        public String employerId;
        public String candidateId;
    }

    public GraduateJobApplicationViewModel(
            JobsRepository jobsRepository,
            ApplicationsRepository applicationsRepository,
            UserRepository userRepository,
            ChatsRepository chatsRepository) {
        this.jobsRepository = jobsRepository;
        this.applicationsRepository = applicationsRepository;
        this.userRepository = userRepository;
        this.chatsRepository = chatsRepository;
    }

    public void init(String jobId, String userId) {
        this.jobId = jobId;
        this.userId = userId;
        loadJob();
        loadApplication();
    }

    public void loadJob() {
        loading.setValue(true);
        if (jobId == null) {
            error.setValue("Job ID is missing");
            loading.setValue(false);
            return;
        }

        jobsRepository.getJob(jobId).observeForever(jobData -> {
            if (jobData != null) {
                job.setValue(jobData);
            } else {
                error.setValue("Job not found");
            }
            loading.setValue(false);
        });
    }

    private void loadApplication() {
        if (jobId == null || userId == null) {
            error.setValue("Missing job or user information");
            return;
        }

        applicationsRepository.getApplicationByJobAndCandidateId(jobId, userId)
                .observeForever(applicationData -> {
                    application.setValue(applicationData);
                    if (applicationData != null) {
                        loadChat();
                    }
                });
    }

    private void loadChat() {
        Application currentApplication = application.getValue();
        if (currentApplication == null) return;

        // Assuming you have a method to get chat by employer and candidate
        chatsRepository.getChatByEmployerIdAndCandidateId(
                currentApplication.getEmployerId(),
                currentApplication.getCandidateId()
        ).observeForever(chat::setValue);
    }

    public LiveData<Job> getJob() {
        return jobsRepository.getJob(jobId);
    }

    public LiveData<Application> getApplication() {
        return applicationsRepository.getApplicationByJobAndCandidateId(jobId, userId);
    }

    public LiveData<Chat> getChat() {
        return chat;
    }

    public LiveData<Boolean> getIsEditMode() {
        return isEditMode;
    }
    
    public void setEditMode(boolean editMode) {
        isEditMode.setValue(editMode);
    }
    
    public void submitApplication(String coverLetter, String portfolioUrl, String linkedinUrl) {
        if (jobId == null || userId == null) {
            error.setValue("Missing job or user information");
            return;
        }

        loading.setValue(true);
        error.setValue(null);

        // Check if application already exists
        applicationsRepository.getApplicationByJobAndCandidateId(jobId, userId)
                .observeForever(existingApplication -> {
                    if (existingApplication != null) {
                        // Update existing application
                        updateApplication(existingApplication, coverLetter, portfolioUrl, linkedinUrl);
                    } else {
                        // Create new application
                        createNewApplication(coverLetter, portfolioUrl, linkedinUrl);
                    }
                });
    }
    
    private void updateApplication(Application existingApplication, String coverLetter, 
                                  String portfolioUrl, String linkedinUrl) {
        existingApplication.setCoverLetter(coverLetter);
        existingApplication.setPortfolioUrl(portfolioUrl);
        existingApplication.setLinkedinUrl(linkedinUrl);
        existingApplication.setUpdatedAt(new Date());
        
        try {
            // Pass the application ID as the first parameter
            applicationsRepository.updateApplication(existingApplication.getId(), existingApplication);
            loading.setValue(false);
        } catch (Exception e) {
            loading.setValue(false);
            error.setValue("Failed to update application: " + e.getMessage());
        }
    }
    
    private void createNewApplication(String coverLetter, String portfolioUrl, String linkedinUrl) {
        // Get job details first
        jobsRepository.getJob(jobId).observeForever(job -> {
            if (job != null) {
                Application application = new Application(
                        UUID.randomUUID().toString(),
                        new Date(),
                        new Date(),
                        jobId,
                        userId,
                        job.getCompanyId(),
                        ApplicationStatus.SUBMITTED,
                        new Date(),
                        null, // resumeUrl
                        coverLetter,
                        portfolioUrl,
                        linkedinUrl,
                        40,
                        null // interview
                );

                applicationsRepository.createApplication(application);
                loading.setValue(false);
            } else {
                error.setValue("Failed to load job details");
                loading.setValue(false);
            }
        });
    }

    public LiveData<Boolean> getLoading() { return loading; }
    public LiveData<String> getError() { return error; }
}
package com.pbde.gradconnect.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pbde.gradconnect.data.models.Application;
import com.pbde.gradconnect.data.models.ApplicationInterview;
import com.pbde.gradconnect.data.models.Candidate;
import com.pbde.gradconnect.data.models.User;
import com.pbde.gradconnect.data.models.enums.ApplicationStatus;
import com.pbde.gradconnect.util.AuthManager;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ApplicationsRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String APPLICATIONS_COLLECTION = "applications";
    private final AuthManager authManager = AuthManager.getInstance();
    public LiveData<List<Application>> getCandidateApplications() {
        MutableLiveData<List<Application>> applicationsLiveData = new MutableLiveData<>();

        db.collection(APPLICATIONS_COLLECTION)
            .whereEqualTo("candidateId", getCurrentUserId())
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<Application> applications = new ArrayList<>();
                for (DocumentSnapshot document : queryDocumentSnapshots) {
                    Application application = documentToApplication(document);
                    applications.add(application);
                }
                applicationsLiveData.setValue(applications);
            })
            .addOnFailureListener(e -> {
                // Handle any errors
                applicationsLiveData.setValue(new ArrayList<>());
            });

        return applicationsLiveData;
    }

    public LiveData<Application> getApplication(String applicationId) {
        MutableLiveData<Application> applicationLiveData = new MutableLiveData<>();
        
        if (applicationId == null) {
            applicationLiveData.setValue(null);
            return applicationLiveData;
        }

        db.collection(APPLICATIONS_COLLECTION)
            .document(applicationId)
            .get()
            .addOnSuccessListener(document -> {
                if (document.exists()) {
                    applicationLiveData.setValue(documentToApplication(document));
                } else {
                    applicationLiveData.setValue(null);
                }
            })
            .addOnFailureListener(e -> applicationLiveData.setValue(null));

        return applicationLiveData;
    }

    public LiveData<Application> getApplicationByJobAndCandidateId(String jobId, String userId) {
        MutableLiveData<Application> applicationLiveData = new MutableLiveData<>();

        db.collection(APPLICATIONS_COLLECTION)
            .whereEqualTo("jobId", jobId)
            .whereEqualTo("candidateId", userId)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                if (!queryDocumentSnapshots.isEmpty()) {
                    applicationLiveData.setValue(documentToApplication(queryDocumentSnapshots.getDocuments().get(0)));
                } else {
                    applicationLiveData.setValue(null);
                }
            })
            .addOnFailureListener(e -> applicationLiveData.setValue(null));

        return applicationLiveData;
    }

    public LiveData<List<Application>> getJobApplications(String jobId) {
        MutableLiveData<List<Application>> applicationsLiveData = new MutableLiveData<>();

        db.collection(APPLICATIONS_COLLECTION)
            .whereEqualTo("jobId", jobId)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<Application> applications = new ArrayList<>();
                for (DocumentSnapshot document : queryDocumentSnapshots) {
                    applications.add(documentToApplication(document));
                }
                applicationsLiveData.setValue(applications);
            })
            .addOnFailureListener(e -> applicationsLiveData.setValue(new ArrayList<>()));

        return applicationsLiveData;
    }

    public LiveData<List<Application>> getEmployerApplications(String employerId) {
        MutableLiveData<List<Application>> applicationsLiveData = new MutableLiveData<>();

        db.collection(APPLICATIONS_COLLECTION)
            .whereEqualTo("employerId", employerId)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<Application> applications = new ArrayList<>();
                for (DocumentSnapshot document : queryDocumentSnapshots) {
                    applications.add(documentToApplication(document));
                }
                applicationsLiveData.setValue(applications);
            })
            .addOnFailureListener(e -> applicationsLiveData.setValue(new ArrayList<>()));

        return applicationsLiveData;
    }

    public LiveData<List<Candidate>> getCandidatesForJob(String jobId, UserRepository userRepository) {
        MutableLiveData<List<Candidate>> candidatesLiveData = new MutableLiveData<>();

        db.collection(APPLICATIONS_COLLECTION)
            .whereEqualTo("jobId", jobId)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<String> candidateIds = new ArrayList<>();
                for (DocumentSnapshot document : queryDocumentSnapshots) {
                    Application application = documentToApplication(document);
                    candidateIds.add(application.getCandidateId());
                }
                
                if (candidateIds.isEmpty()) {
                    candidatesLiveData.setValue(new ArrayList<>());
                    return;
                }

                userRepository.getUsersByIds(candidateIds)
                    .addOnSuccessListener(users -> {
                        List<Candidate> candidates = new ArrayList<>();
                        for (User user : users) {
                            candidates.add((Candidate) user);
                        }
                        candidatesLiveData.setValue(candidates);
                    })
                    .addOnFailureListener(e -> candidatesLiveData.setValue(new ArrayList<>()));
            })
            .addOnFailureListener(e -> candidatesLiveData.setValue(new ArrayList<>()));

        return candidatesLiveData;
    }

    public void createApplication(Application application) {
        db.collection(APPLICATIONS_COLLECTION)
            .document(application.getId())
            .set(applicationToMap(application));
    }

    public void updateApplication(String applicationId, Application application) {
        db.collection(APPLICATIONS_COLLECTION)
            .document(applicationId)
            .update(applicationToMap(application));
    }

    public void updateApplicationStatus(String applicationId, ApplicationStatus status) {
        int progress = calculateProgress(status);
        db.collection(APPLICATIONS_COLLECTION)
            .document(applicationId)
            .update("status", status.getValue(), "progress", progress);
    }

    public void deleteApplication(String applicationId) {
        db.collection(APPLICATIONS_COLLECTION)
            .document(applicationId)
            .delete();
    }

    private int calculateProgress(ApplicationStatus status) {
        switch (status) {
            case INTERVIEW:
                return 70;
            case ACCEPTED:
            case REJECTED:
                return 100;
            case WITHDRAWN:
                return 0;
            default:
                return 40;
        }
    }

    @NonNull
    private java.util.Map<String, Object> applicationToMap(Application application) {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("id", application.getId());
        map.put("jobId", application.getJobId());
        map.put("candidateId", application.getCandidateId());
        map.put("employerId", application.getEmployerId());
        map.put("status", application.getStatus().toString());
        map.put("coverLetter", application.getCoverLetter());
        map.put("resumeUrl", application.getResumeUrl());
        map.put("portfolioUrl", application.getPortfolioUrl());
        map.put("linkedinUrl", application.getLinkedinUrl());
        map.put("progress", application.getProgress());
        map.put("createdAt", formatDate(application.getCreatedAt()));
        map.put("updatedAt", formatDate(application.getUpdatedAt()));
        map.put("appliedAt", formatDate(application.getAppliedAt()));
        return map;
    }

    private String formatDate(Date date) {
        if (date == null) return null;
        SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        iso8601Format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return iso8601Format.format(date);
    }

    @NonNull
    private Application documentToApplication(DocumentSnapshot document) {
        String id = document.getId();
        String jobId = document.getString("jobId");
        String candidateId = document.getString("candidateId");
        String employerId = document.getString("employerId");
        ApplicationStatus status = ApplicationStatus.fromString(document.getString("status"));
        String coverLetter = document.getString("coverLetter");
        String resumeUrl = document.getString("resumeUrl");
        String portfolioUrl = document.getString("portfolioUrl");
        String linkedinUrl = document.getString("linkedinUrl");
        ApplicationInterview applicationInterview = null;
        Integer progress = document.getLong("progress").intValue();
        
        String createdAtString = document.getString("createdAt");
        String updatedAtString = document.getString("updatedAt");
        String appliedAtString = document.getString("appliedAt");
        Date createdAt = parseDate(createdAtString);
        Date updatedAt = parseDate(updatedAtString);
        Date appliedAt = parseDate(appliedAtString);

        return new Application(id, createdAt, updatedAt,
                jobId, candidateId, employerId,
                status, appliedAt,
                resumeUrl, coverLetter,
                portfolioUrl, linkedinUrl,
                progress, applicationInterview
        );
    }

    private String getCurrentUserId() {
        return authManager.getCurrentUser().getValue() != null ? 
            authManager.getCurrentUser().getValue().getId() : 
            null;
    }

    private Date parseDate(String dateString) {
        if (dateString == null) return null;
        try {
            SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            iso8601Format.setTimeZone(TimeZone.getTimeZone("UTC"));
            return iso8601Format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}

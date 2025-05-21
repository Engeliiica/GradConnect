package com.pbde.gradconnect.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.pbde.gradconnect.data.models.Job;
import com.pbde.gradconnect.data.models.JobFilters;
import com.pbde.gradconnect.data.models.enums.JobType;
import com.pbde.gradconnect.data.models.enums.WorkMode;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class JobsRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final String JOBS_COLLECTION = "jobs";

    public LiveData<List<Job>> getActiveJobs() {
        MutableLiveData<List<Job>> jobsLiveData = new MutableLiveData<>();

        db.collection(JOBS_COLLECTION)
            .whereEqualTo("active", true)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<Job> jobs = new ArrayList<>();
                for (DocumentSnapshot document : queryDocumentSnapshots) {
                    Job job = this.documentToJob(document);
                    jobs.add(job);
                }
                jobsLiveData.setValue(jobs);
            })
            .addOnFailureListener(e -> {
                // Handle any errors
                jobsLiveData.setValue(new ArrayList<>());
            });

        return jobsLiveData;
    }

    public LiveData<List<Job>> getCandidateJobs(String candidateId) {
        MutableLiveData<List<Job>> jobsLiveData = new MutableLiveData<>();

        db.collection(JOBS_COLLECTION)
            .whereArrayContains("candidateIds", candidateId)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<Job> jobs = new ArrayList<>();
                for (DocumentSnapshot document : queryDocumentSnapshots) {
                    Job job = this.documentToJob(document);
                    jobs.add(job);
                }
                jobsLiveData.setValue(jobs);
            })
            .addOnFailureListener(e -> {
                // Handle any errors
                jobsLiveData.setValue(new ArrayList<>());
            });

        return jobsLiveData;
    }

    public LiveData<List<Job>> getActiveJobs(String employerId) {
        MutableLiveData<List<Job>> jobsLiveData = new MutableLiveData<>();
        Query query = db.collection(JOBS_COLLECTION)
            .whereEqualTo("active", true);

        if (employerId != null && !employerId.isEmpty()) {
            query = query.whereEqualTo("companyId", employerId);
        }

        query.get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<Job> jobs = new ArrayList<>();
                for (DocumentSnapshot document : queryDocumentSnapshots) {
                    jobs.add(documentToJob(document));
                }
                jobsLiveData.setValue(jobs);
            })
            .addOnFailureListener(e -> jobsLiveData.setValue(new ArrayList<>()));

        return jobsLiveData;
    }

    public LiveData<List<Job>> getEmployerJobs(String employerId) {
        MutableLiveData<List<Job>> jobsLiveData = new MutableLiveData<>();

        db.collection(JOBS_COLLECTION)
            .whereEqualTo("companyId", employerId)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<Job> jobs = new ArrayList<>();
                for (DocumentSnapshot document : queryDocumentSnapshots) {
                    jobs.add(documentToJob(document));
                }
                jobsLiveData.setValue(jobs);
            })
            .addOnFailureListener(e -> jobsLiveData.setValue(new ArrayList<>()));

        return jobsLiveData;
    }

    public LiveData<Job> getJob(String jobId) {
        MutableLiveData<Job> jobLiveData = new MutableLiveData<>();

        db.collection(JOBS_COLLECTION)
            .document(jobId)
            .get()
            .addOnSuccessListener(document -> {
                if (document.exists()) {
                    jobLiveData.setValue(documentToJob(document));
                } else {
                    jobLiveData.setValue(null);
                }
            })
            .addOnFailureListener(e -> jobLiveData.setValue(null));

        return jobLiveData;
    }

    public LiveData<Job> createJob(Job job) {
        MutableLiveData<Job> jobLiveData = new MutableLiveData<>();
        Map<String, Object> jobDocument = jobToDocument(job);

        db.collection(JOBS_COLLECTION)
            .document(job.getId())
            .set(jobDocument)
            .addOnSuccessListener(aVoid -> jobLiveData.setValue(job))
            .addOnFailureListener(e -> jobLiveData.setValue(null));

        return jobLiveData;
    }

    public LiveData<Job> updateJob(String jobId, Map<String, Object> updates) {
        MutableLiveData<Job> jobLiveData = new MutableLiveData<>();

        db.collection(JOBS_COLLECTION)
            .document(jobId)
            .update(updates)
            .addOnSuccessListener(aVoid -> {
                // Fetch the updated job
                getJob(jobId).observeForever(jobLiveData::setValue);
            })
            .addOnFailureListener(e -> jobLiveData.setValue(null));

        return jobLiveData;
    }

    public Task<Void> deleteJob(String jobId) {
        return db.collection(JOBS_COLLECTION)
                .document(jobId)
                .delete(); // Now you're returning the Task to the caller
    }

    public LiveData<List<Job>> getCandidateFilteredJobs(JobFilters filters) {
        MutableLiveData<List<Job>> jobsLiveData = new MutableLiveData<>();

        Query query = db.collection(JOBS_COLLECTION)
            .whereEqualTo("active", true)
            .whereIn("type", filters.getTypes())
            .whereIn("workMode", filters.getWorkModes());

        query.get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<Job> jobs = new ArrayList<>();
                for (DocumentSnapshot document : queryDocumentSnapshots) {
                    jobs.add(documentToJob(document));
                }
                jobsLiveData.setValue(jobs);
            })
            .addOnFailureListener(e -> jobsLiveData.setValue(new ArrayList<>()));

        return jobsLiveData;
    }

    public LiveData<List<Job>> getEmployerFilteredJobs(String employerId, JobFilters filters) {
        MutableLiveData<List<Job>> jobsLiveData = new MutableLiveData<>();

        Query query = db.collection(JOBS_COLLECTION)
            .whereEqualTo("companyId", employerId)
            .whereIn("type", filters.getTypes())
            .whereIn("workMode", filters.getWorkModes());

        query.get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<Job> jobs = new ArrayList<>();
                for (DocumentSnapshot document : queryDocumentSnapshots) {
                    jobs.add(documentToJob(document));
                }
                jobsLiveData.setValue(jobs);
            })
            .addOnFailureListener(e -> jobsLiveData.setValue(new ArrayList<>()));

        return jobsLiveData;
    }

    public LiveData<List<Job>> getFilteredJobs(String workMode, String jobType) {
        MutableLiveData<List<Job>> jobsLiveData = new MutableLiveData<>();

        Query query = db.collection(JOBS_COLLECTION)
            .whereEqualTo("active", true);

        if (!workMode.isEmpty()) {
            query = query.whereEqualTo("workMode", workMode);
        }

        if (!jobType.isEmpty()) {
            query = query.whereEqualTo("type", jobType);
        }

        query.get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<Job> jobs = new ArrayList<>();
                for (DocumentSnapshot document : queryDocumentSnapshots) {
                    jobs.add(documentToJob(document));
                }
                jobsLiveData.setValue(jobs);
            })
            .addOnFailureListener(e -> jobsLiveData.setValue(new ArrayList<>()));

        return jobsLiveData;
    }

    public LiveData<List<Job>> getFilteredEmployerJobs(String employerId, String workMode, String jobType) {
        MutableLiveData<List<Job>> jobsLiveData = new MutableLiveData<>();

        Query query = db.collection(JOBS_COLLECTION)
            .whereEqualTo("companyId", employerId);

        if (!workMode.isEmpty()) {
            query = query.whereEqualTo("workMode", workMode);
        }

        if (!jobType.isEmpty()) {
            query = query.whereEqualTo("type", jobType);
        }

        query.get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<Job> jobs = new ArrayList<>();
                for (DocumentSnapshot document : queryDocumentSnapshots) {
                    jobs.add(documentToJob(document));
                }
                jobsLiveData.setValue(jobs);
            })
            .addOnFailureListener(e -> jobsLiveData.setValue(new ArrayList<>()));

        return jobsLiveData;
    }

    public LiveData<List<Job>> getFilteredJobs(String workMode, String jobType, String searchQuery) {
        MutableLiveData<List<Job>> jobsLiveData = new MutableLiveData<>();

        Query query = db.collection(JOBS_COLLECTION)
            .whereEqualTo("active", true);

        if (!workMode.isEmpty()) {
            query = query.whereEqualTo("workMode", workMode);
        }

        if (!jobType.isEmpty()) {
            query = query.whereEqualTo("type", jobType);
        }

        if (!searchQuery.isEmpty()) {
            query = query.whereGreaterThanOrEqualTo("title", searchQuery)
                         .whereLessThanOrEqualTo("title", searchQuery + "\uf8ff");
        }

        query.get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<Job> jobs = new ArrayList<>();
                for (DocumentSnapshot document : queryDocumentSnapshots) {
                    jobs.add(documentToJob(document));
                }
                jobsLiveData.setValue(jobs);
            })
            .addOnFailureListener(e -> jobsLiveData.setValue(new ArrayList<>()));

        return jobsLiveData;
    }

    @NonNull
    private Job documentToJob(DocumentSnapshot document) {
        String id = document.getId();
        String title = document.getString("title");
        String description = document.getString("description");
        String location = document.getString("location");
        String companyId = document.getString("companyId");
        String companyName = document.getString("companyName");
        JobType type = JobType.fromString(document.getString("type"));
        WorkMode workMode = WorkMode.fromString(document.getString("workMode"));
        String level = document.getString("level");
        List<String> requirements = (List<String>) document.get("requirements");
        List<String> responsibilities = (List<String>) document.get("responsibilities");
        List<String> benefits = (List<String>) document.get("benefits");
        List<String> candidateIds = (List<String>) document.get("candidateIds");
        String salary = document.getString("salary");
        Boolean active = document.getBoolean("active");

        String createdAtString = document.getString("createdAt");
        String updatedAtString = document.getString("updatedAt");
        Date createdAt = parseDate(createdAtString);
        Date updatedAt = parseDate(updatedAtString);

        return new Job(id,
            createdAt,
            updatedAt,
            title,
            companyId,
            companyName,
            location,
            type,
            workMode,
            level,
            description,
            requirements,
            responsibilities,
            benefits,
            candidateIds,
            salary,
            active
        );
    }

    private Map<String, Object> jobToDocument(Job job) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", job.getId());
        map.put("title", job.getTitle());
        map.put("description", job.getDescription());
        map.put("location", job.getLocation());
        map.put("companyId", job.getCompanyId());
        map.put("companyName", job.getCompanyName());
        map.put("type", job.getType().getValue());
        map.put("workMode", job.getWorkMode().getValue());
        map.put("level", job.getLevel());
        map.put("requirements", job.getRequirements());
        map.put("responsibilities", job.getResponsibilities());
        map.put("benefits", job.getBenefits());
        map.put("candidateIds", job.getCandidateIds());
        map.put("salary", job.getSalary());
        map.put("active", job.isActive());
        return map;
    }

    @NonNull
    private Map<String, Object> jobToMap(Job job) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", job.getId());
        map.put("title", job.getTitle());
        map.put("description", job.getDescription());
        map.put("location", job.getLocation());
        map.put("companyId", job.getCompanyId());
        map.put("companyName", job.getCompanyName());
        map.put("type", job.getType().toString());
        map.put("workMode", job.getWorkMode().toString());
        map.put("level", job.getLevel());
        map.put("requirements", job.getRequirements());
        map.put("responsibilities", job.getResponsibilities());
        map.put("benefits", job.getBenefits());
        map.put("candidateIds", job.getCandidateIds());
        map.put("salary", job.getSalary());
        map.put("active", job.isActive());
        map.put("createdAt", formatDate(job.getCreatedAt()));
        map.put("updatedAt", formatDate(job.getUpdatedAt()));
        return map;
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

    private String formatDate(Date date) {
        if (date == null) return null;
        SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        iso8601Format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return iso8601Format.format(date);
    }
}

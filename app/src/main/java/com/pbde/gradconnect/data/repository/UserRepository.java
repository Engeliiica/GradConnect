package com.pbde.gradconnect.data.repository;

import androidx.annotation.NonNull;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pbde.gradconnect.data.models.CandidateEducation;
import com.pbde.gradconnect.data.models.CandidateExperience;
import com.pbde.gradconnect.data.models.CandidateProfile;
import com.pbde.gradconnect.data.models.EmployerProfile;
import com.pbde.gradconnect.data.models.User;
import com.pbde.gradconnect.data.models.Candidate;
import com.pbde.gradconnect.data.models.Employer;
import com.pbde.gradconnect.data.models.enums.UserRole;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;
import java.util.ArrayList;
import java.util.List;
import com.google.firebase.firestore.QuerySnapshot;
import android.util.Log;
import com.pbde.gradconnect.utils.DateUtils;

public class UserRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String USERS_COLLECTION = "users";
    
    public Task<User> getUserByEmail(String email) {
        return db.collection(USERS_COLLECTION)
                .whereEqualTo("email", email)
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        return documentToUser(document);
                    }
                    return null;
                });
    }
    
    public Task<User> getUserById(String id) {
        return db.collection(USERS_COLLECTION)
                .document(id)
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        return documentToUser(task.getResult());
                    }
                    return null;
                });
    }

    public Task<User> getUserByUid(String uid) {
        return db.collection(USERS_COLLECTION)
                .whereEqualTo("uid", uid)
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        return documentToUser(document);
                    }
                    return null;
                });
    }
    
    public Task<Void> createUser(User user) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("id", user.getId());
        userData.put("uid", user.getUid());
        userData.put("fullName", user.getFullName());
        userData.put("email", user.getEmail());
        userData.put("role", user.getRole().getRoleString());
        userData.put("createdAt", DateUtils.formatToIso8601(user.getCreatedAt()));
        userData.put("updatedAt", DateUtils.formatToIso8601(user.getUpdatedAt()));
        
        return db.collection(USERS_COLLECTION)
                .document(user.getId())
                .set(userData);
    }

    public Task<List<User>> getUsersByIds(List<String> userIds) {
        return db.collection(USERS_COLLECTION)
                .whereIn("id", userIds)
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        List<User> users = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult().getDocuments()) {
                            users.add(documentToUser(document));
                        }
                        return users;
                    }
                    return new ArrayList<>();
                });
    }
    
    @NonNull
    private User documentToUser(DocumentSnapshot document) {
        String id = document.getString("id");
        String uid = document.getString("uid");
        String fullName = document.getString("fullName");
        String email = document.getString("email");
        Map<String, Object> profileMap = (Map<String, Object>) document.get("profile");
        String roleString = document.getString("role");
        UserRole role = roleString != null ? UserRole.fromString(roleString) : UserRole.GUEST;

        String createdAtString = document.getString("createdAt");
        String updatedAtString = document.getString("updatedAt");
        Date createdAt = parseDate(createdAtString);
        Date updatedAt = parseDate(updatedAtString);

        if (role == UserRole.CANDIDATE) {
            CandidateProfile candidateProfile = null;
            if (profileMap != null) {
                List<CandidateEducation> education = parseEducation(profileMap);
                List<CandidateExperience> experience = parseExperience(profileMap);
                List<String> skills = profileMap.get("skills") instanceof List ? 
                    (List<String>) profileMap.get("skills") : 
                    new ArrayList<>();
                String phone = (String) profileMap.get("phone");
                String location = (String) profileMap.get("location");

                candidateProfile = new CandidateProfile(
                    phone != null ? phone : "",
                    location != null ? location : "",
                    education,
                    experience,
                    skills
                );
            }
            return new Candidate(id, createdAt, updatedAt, uid, fullName, email, candidateProfile);
        } else if (role == UserRole.EMPLOYER) {
            EmployerProfile employerProfile = null;
            if (profileMap != null) {
                String companyName = (String) profileMap.get("companyName");
                String companyDescription = (String) profileMap.get("companyDescription");
                String website = (String) profileMap.get("website");

                if (companyName != null) {
                    employerProfile = new EmployerProfile(
                        companyName,
                        companyDescription,
                        website
                    );
                }
            }
            return new Employer(id, createdAt, updatedAt, uid, fullName, email, employerProfile);
        }
        
        return new User(id, createdAt, updatedAt, uid, fullName, email, role, null, null);
    }

    private List<CandidateEducation> parseEducation(Map<String, Object> profileMap) {
        List<CandidateEducation> educationList = new ArrayList<>();
        if (profileMap.get("education") instanceof List) {
            List<Map<String, Object>> educationData = (List<Map<String, Object>>) profileMap.get("education");
            for (Map<String, Object> edu : educationData) {
                String degree = (String) edu.get("degree");
                String institution = (String) edu.get("institution");
                String description = (String) edu.get("description");
                Date startDate = parseDate((String) edu.get("startDate"));
                Date endDate = parseDate((String) edu.get("endDate"));
                
                if (degree != null && institution != null && startDate != null) {
                    educationList.add(new CandidateEducation(
                        degree,
                        institution,
                        startDate,
                        endDate,
                        description
                    ));
                }
            }
        }
        return educationList;
    }

    private List<CandidateExperience> parseExperience(Map<String, Object> profileMap) {
        List<CandidateExperience> experienceList = new ArrayList<>();
        if (profileMap.get("experience") instanceof List) {
            List<Map<String, Object>> experienceData = (List<Map<String, Object>>) profileMap.get("experience");
            for (Map<String, Object> exp : experienceData) {
                String title = (String) exp.get("title");
                String company = (String) exp.get("company");
                String description = (String) exp.get("description");
                Date startDate = parseDate((String) exp.get("startDate"));
                Date endDate = parseDate((String) exp.get("endDate"));
                
                if (title != null && company != null && startDate != null) {
                    experienceList.add(new CandidateExperience(
                        title,
                        company,
                        startDate,
                        endDate,
                        description
                    ));
                }
            }
        }
        return experienceList;
    }

    private Date parseDate(String dateString) {
        return DateUtils.parseIsoDate(dateString);
    }

    private String dateToIso8601(Date date) {
        return DateUtils.formatToIso8601(date);
    }
    
    public Task<Void> updateUserProfile(String userId, Map<String, Object> updates) {
        return db.collection(USERS_COLLECTION)
                .document(userId)
                .update(updates);
    }

    public Task<List<Candidate>> getCandidates() {
        return db.collection(USERS_COLLECTION)
                .whereEqualTo("role", UserRole.CANDIDATE.getRoleString())
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        List<Candidate> candidates = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            User user = documentToUser(document);
                            if (user instanceof Candidate) {
                                candidates.add((Candidate) user);
                            }
                        }
                        return candidates;
                    }
                    return new ArrayList<>();
                });
    }

    public Task<List<Employer>> getEmployers() {
        return db.collection(USERS_COLLECTION)
                .whereEqualTo("role", UserRole.EMPLOYER.getRoleString())
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        List<Employer> employers = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            User user = documentToUser(document);
                            if (user instanceof Employer) {
                                employers.add((Employer) user);
                            }
                        }
                        return employers;
                    }
                    return new ArrayList<>();
                });
    }

    public Task<Candidate> getCandidateById(String candidateId) {
        return db.collection(USERS_COLLECTION)
                .document(candidateId)
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        User user = documentToUser(task.getResult());
                        if (user instanceof Candidate) {
                            return (Candidate) user;
                        }
                    }
                    return null;
                });
    }

    public Task<List<Candidate>> getManyCandidatesByIds(List<String> candidateIds) {
        return db.collection(USERS_COLLECTION)
                .whereEqualTo("role", UserRole.CANDIDATE.getRoleString())
                .whereIn("id", candidateIds)
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        List<Candidate> candidates = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult().getDocuments()) {
                            User user = documentToUser(document);
                            if (user instanceof Candidate) {
                                candidates.add((Candidate) user);
                            }
                        }
                        return candidates;
                    }
                    return new ArrayList<>();
                });
    }

    public Task<List<Employer>> getManyEmployersByIds(List<String> employerIds) {
        return db.collection(USERS_COLLECTION)
                .whereEqualTo("role", UserRole.EMPLOYER.getRoleString())
                .whereIn("id", employerIds)
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        List<Employer> employers = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            User user = documentToUser(document);
                            if (user instanceof Employer) {
                                employers.add((Employer) user);
                            }
                        }
                        return employers;
                    }
                    return new ArrayList<>();
                });
    }

    public Task<List<Candidate>> searchCandidatesByFullname(String fullName) {
        return db.collection(USERS_COLLECTION)
                .whereEqualTo("role", UserRole.CANDIDATE.getRoleString())
                .whereEqualTo("fullName", fullName)
                .limit(10)
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        List<Candidate> candidates = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult().getDocuments()) {
                            User user = documentToUser(document);
                            if (user instanceof Candidate) {
                                candidates.add((Candidate) user);
                            }
                        }
                        return candidates;
                    }
                    return new ArrayList<>();
                });
    }

    public Task<Candidate> setCandidateProfile(Candidate candidate) {
        if (candidate.getRole() != UserRole.CANDIDATE) {
            throw new IllegalArgumentException("User is not a candidate");
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("profile", candidate.getProfile().toMap());
        updates.put("fullName", candidate.getFullName());
        updates.put("updatedAt", DateUtils.formatToIso8601(new Date()));

        return db.collection(USERS_COLLECTION)
                .document(candidate.getId())
                .update(updates)
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        candidate.setUpdatedAt(new Date());
                        return candidate;
                    }
                    throw task.getException();
                });
    }

    public Task<Employer> setEmployerProfile(Employer employer) {
        if (employer.getRole() != UserRole.EMPLOYER) {
            throw new IllegalArgumentException("User is not an employer");
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("profile", employer.getProfile());
        updates.put("updatedAt", DateUtils.formatToIso8601(new Date()));

        return db.collection(USERS_COLLECTION)
                .document(employer.getId())
                .update(updates)
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        employer.setUpdatedAt(new Date());
                        return employer;
                    }
                    throw task.getException();
                });
    }
}

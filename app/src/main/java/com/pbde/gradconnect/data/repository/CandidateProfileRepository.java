package com.pbde.gradconnect.data.repository;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pbde.gradconnect.data.models.CandidateEducation;
import com.pbde.gradconnect.data.models.CandidateExperience;
import com.pbde.gradconnect.data.models.CandidateProfile;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class CandidateProfileRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String USERS_COLLECTION = "users";

    public Task<CandidateProfile> getCandidateProfile(String userId) {
        return db.collection(USERS_COLLECTION)
                .document(userId)
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                        DocumentSnapshot document = task.getResult();
                        Map<String, Object> profileData = (Map<String, Object>) document.get("profile");
                        
                        if (profileData != null) {
                            String phone = (String) profileData.get("phone");
                            String location = (String) profileData.get("location");
                            List<String> skills = new ArrayList<>();
                            
                            if (profileData.get("skills") instanceof List) {
                                skills = (List<String>) profileData.get("skills");
                            }
                            
                            List<CandidateEducation> education = parseEducation(profileData);
                            List<CandidateExperience> experience = parseExperience(profileData);
                            
                            return new CandidateProfile(
                                phone != null ? phone : "",
                                location != null ? location : "",
                                education,
                                experience,
                                skills
                            );
                        }
                    }
                    // Return empty profile if nothing found
                    return new CandidateProfile("", "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
                });
    }
    
    private List<CandidateEducation> parseEducation(Map<String, Object> profileData) {
        List<CandidateEducation> educationList = new ArrayList<>();
        if (profileData.get("education") instanceof List) {
            List<Map<String, Object>> educationData = (List<Map<String, Object>>) profileData.get("education");
            for (Map<String, Object> edu : educationData) {
                String degree = (String) edu.get("degree");
                String institution = (String) edu.get("institution");
                String description = (String) edu.get("description");
                
                // Parse dates
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
    
    private List<CandidateExperience> parseExperience(Map<String, Object> profileData) {
        List<CandidateExperience> experienceList = new ArrayList<>();
        if (profileData.get("experience") instanceof List) {
            List<Map<String, Object>> experienceData = (List<Map<String, Object>>) profileData.get("experience");
            for (Map<String, Object> exp : experienceData) {
                String title = (String) exp.get("title");
                String company = (String) exp.get("company");
                String description = (String) exp.get("description");
                
                // Parse dates
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

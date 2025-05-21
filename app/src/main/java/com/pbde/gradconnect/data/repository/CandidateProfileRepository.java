package com.pbde.gradconnect.data.repository;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pbde.gradconnect.data.models.Education;
import com.pbde.gradconnect.data.models.Experience;
import com.pbde.gradconnect.data.models.GraduateProfile;
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

    public Task<GraduateProfile> getCandidateProfile(String userId) {
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
                            
                            List<Education> education = parseEducation(profileData);
                            List<Experience> experience = parseExperience(profileData);
                            
                            return new GraduateProfile(
                                phone != null ? phone : "",
                                location != null ? location : "",
                                education,
                                experience,
                                skills
                            );
                        }
                    }
                    // Return empty profile if nothing found
                    return new GraduateProfile("", "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
                });
    }
    
    private List<Education> parseEducation(Map<String, Object> profileData) {
        List<Education> educationList = new ArrayList<>();
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
                    educationList.add(new Education(
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
    
    private List<Experience> parseExperience(Map<String, Object> profileData) {
        List<Experience> experienceList = new ArrayList<>();
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
                    experienceList.add(new Experience(
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

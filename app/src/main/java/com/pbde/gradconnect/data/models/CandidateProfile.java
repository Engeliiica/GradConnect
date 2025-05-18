package com.pbde.gradconnect.data.models;

import androidx.annotation.NonNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CandidateProfile {
    @NonNull
    private String phone;
    @NonNull
    private String location;
    @NonNull
    private List<CandidateEducation> education;  // Renamed from candidateEducation
    @NonNull
    private List<CandidateExperience> experience;  // Renamed from candidateExperience
    @NonNull
    private List<String> skills;

    public CandidateProfile(@NonNull String phone, @NonNull String location,
                            @NonNull List<CandidateEducation> education, @NonNull List<CandidateExperience> experience,
                            @NonNull List<String> skills) {
        this.phone = phone;
        this.location = location;
        this.education = education;
        this.experience = experience;
        this.skills = skills;
    }

    @NonNull
    public String getPhone() {
        return phone;
    }

    public void setPhone(@NonNull String phone) {
        this.phone = phone;
    }

    @NonNull
    public String getLocation() {
        return location;
    }

    public void setLocation(@NonNull String location) {
        this.location = location;
    }

    @NonNull
    public List<CandidateEducation> getEducation() {
        return education;
    }

    public void setEducation(@NonNull List<CandidateEducation> education) {
        this.education = education;
    }

    @NonNull
    public List<CandidateExperience> getExperience() {
        return experience;
    }

    public void setExperience(@NonNull List<CandidateExperience> experience) {
        this.experience = experience;
    }

    @NonNull
    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(@NonNull List<String> skills) {
        this.skills = skills;
    }
    
    // Add this method to convert profile to a Map for Firestore
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("phone", phone);
        map.put("location", location);
        map.put("education", education);
        map.put("experience", experience);
        map.put("skills", skills);
        return map;
    }
}

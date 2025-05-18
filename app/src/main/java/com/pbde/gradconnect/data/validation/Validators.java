package com.pbde.gradconnect.data.validation;

import androidx.annotation.NonNull;

import com.pbde.gradconnect.data.models.Application;
import com.pbde.gradconnect.data.models.Candidate;
import com.pbde.gradconnect.data.models.Chat;
import com.pbde.gradconnect.data.models.CandidateEducation;
import com.pbde.gradconnect.data.models.Employer;
import com.pbde.gradconnect.data.models.EmployerProfile;
import com.pbde.gradconnect.data.models.CandidateExperience;
import com.pbde.gradconnect.data.models.JobFilters;
import com.pbde.gradconnect.data.models.Job;
import com.pbde.gradconnect.data.models.Message;
import com.pbde.gradconnect.data.models.CandidateProfile;
import com.pbde.gradconnect.data.models.User;

import java.util.List;

public class Validators {

    public static Application validateApplication(@NonNull Application application) {
        if (application.getJobId() == null || application.getJobId().isEmpty()) {
            throw new ValidationException("Job ID is required");
        }
        if (application.getCandidateId() == null || application.getCandidateId().isEmpty()) {
            throw new ValidationException("Candidate ID is required");
        }
        if (application.getEmployerId() == null || application.getEmployerId().isEmpty()) {
            throw new ValidationException("Employer ID is required");
        }
        if (application.getStatus() == null) {
            throw new ValidationException("Status is required");
        }
        if (application.getAppliedAt() == null) {
            throw new ValidationException("Application date is required");
        }
        return application;
    }

//    public static Candidate validateCandidate(@NonNull Candidate candidate) {
//        if (candidate.getUid() == null || candidate.getUid().isEmpty()) {
//            throw new ValidationException("UID is required");
//        }
//        if (candidate.getFullName() == null || candidate.getFullName().isEmpty()) {
//            throw new ValidationException("Full name is required");
//        }
//        if (candidate.getEmail() == null || candidate.getEmail().isEmpty()) {
//            throw new ValidationException("Email is required");
//        }
//        if (candidate.getProfile() == null) {
//            throw new ValidationException("Profile is required");
//        }
//
//        validateProfile(candidate.getProfile());
//
//        return candidate;
//    }

    public static CandidateProfile validateProfile(@NonNull CandidateProfile candidateProfile) {
        if (candidateProfile.getPhone() == null || candidateProfile.getPhone().isEmpty()) {
            throw new ValidationException("Phone is required");
        }
        if (candidateProfile.getLocation() == null || candidateProfile.getLocation().isEmpty()) {
            throw new ValidationException("Location is required");
        }
        
        List<CandidateEducation> candidateEducationList = candidateProfile.getEducation();
        if (candidateEducationList != null) {
            for (CandidateEducation candidateEducation : candidateEducationList) {
                validateEducation(candidateEducation);
            }
        }
        
        List<CandidateExperience> candidateExperienceList = candidateProfile.getExperience();
        if (candidateExperienceList != null) {
            for (CandidateExperience candidateExperience : candidateExperienceList) {
                validateExperience(candidateExperience);
            }
        }
        
        return candidateProfile;
    }

    public static CandidateEducation validateEducation(@NonNull CandidateEducation candidateEducation) {
        if (candidateEducation.getDegree() == null || candidateEducation.getDegree().isEmpty()) {
            throw new ValidationException("Degree is required");
        }
        if (candidateEducation.getInstitution() == null || candidateEducation.getInstitution().isEmpty()) {
            throw new ValidationException("Institution is required");
        }
        return candidateEducation;
    }

    public static CandidateExperience validateExperience(@NonNull CandidateExperience candidateExperience) {
        if (candidateExperience.getTitle() == null || candidateExperience.getTitle().isEmpty()) {
            throw new ValidationException("Title is required");
        }
        if (candidateExperience.getCompany() == null || candidateExperience.getCompany().isEmpty()) {
            throw new ValidationException("Company is required");
        }
        return candidateExperience;
    }

    public static Chat validateChat(@NonNull Chat chat) {
        if (chat.getCandidateId() == null || chat.getCandidateId().isEmpty()) {
            throw new ValidationException("Candidate ID is required");
        }
        if (chat.getEmployerId() == null || chat.getEmployerId().isEmpty()) {
            throw new ValidationException("Employer ID is required");
        }
        return chat;
    }

//    public static Employer validateEmployer(@NonNull Employer employer) {
//        if (employer.getUid() == null || employer.getUid().isEmpty()) {
//            throw new ValidationException("UID is required");
//        }
//        if (employer.getFullName() == null || employer.getFullName().isEmpty()) {
//            throw new ValidationException("Full name is required");
//        }
//        if (employer.getEmail() == null || employer.getEmail().isEmpty()) {
//            throw new ValidationException("Email is required");
//        }
//
//        EmployerProfile profile = employer.getProfile();
//        if (profile != null) {
//            validateEmployerProfile(profile);
//        }
//
//        return employer;
//    }

    public static EmployerProfile validateEmployerProfile(@NonNull EmployerProfile profile) {
        if (profile.getCompanyName() == null || profile.getCompanyName().isEmpty()) {
            throw new ValidationException("Company name is required");
        }
        return profile;
    }

    public static Job validateJob(@NonNull Job job) {
        if (job.getTitle() == null || job.getTitle().isEmpty()) {
            throw new ValidationException("Title is required");
        }
        if (job.getCompanyId() == null || job.getCompanyId().isEmpty()) {
            throw new ValidationException("Company ID is required");
        }
        if (job.getCompanyName() == null || job.getCompanyName().isEmpty()) {
            throw new ValidationException("Company name is required");
        }
        if (job.getLocation() == null || job.getLocation().isEmpty()) {
            throw new ValidationException("Location is required");
        }
        if (job.getType() == null) {
            throw new ValidationException("Job type is required");
        }
        if (job.getWorkMode() == null) {
            throw new ValidationException("Work mode is required");
        }
        return job;
    }

    public static Message validateMessage(@NonNull Message message) {
        if (message.getChatId() == null || message.getChatId().isEmpty()) {
            throw new ValidationException("Chat ID is required");
        }
        if (message.getSenderId() == null || message.getSenderId().isEmpty()) {
            throw new ValidationException("Sender ID is required");
        }
        if (message.getSenderRole() == null) {
            throw new ValidationException("Sender role is required");
        }
        if (message.getContent() == null || message.getContent().isEmpty()) {
            throw new ValidationException("Content is required");
        }
        if (message.getTimestamp() == null) {
            throw new ValidationException("Timestamp is required");
        }
        return message;
    }

    public static User validateUser(@NonNull User user) {
        if (user.getUid() == null || user.getUid().isEmpty()) {
            throw new ValidationException("UID is required");
        }
        if (user.getFullName() == null || user.getFullName().isEmpty()) {
            throw new ValidationException("Full name is required");
        }
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new ValidationException("Email is required");
        }
        if (user.getRole() == null) {
            throw new ValidationException("Role is required");
        }
        return user;
    }

//    public static JobFilters validateFilters(@NonNull JobFilters jobFilters) {
//        if (jobFilters.getType() == null) {
//            throw new ValidationException("Type filters are required");
//        }
//        if (jobFilters.getWorkMode() == null) {
//            throw new ValidationException("Work mode filters are required");
//        }
//        return jobFilters;
//    }
}

package com.pbde.gradconnect.ui.candidate.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.pbde.gradconnect.data.models.Candidate;
import com.pbde.gradconnect.data.models.CandidateProfile;
import com.pbde.gradconnect.data.repository.CandidateProfileRepository;
import com.pbde.gradconnect.data.repository.UserRepository;

public class CandidateProfileViewModel extends ViewModel {
    private final CandidateProfileRepository profileRepository = new CandidateProfileRepository();
    private final UserRepository userRepository = new UserRepository();
    
    private final MutableLiveData<CandidateProfile> profileData = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Candidate> candidateData = new MutableLiveData<>();

    public LiveData<CandidateProfile> getProfileData() {
        return profileData;
    }
    
    public LiveData<Candidate> getCandidateData() {
        return candidateData;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void loadProfile(String userId) {
        isLoading.setValue(true);
        error.setValue(null);
        
        userRepository.getUserByUid(userId)
            .addOnSuccessListener(user -> {
                if (user instanceof Candidate) {
                    candidateData.setValue((Candidate) user);
                    
                    profileRepository.getCandidateProfile(user.getId())
                        .addOnSuccessListener(profile -> {
                            profileData.setValue(profile);
                            isLoading.setValue(false);
                        })
                        .addOnFailureListener(e -> {
                            error.setValue("Failed to load profile details: " + e.getMessage());
                            isLoading.setValue(false);
                        });
                } else {
                    error.setValue("Invalid user type");
                    isLoading.setValue(false);
                }
            })
            .addOnFailureListener(e -> {
                error.setValue("Failed to load user data: " + e.getMessage());
                isLoading.setValue(false);
            });
    }
    
    public void loadCurrentUserProfile() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        loadProfile(userId);
    }

    public void saveProfile(CandidateProfile updatedProfile) {
        saveProfile(updatedProfile, null);
    }

    public void saveProfile(CandidateProfile updatedProfile, String newFullName) {
        if (candidateData.getValue() == null) {
            error.setValue("No user data available");
            return;
        }

        isLoading.setValue(true);
        error.setValue(null);
        
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRepository.getUserByUid(userId)
            .addOnSuccessListener(user -> {
                if (user instanceof Candidate) {
                    Candidate candidate = (Candidate) user;
                    candidate.setProfile(updatedProfile);
                    
                    // Update full name if provided
                    if (newFullName != null && !newFullName.isEmpty()) {
                        candidate.setFullName(newFullName);
                    }
                    
                    userRepository.setCandidateProfile(candidate)
                        .addOnSuccessListener(updatedCandidate -> {
                            candidateData.setValue(updatedCandidate);
                            profileData.setValue(updatedProfile);
                            isLoading.setValue(false);
                        })
                        .addOnFailureListener(e -> {
                            error.setValue("Failed to save profile: " + e.getMessage());
                            isLoading.setValue(false);
                        });
                } else {
                    error.setValue("Invalid user type");
                    isLoading.setValue(false);
                }
            })
            .addOnFailureListener(e -> {
                error.setValue("Failed to load user data: " + e.getMessage());
                isLoading.setValue(false);
            });
    }
}

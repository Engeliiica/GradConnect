package com.pbde.gradconnect.screens.graduate.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.pbde.gradconnect.data.models.Graduate;
import com.pbde.gradconnect.data.models.GraduateProfile;
import com.pbde.gradconnect.data.repository.CandidateProfileRepository;
import com.pbde.gradconnect.data.repository.UserRepository;

public class GraduateProfileViewModel extends ViewModel {
    private final CandidateProfileRepository profileRepository = new CandidateProfileRepository();
    private final UserRepository userRepository = new UserRepository();
    
    private final MutableLiveData<GraduateProfile> profileData = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Graduate> candidateData = new MutableLiveData<>();

    public LiveData<GraduateProfile> getProfileData() {
        return profileData;
    }
    
    public LiveData<Graduate> getCandidateData() {
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
                if (user instanceof Graduate) {
                    candidateData.setValue((Graduate) user);
                    
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

    public void saveProfile(GraduateProfile updatedProfile) {
        saveProfile(updatedProfile, null);
    }

    public void saveProfile(GraduateProfile updatedProfile, String newFullName) {
        if (candidateData.getValue() == null) {
            error.setValue("No user data available");
            return;
        }

        isLoading.setValue(true);
        error.setValue(null);
        
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRepository.getUserByUid(userId)
            .addOnSuccessListener(user -> {
                if (user instanceof Graduate) {
                    Graduate graduates = (Graduate) user;
                    graduates.setProfile(updatedProfile);
                    
                    // Update full name if provided
                    if (newFullName != null && !newFullName.isEmpty()) {
                        graduates.setFullName(newFullName);
                    }
                    
                    userRepository.setCandidateProfile(graduates)
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

package com.pbde.gradconnect.ui.employer.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.pbde.gradconnect.data.models.Employer;
import com.pbde.gradconnect.data.models.EmployerProfile;
import com.pbde.gradconnect.data.repository.EmployerProfileRepository;
import com.pbde.gradconnect.data.repository.UserRepository;
import com.google.android.gms.tasks.Tasks;

public class EmployerProfileViewModel extends ViewModel {
    private final EmployerProfileRepository profileRepository = new EmployerProfileRepository();
    private final UserRepository userRepository = new UserRepository();

    private final MutableLiveData<EmployerProfile> profileData = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Employer> employerData = new MutableLiveData<>();

    public LiveData<EmployerProfile> getProfileData() {
        return profileData;
    }

    public LiveData<Employer> getEmployerData() {
        return employerData;
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
                if (user instanceof Employer) {
                    employerData.setValue((Employer) user);
                    profileRepository.getEmployerProfile(userId)
                        .addOnSuccessListener(profile -> {
                            profileData.setValue(profile);
                            isLoading.setValue(false);
                        })
                        .addOnFailureListener(e -> {
                            error.setValue("Failed to load profile: " + e.getMessage());
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

    public void saveProfile(EmployerProfile updatedProfile) {
        if (employerData.getValue() == null) {
            error.setValue("No user data available");
            return;
        }
        
        if (updatedProfile.getCompanyName().trim().isEmpty()) {
            error.setValue("Company name is required");
            return;
        }

        isLoading.setValue(true);
        error.setValue(null);
        
        profileRepository.saveEmployerProfile(employerData.getValue().getId(), updatedProfile)
            .addOnSuccessListener(aVoid -> {
                Employer employer = employerData.getValue();
                employer.setProfile(updatedProfile);
                employerData.setValue(employer);
                isLoading.setValue(false);
            })
            .addOnFailureListener(e -> {
                error.setValue("Failed to save profile: " + e.getMessage());
                isLoading.setValue(false);
            });
    }

    // Add helper to get current Employer synchronously
    public Employer getCurrentEmployer() {
        return employerData.getValue();
    }
}

package com.pbde.gradconnect.ui.auth.register;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pbde.gradconnect.data.models.enums.UserRole;
import com.pbde.gradconnect.util.AuthManager;

public class AuthRegisterViewModel extends ViewModel {
    private final AuthManager authManager = AuthManager.getInstance();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>(null);

    public LiveData<Boolean> getAuthState() {
        return authManager.getAuthState();
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public UserRole getUserRole() {
        return authManager.getCurrentUser().getValue() != null ?
                authManager.getCurrentUser().getValue().getRole() :
                UserRole.GUEST;
    }

    public void register(String email, String password, UserRole role) {
        isLoading.setValue(true);
        error.setValue(null);

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            error.setValue("Email and password cannot be empty");
            isLoading.setValue(false);
            return;
        }

        authManager.register(email, password, "", role)
                .addOnSuccessListener(authResult -> {
                    authManager.login(email, password)
                            .addOnSuccessListener(loginResult -> {
                                isLoading.setValue(false);
                            })
                            .addOnFailureListener(e -> {
                                error.setValue("Login failed after registration: " + e.getMessage());
                                isLoading.setValue(false);
                            });
                })
                .addOnFailureListener(e -> {
                    error.setValue("Registration failed: " + e.getMessage());
                    isLoading.setValue(false);
                });
    }
}

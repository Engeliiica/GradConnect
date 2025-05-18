package com.pbde.gradconnect.util;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pbde.gradconnect.data.repository.UserRepository;
import com.pbde.gradconnect.data.models.User;
import com.pbde.gradconnect.data.models.enums.UserRole;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

/**
 * Manages authentication state and user identity throughout the application.
 * Implements the singleton pattern to ensure a single instance.
 */
public class AuthManager {
    private static AuthManager instance;
    private final FirebaseAuth firebaseAuth;
    private final UserRepository userRepository;
    private final MutableLiveData<User> currentUser = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isAuthenticated = new MutableLiveData<>(false);

    private AuthManager() {
        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
        // Note: setPersistenceEnabled is handled in the FirebaseApp initialization
        userRepository = new UserRepository();

        // Set up auth state listener
        firebaseAuth.addAuthStateListener(this::handleAuthStateChange);

        // Check initial auth state
        synchronizeAuthState();
    }

    /**
     * Get the singleton instance of AuthManager
     * @return The AuthManager instance
     */
    public static synchronized AuthManager getInstance() {
        if (instance == null) {
            instance = new AuthManager();
        }
        return instance;
    }

    /**
     * Handles Firebase authentication state changes
     * @param auth The Firebase auth instance
     */
    private void handleAuthStateChange(@NonNull FirebaseAuth auth) {
        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser != null) {
            fetchUserData(firebaseUser);
        } else {
            // User is signed out
            currentUser.setValue(null);
            isAuthenticated.setValue(false);
        }
    }

    /**
     * Synchronizes the current authentication state with our local state
     */
    private void synchronizeAuthState() {
        FirebaseUser currentFirebaseUser = firebaseAuth.getCurrentUser();
        if (currentFirebaseUser != null) {
            fetchUserData(currentFirebaseUser);
        }
    }

    /**
     * Fetches user data from Firestore based on Firebase user
     * @param firebaseUser The authenticated Firebase user
     */
    private void fetchUserData(FirebaseUser firebaseUser) {
        String email = firebaseUser.getEmail();
        if (email != null) {
            userRepository.getUserByEmail(email)
                    .addOnSuccessListener(user -> {
                        if (user != null) {
                            currentUser.setValue(user);
                            isAuthenticated.setValue(true);
                        } else {
                            // Handle case where user exists in Firebase Auth but not in Firestore
                            logout();
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure to fetch user data
                        logout();
                    });
        }
    }

    /**
     * Get the current authenticated user as LiveData
     * @return LiveData containing the current user or null if not authenticated
     */
    public LiveData<User> getCurrentUser() {
        return currentUser;
    }

    /**
     * Get the current authentication state as LiveData
     * @return LiveData containing true if authenticated, false otherwise
     */
    public LiveData<Boolean> getAuthState() {
        return isAuthenticated;
    }

    /**
     * Authenticates a user with email and password
     * @param email User's email
     * @param password User's password
     * @return Task representing the login operation
     */
    public Task<AuthResult> login(String email, String password) {
        if (email == null || password == null) {
            throw new IllegalArgumentException("Email and password cannot be null");
        }
        
        return firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    // Authentication state is handled by the AuthStateListener
                })
                .addOnFailureListener(e -> {
                    // Clear any stale state on failure
                    currentUser.setValue(null);
                    isAuthenticated.setValue(false);
                });
    }

    /**
     * Registers a new user with email, password and profile information
     * @param email User's email
     * @param password User's password
     * @param fullName User's full name
     * @param role User's role in the system
     * @return Task representing the registration operation
     */
    public Task<AuthResult> register(String email, String password, String fullName, UserRole role) {
        if (email == null || password == null || fullName == null || role == null) {
            throw new IllegalArgumentException("Registration parameters cannot be null");
        }
        
        return firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = authResult.getUser();
                    if (firebaseUser != null) {
                        String userId = UUID.randomUUID().toString();
                        Date now = new Date();
                        User user = new User(
                                userId,
                                now,
                                now,
                                firebaseUser.getUid(),
                                fullName,
                                email,
                                role,
                                null,
                                null
                        );

                        userRepository.createUser(user)
                                .addOnSuccessListener(aVoid -> {
                                    currentUser.setValue(user);
                                    isAuthenticated.setValue(true);
                                })
                                .addOnFailureListener(e -> {
                                    // If we can't create the user in Firestore, delete the Auth account
                                    firebaseUser.delete();
                                    logout();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    // Clear any stale state on failure
                    currentUser.setValue(null);
                    isAuthenticated.setValue(false);
                });
    }

    /**
     * Signs out the current user
     */
    public void logout() {
        // Make sure to clear local state first before Firebase signOut
        currentUser.setValue(null);
        isAuthenticated.setValue(false);
        
        // Then sign out from Firebase
        firebaseAuth.signOut();
    }

    /**
     * Checks if a user is currently logged in
     * @return true if logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return firebaseAuth.getCurrentUser() != null && currentUser.getValue() != null;
    }
    
    /**
     * Safely gets the current Firebase user UID
     * @return The UID of the current user, or null if not authenticated
     */
    public String getCurrentUserUid() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        return user != null ? user.getUid() : null;
    }
    
    /**
     * Safely gets the current user's Firestore ID
     * @return The Firestore ID of the current user, or null if not authenticated
     */
    public String getCurrentUserId() {
        User user = currentUser.getValue();
        return user != null ? user.getId() : null;
    }
}

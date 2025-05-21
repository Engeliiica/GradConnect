package com.pbde.gradconnect.screens.auth.register;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.pbde.gradconnect.R;
import com.pbde.gradconnect.data.models.enums.UserRole;

public class AuthRegisterFragment extends Fragment {

    private AuthRegisterViewModel viewModel;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private RadioGroup roleRadioGroup;
    private Button registerButton;
    private TextView loginLinkTextView;

    public AuthRegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AuthRegisterViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_auth_register, container, false);

        // Initialize UI elements
        emailEditText = view.findViewById(R.id.editTextEmail);
        passwordEditText = view.findViewById(R.id.editTextPassword);
        confirmPasswordEditText = view.findViewById(R.id.editTextConfirmPassword);
        registerButton = view.findViewById(R.id.buttonRegister);
        roleRadioGroup = view.findViewById(R.id.radioGroupRole);
        loginLinkTextView = view.findViewById(R.id.textViewRegisterLink);

        // Setup observers
        viewModel.getAuthState().observe(getViewLifecycleOwner(), isAuthenticated -> {
            if (isAuthenticated) {
                navigateToUserDashboard();
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            registerButton.setEnabled(!isLoading);
        });

        viewModel.getError().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Snackbar.make(view, errorMessage, Snackbar.LENGTH_LONG)
                        .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.error))
                        .setTextColor(getResources().getColor(R.color.background))
                        .show();
            }
        });

        // Set click listeners
        registerButton.setOnClickListener(v -> handleRegister());
        
        // Setup login link
        loginLinkTextView.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.navigation_login);
        });

        return view;
    }

    private void handleRegister() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();
        
        // Get selected user role
        UserRole selectedRole = UserRole.CANDIDATE; // Default
        if (roleRadioGroup.getCheckedRadioButtonId() == R.id.radioButtonEmployer) {
            selectedRole = UserRole.EMPLOYER;
        }
        
//        viewModel.setUserRole(selectedRole);

        // Basic validation
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required");
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordEditText.setError("Please confirm your password");
            return;
        }

        if (!password.equals(confirmPassword)) {
            passwordEditText.setError("Passwords don't match");
            confirmPasswordEditText.setError("Passwords don't match");
            return;
        }

        // Attempt register
        viewModel.register(email, password, selectedRole);
    }

    private void navigateToUserDashboard() {
        UserRole userRole = viewModel.getUserRole();

        try {
            if (userRole == UserRole.CANDIDATE) {
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_navigation_register_to_activity_graduate_main);
            } else if (userRole == UserRole.EMPLOYER) {
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_navigation_register_to_activity_employer_main);
            }
        } catch (Exception e) {
            Snackbar.make(requireView(), "Navigation error: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }
}
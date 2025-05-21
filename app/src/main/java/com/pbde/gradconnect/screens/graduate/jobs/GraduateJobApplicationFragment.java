package com.pbde.gradconnect.screens.graduate.jobs;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.textfield.TextInputEditText;
import com.pbde.gradconnect.R;
import com.pbde.gradconnect.data.models.Chat;
import com.pbde.gradconnect.data.repository.ApplicationsRepository;
import com.pbde.gradconnect.data.repository.ChatsRepository;
import com.pbde.gradconnect.data.repository.JobsRepository;
import com.pbde.gradconnect.data.repository.UserRepository;
import com.pbde.gradconnect.screens.graduate.chats.GraduateChatActivity;
import com.pbde.gradconnect.util.AuthManager;

public class GraduateJobApplicationFragment extends Fragment {
    private GraduateJobApplicationViewModel viewModel;
    private String jobId;

    private String userId;
    
    // UI components
    private TextView errorText;
    private TextView jobTitle;
    private TextView companyName;
    private TextView location;
    private TextView jobType;
    private TextView salary;
    private Button messageRecruiterButton;
    private TextInputEditText coverLetterInput;
    private TextInputEditText portfolioUrlInput;
    private TextInputEditText linkedinUrlInput;
    private Button cancelButton;
    private Button submitButton;
    private ProgressBar loadingProgress;
    private Button editButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_graduate_job_application, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        jobId = getArguments() != null ? getArguments().getString("jobId") : null;
        
        // Initialize UI components
        errorText = view.findViewById(R.id.errorText);
        jobTitle = view.findViewById(R.id.jobTitle);
        companyName = view.findViewById(R.id.companyName);
        location = view.findViewById(R.id.location);
        jobType = view.findViewById(R.id.jobType);
        salary = view.findViewById(R.id.salary);
        messageRecruiterButton = view.findViewById(R.id.messageRecruiterButton);
        coverLetterInput = view.findViewById(R.id.coverLetterInput);
        portfolioUrlInput = view.findViewById(R.id.portfolioUrlInput);
        linkedinUrlInput = view.findViewById(R.id.linkedinUrlInput);
        cancelButton = view.findViewById(R.id.cancelButton);
        submitButton = view.findViewById(R.id.submitButton);
        loadingProgress = view.findViewById(R.id.loadingProgress);

        // Initialize the edit button (moved up before setupListeners call)
        editButton = new Button(requireContext(), null, com.google.android.material.R.attr.materialButtonStyle);
        editButton.setText("Edit Application");
        editButton.setLayoutParams(new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT));
        
        // Add the edit button to the layout
        ViewGroup formContainer = (ViewGroup) submitButton.getParent();
        formContainer.addView(editButton, 0);
        editButton.setVisibility(View.GONE);

        // Initialize ViewModel with repositories
        viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @Override
            public <T extends ViewModel> T create(Class<T> modelClass) {
                return (T) new GraduateJobApplicationViewModel(
                        new JobsRepository(),
                        new ApplicationsRepository(),
                        new UserRepository(),
                        new ChatsRepository()
                );
            }
        }).get(GraduateJobApplicationViewModel.class);

        // Get arguments
        jobId = getArguments().getString("jobId");
        userId = AuthManager.getInstance().getCurrentUser().getValue().getId();

        // Initialize ViewModel
        viewModel.init(jobId, userId);

        setupObservers();
        setupListeners();
        setupNavigation();
    }

    private void setupObservers() {
        viewModel.getJob().observe(getViewLifecycleOwner(), job -> {
            if (job != null) {
                jobTitle.setText(job.getTitle());
                companyName.setText(job.getCompanyName());
                location.setText(job.getLocation());
                salary.setText(job.getSalary());
            }
        });

        viewModel.getApplication().observe(getViewLifecycleOwner(), application -> {
            if (application != null) {
                // Application exists
                coverLetterInput.setText(application.getCoverLetter());
                portfolioUrlInput.setText(application.getPortfolioUrl());
                linkedinUrlInput.setText(application.getLinkedinUrl());

                // Initially disable form but show edit button
                disableForm();
                editButton.setVisibility(View.VISIBLE);
                submitButton.setText("Update Application");
            } else {
                // No application exists
                editButton.setVisibility(View.GONE);
                enableForm();
                submitButton.setText("Submit Application");
            }
        });

        viewModel.getIsEditMode().observe(getViewLifecycleOwner(), isEditMode -> {
            if (isEditMode) {
                enableForm();
                editButton.setVisibility(View.GONE);
            } else {
                if (viewModel.getApplication().getValue() != null) {
                    disableForm();
                    editButton.setVisibility(View.VISIBLE);
                }
            }
        });

        viewModel.getLoading().observe(getViewLifecycleOwner(), this::updateLoadingState);
        viewModel.getError().observe(getViewLifecycleOwner(), this::showError);
        viewModel.getChat().observe(getViewLifecycleOwner(), chat -> {
            messageRecruiterButton.setVisibility(chat != null ? View.VISIBLE : View.GONE);
        });
    }

    private void setupListeners() {
        submitButton.setOnClickListener(v -> {
            if (validateForm()) {
                viewModel.submitApplication(
                        coverLetterInput.getText().toString(),
                        portfolioUrlInput.getText().toString(),
                        linkedinUrlInput.getText().toString()
                );
                // Exit edit mode after submitting
                viewModel.setEditMode(false);
            }
        });
        
        editButton.setOnClickListener(v -> {
            viewModel.setEditMode(true);
        });
    }

    private void updateLoadingState(boolean isLoading) {
        loadingProgress.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        submitButton.setEnabled(!isLoading);
        coverLetterInput.setEnabled(!isLoading);
        portfolioUrlInput.setEnabled(!isLoading);
        linkedinUrlInput.setEnabled(!isLoading);
    }

    private void showError(@Nullable String errorMessage) {
        if (errorMessage != null && !errorMessage.isEmpty()) {
            errorText.setText(errorMessage);
            errorText.setVisibility(View.VISIBLE);
        } else {
            errorText.setVisibility(View.GONE);
        }
    }

    private void setupNavigation() {
        messageRecruiterButton.setOnClickListener(v -> {
            Chat chat = viewModel.getChat().getValue();
            if (chat != null) onMessageRecruiterButtonClick(chat);
        });

        cancelButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigateUp();
        });
    }

    public void onMessageRecruiterButtonClick(Chat chat) {
        Intent intent = new Intent(requireContext(), GraduateChatActivity.class);
        intent.putExtra("chatId", chat.getId());
        startActivity(intent);
    }

    private void disableForm() {
        coverLetterInput.setEnabled(false);
        portfolioUrlInput.setEnabled(false);
        linkedinUrlInput.setEnabled(false);
        submitButton.setEnabled(false);
        submitButton.setVisibility(View.GONE);

        // Show message recruiter button if chat exists
        if (viewModel.getChat().getValue() != null) {
            messageRecruiterButton.setVisibility(View.VISIBLE);
        }
    }
    
    private void enableForm() {
        coverLetterInput.setEnabled(true);
        portfolioUrlInput.setEnabled(true);
        linkedinUrlInput.setEnabled(true);
        submitButton.setEnabled(true);
        submitButton.setVisibility(View.VISIBLE);
    }

    private boolean validateForm() {
        boolean isValid = true;

        String coverLetter = coverLetterInput.getText().toString().trim();
        if (TextUtils.isEmpty(coverLetter)) {
            coverLetterInput.setError("Cover letter is required");
            isValid = false;
        }

        String portfolioUrl = portfolioUrlInput.getText().toString().trim();
        if (!TextUtils.isEmpty(portfolioUrl) && !Patterns.WEB_URL.matcher(portfolioUrl).matches()) {
            portfolioUrlInput.setError("Please enter a valid URL");
            isValid = false;
        }

        String linkedinUrl = linkedinUrlInput.getText().toString().trim();
        if (!TextUtils.isEmpty(linkedinUrl) && !Patterns.WEB_URL.matcher(linkedinUrl).matches()) {
            linkedinUrlInput.setError("Please enter a valid LinkedIn URL");
            isValid = false;
        }

        return isValid;
    }
}
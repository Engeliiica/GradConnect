package com.pbde.gradconnect.screens.employer.applications;

import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ProgressBar;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.pbde.gradconnect.R;
import com.pbde.gradconnect.data.models.Graduate;
import com.pbde.gradconnect.data.models.Job;
import com.pbde.gradconnect.data.models.Application;
import com.pbde.gradconnect.data.models.enums.ApplicationStatus;
import android.content.Intent;
import com.pbde.gradconnect.screens.employer.chats.EmployerChatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EmployerSingleApplicationFragment extends Fragment {

    private EmployerSingleApplicationViewModel viewModel;

    private String applicationId;
    private TextView jobTitleText;
    private Chip statusChip;
    private TextView companyNameText;
    private TextView applicationDateText;
    private TextView jobDescriptionText;
    private ViewGroup requirementsContainer;
    private Button messageCandidateButton;
    private Button interviewButton;
    private Button rejectButton;
    private TextView candidateNameText, candidateEmailText, coverLetter;
    private ProgressBar progressBar;
    private View contentContainer;

    public static EmployerSingleApplicationFragment newInstance(String applicationId) {
        EmployerSingleApplicationFragment fragment = new EmployerSingleApplicationFragment();
        Bundle args = new Bundle();
        args.putString("applicationId", applicationId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_employer_single_application, container, false);
        
        // Initialize views
        jobTitleText = view.findViewById(R.id.jobTitleText);
        statusChip = view.findViewById(R.id.statusChip);
        companyNameText = view.findViewById(R.id.companyNameText);
        applicationDateText = view.findViewById(R.id.applicationDateText);
        coverLetter = view.findViewById(R.id.coverLetter);
        jobDescriptionText = view.findViewById(R.id.jobDescriptionText);
        requirementsContainer = view.findViewById(R.id.requirementsContainer);
        messageCandidateButton = view.findViewById(R.id.messageCandidateButton);
        interviewButton = view.findViewById(R.id.interviewButton);
        rejectButton = view.findViewById(R.id.rejectButton);
        candidateNameText = view.findViewById(R.id.candidateNameText);
        candidateEmailText = view.findViewById(R.id.candidateEmailText);
        
        // Add progress bar programmatically if not in layout
        progressBar = new ProgressBar(requireContext());
        progressBar.setId(View.generateViewId());
        ViewGroup rootView = (ViewGroup) view;
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, 
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        progressBar.setLayoutParams(params);
        rootView.addView(progressBar);
        progressBar.setVisibility(View.GONE);
        
        contentContainer = view.findViewById(R.id.contentContainer);
        
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(EmployerSingleApplicationViewModel.class);
        
        // Get applicationId from arguments
        applicationId = getArguments() != null ? getArguments().getString("applicationId") : null;
        
        setupObservers();
        setupClickListeners();
        
        // Load application data
        if (applicationId != null) {
            viewModel.getApplication(applicationId);
        } else {
            showError("Application ID is missing");
        }
    }
    
    private void setupObservers() {
        // Observe loading state
        viewModel.isLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            contentContainer.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        });
        
        // Observe error messages
        viewModel.getError().observe(getViewLifecycleOwner(), this::showError);
        
        // Observe application data
        viewModel.getApplication(applicationId).observe(getViewLifecycleOwner(), this::updateApplicationUI);
        
        // Observe job data
        viewModel.getJobData().observe(getViewLifecycleOwner(), this::updateJobUI);
        
        // Observe candidate data
        viewModel.getCandidateData().observe(getViewLifecycleOwner(), this::updateCandidateUI);
        
        // Observe action success
        viewModel.getActionSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                showConfirmationMessage("Action completed successfully");
            }
        });
    }
    
    private void setupClickListeners() {
        messageCandidateButton.setOnClickListener(v -> {
            Application application = viewModel.getApplication(applicationId).getValue();
            if (application != null) {
                String employerId = application.getEmployerId();
                String candidateId = application.getCandidateId();
                
                viewModel.findOrCreateChat(employerId, candidateId).observe(getViewLifecycleOwner(), chat -> {
                    if (chat != null) {
                        Intent intent = new Intent(requireContext(), EmployerChatActivity.class);
                        intent.putExtra("chatId", chat.getId());
                        startActivity(intent);
                    }
                });
            } else {
                showError("Cannot start chat, application data is missing");
            }
        });

        interviewButton.setOnClickListener(v -> {
            showConfirmationDialog(
                "Schedule Interview", 
                "Are you sure you want to schedule an interview with this candidate?",
                () -> viewModel.scheduleInterview(applicationId)
            );
        });
        
        rejectButton.setOnClickListener(v -> {
            showConfirmationDialog(
                "Reject Application", 
                "Are you sure you want to reject this application?",
                () -> viewModel.rejectApplication(applicationId)
            );
        });
    }
    
    private void updateApplicationUI(Application application) {
        if (application == null) return;
        
        statusChip.setText(application.getStatus().toString());
        updateStatusChipStyle(application.getStatus());
        applicationDateText.setText(String.format("Applied: %s", formatDate(application.getAppliedAt())));
        coverLetter.setText(application.getCoverLetter() != null && !application.getCoverLetter().isEmpty() 
            ? application.getCoverLetter() 
            : "No cover letter provided");
        
        // Update button states based on application status
        boolean isActionable = application.getStatus() == ApplicationStatus.SUBMITTED;
        interviewButton.setEnabled(isActionable);
        rejectButton.setEnabled(isActionable);
    }
    
    private void updateJobUI(Job job) {
        if (job == null) return;
        
        jobTitleText.setText(job.getTitle());
        companyNameText.setText(job.getCompanyName());
        jobDescriptionText.setText(job.getDescription());
            
        // Clear previous requirements
        requirementsContainer.removeAllViews();
            
        // Add requirements
        if (job.getRequirements() != null && !job.getRequirements().isEmpty()) {
            for (String requirement : job.getRequirements()) {
                TextView requirementText = new TextView(getContext());
                requirementText.setText(String.format("• %s", requirement));
                requirementsContainer.addView(requirementText);
            }
        } else {
            TextView noRequirementsText = new TextView(getContext());
            noRequirementsText.setText("No specific requirements listed");
            requirementsContainer.addView(noRequirementsText);
        }
    }
    
    private void updateCandidateUI(Graduate graduates) {
        if (graduates == null) return;
        
        candidateNameText.setText(graduates.getFullName());
        candidateEmailText.setText(graduates.getEmail());
    }
    
    private void updateStatusChipStyle(ApplicationStatus status) {
        // Set chip color based on status
        int backgroundColor;
        int textColor;
        
        switch (status) {
            case SUBMITTED:
                backgroundColor = getResources().getColor(R.color.colorPending, null);
                textColor = getResources().getColor(R.color.colorOnPending, null);
                break;
            case INTERVIEW:
                backgroundColor = getResources().getColor(R.color.colorInterview, null);
                textColor = getResources().getColor(R.color.colorOnInterview, null);
                break;
            case ACCEPTED:
                backgroundColor = getResources().getColor(R.color.colorAccepted, null);
                textColor = getResources().getColor(R.color.colorOnAccepted, null);
                break;
            case REJECTED:
                backgroundColor = getResources().getColor(R.color.colorRejected, null);
                textColor = getResources().getColor(R.color.colorOnRejected, null);
                break;
            default:
                backgroundColor = getResources().getColor(R.color.colorPending, null);
                textColor = getResources().getColor(R.color.colorOnPending, null);
                break;
        }
        
        statusChip.setBackgroundColor(backgroundColor);
        statusChip.setTextColor(textColor);
    }

    private String formatDate(Date date) {
        if (date == null) return "N/A";
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        return sdf.format(date);
    }

    private void showConfirmationMessage(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
    
    private void showError(String errorMessage) {
        if (getContext() != null && errorMessage != null) {
            Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
        }
    }
    
    private void showConfirmationDialog(String title, String message, Runnable onConfirm) {
        new MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Confirm", (dialog, which) -> {
                onConfirm.run();
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
}
package com.pbde.gradconnect.screens.employer.jobs;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pbde.gradconnect.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.button.MaterialButton;
import com.pbde.gradconnect.data.models.Job;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.Locale;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

public class EmployerSingleJobFragment extends Fragment {
    private String applicationId;
    private String jobId;
    private EmployerSingleJobViewModel viewModel;
    
    private ProgressBar loadingProgress;
    private TextView errorText;
    private MaterialCardView contentContainer;
    
    private TextView jobTitle, companyName, createdDate, jobDescription, salary;
    private Chip jobType, workMode, level;
    private MaterialButton editJobButton, deleteJobButton;
    private LinearLayout requirementsList, responsibilitiesList, benefitsList;
    private ChipGroup chipGroup;

    public static EmployerSingleJobFragment newInstance() {
        return new EmployerSingleJobFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(EmployerSingleJobViewModel.class);

        // Get jobId from arguments
        jobId = getArguments() != null ? getArguments().getString("jobId") : null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_employer_single_job, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize views
        loadingProgress = view.findViewById(R.id.loading_progress);
        errorText = view.findViewById(R.id.error_text);
        contentContainer = view.findViewById(R.id.card_view);
        
        jobTitle = view.findViewById(R.id.job_title);
        companyName = view.findViewById(R.id.company_name);
        createdDate = view.findViewById(R.id.created_date);
        jobDescription = view.findViewById(R.id.job_description);
        requirementsList = view.findViewById(R.id.requirementsList);
        responsibilitiesList = view.findViewById(R.id.responsibilitiesList);
        benefitsList = view.findViewById(R.id.benefitsList);
        jobType = view.findViewById(R.id.job_type);
        workMode = view.findViewById(R.id.work_mode);
        level = view.findViewById(R.id.job_level);
        salary = view.findViewById(R.id.job_salary);

        editJobButton = view.findViewById(R.id.edit_job_button);
        deleteJobButton = view.findViewById(R.id.delete_job_button);

        chipGroup = jobType.getParent() instanceof ChipGroup ?
            (ChipGroup) jobType.getParent() : null;

        setupClickListeners();
        setupObservers();
        loadData();
    }

    private void setupObservers() {
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), this::showLoading);
        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) showError(error);
        });
        viewModel.getJob().observe(getViewLifecycleOwner(), this::updateUI);
    }

    private void setupClickListeners() {
        deleteJobButton.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete Job")
                .setMessage("Are you sure you want to delete this job?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (dialog, which) -> {
                    if (jobId != null) {
                        viewModel.deleteJob(jobId);
                    }
                })
                .show();
        });

        editJobButton.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("jobId", jobId);
            Navigation.findNavController(requireView())
                .navigate(R.id.action_navigation_employer_single_job_to_navigation_employer_create_edit_job, bundle);
        });
    }

    private void loadData() {
        if (jobId != null) {
            viewModel.loadJob(jobId);
        } else {
            showError("Job ID not found");
        }
    }

    private void updateUI(Job job) {
        if (job == null) {
            showError("Job not found");
            return;
        }

        jobTitle.setText(job.getTitle());
        companyName.setText(job.getCompanyName());
        createdDate.setText("Created: " + formatDate(job.getCreatedAt()));
        jobDescription.setText(job.getDescription());
        salary.setText(job.getSalary());

        // Update chips
        jobType.setText(job.getType().getValue());
        workMode.setText(job.getWorkMode().getValue());
        level.setText(job.getLevel());

        // Setup lists
        setupList(requirementsList, job.getRequirements());
        setupList(benefitsList, job.getBenefits());
        setupList(responsibilitiesList, job.getResponsibilities());
        showContent();
    }

    private void showContent() {
        loadingProgress.setVisibility(View.GONE);
        errorText.setVisibility(View.GONE);
        contentContainer.setVisibility(View.VISIBLE);
    }
    
    private void showLoading(boolean show) {
        loadingProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        contentContainer.setVisibility(show ? View.GONE : View.VISIBLE);
        errorText.setVisibility(View.GONE);
    }
    
    private void showError(String message) {
        loadingProgress.setVisibility(View.GONE);
        contentContainer.setVisibility(View.GONE);
        errorText.setVisibility(View.VISIBLE);
        errorText.setText(message);
    }

    private void setupList(LinearLayout container, java.util.List<String> items) {
        container.removeAllViews(); // Clear existing views
        for (String item : items) {
            TextView textView = new TextView(requireContext());
            textView.setText(String.format("• %s", item));
            textView.setPadding(0, 4, 0, 4);
            container.addView(textView);
        }
    }

    private String formatDate(java.util.Date date) {
        if (date == null) return "";
        return new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(date);
    }
}
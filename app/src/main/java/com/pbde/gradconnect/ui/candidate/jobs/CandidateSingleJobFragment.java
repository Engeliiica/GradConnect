package com.pbde.gradconnect.ui.candidate.jobs;

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
import android.widget.LinearLayout;
import androidx.navigation.Navigation;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.imageview.ShapeableImageView;
import com.pbde.gradconnect.R;
import com.pbde.gradconnect.data.models.Job;
import com.pbde.gradconnect.data.models.Application;
import java.text.SimpleDateFormat;
import java.util.Locale;
import android.graphics.Color;
import android.text.TextUtils;

public class CandidateSingleJobFragment extends Fragment {
    private CandidateSingleJobViewModel viewModel;
    private String jobId;
    private View loadingContainer;
    private View contentContainer;
    private TextView errorText;
    private TextView tvJobTitle;
    private TextView tvCompanyName;
    private TextView tvLocation;
    private TextView tvJobType;
    private TextView tvPostedDate;
    private TextView tvSalary;
    private TextView tvDescription;
    private ChipGroup chipGroup;
    private LinearLayout requirementsList;
    private LinearLayout responsibilitiesList;
    private LinearLayout benefitsList;
    private Button btnApply;
    private Button btnViewApplication;

    public static CandidateSingleJobFragment newInstance() {
        return new CandidateSingleJobFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_candidate_single_job, container, false);
        initializeViews(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(CandidateSingleJobViewModel.class);
        
        // Get jobId from arguments
        jobId = getArguments() != null ? getArguments().getString("jobId") : null;
        
        setupObservers();
        loadData();
    }

    private void initializeViews(View view) {
        loadingContainer = view.findViewById(R.id.loadingContainer);
        contentContainer = view.findViewById(R.id.contentContainer);
        errorText = view.findViewById(R.id.errorText);
        tvJobTitle = view.findViewById(R.id.tvJobTitle);
        tvCompanyName = view.findViewById(R.id.tvCompanyName);
        tvLocation = view.findViewById(R.id.tvLocation);
        tvJobType = view.findViewById(R.id.tvJobType);
        tvPostedDate = view.findViewById(R.id.tvPostedDate);
        tvSalary = view.findViewById(R.id.tvSalary);
        tvDescription = view.findViewById(R.id.tvDescription);
        chipGroup = view.findViewById(R.id.chipGroup);
        requirementsList = view.findViewById(R.id.requirementsList);
        responsibilitiesList = view.findViewById(R.id.responsibilitiesList);
        benefitsList = view.findViewById(R.id.benefitsList);
        btnApply = view.findViewById(R.id.btnApply);
        btnViewApplication = view.findViewById(R.id.btnViewApplication);

        setupButtonListeners();
    }

    private void setupButtonListeners() {
        btnApply.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("jobId", jobId);
            Navigation.findNavController(v)
                    .navigate(R.id.action_navigation_candidate_single_job_to_navigation_candidate_job_application, bundle);
        });

        btnViewApplication.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("jobId", jobId);
            Navigation.findNavController(v)
                    .navigate(R.id.action_navigation_candidate_single_job_to_navigation_candidate_job_application, bundle);
        });
    }

    private void setupObservers() {
        viewModel.getJob().observe(getViewLifecycleOwner(), this::updateJobUI);
        viewModel.getApplication().observe(getViewLifecycleOwner(), this::updateApplicationUI);
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), this::updateLoadingState);
        viewModel.getError().observe(getViewLifecycleOwner(), this::showError);
    }

    private void loadData() {
        if (jobId != null) {
            viewModel.loadJob(jobId);
        } else {
            showError("Job ID not found");
        }
    }

    private void updateJobUI(Job job) {
        if (job == null) {
            showError("Job not found");
            return;
        }

        tvJobTitle.setText(job.getTitle());
        setupCompanyAvatar(job.getCompanyName());
        tvCompanyName.setText(job.getCompanyName());
        tvLocation.setText(job.getLocation());
        tvJobType.setText(job.getType().getValue());
        tvPostedDate.setText(String.format("Posted %s", formatDate(job.getCreatedAt())));
        tvSalary.setText(job.getSalary());
        tvDescription.setText(job.getDescription());

        // Setup chips
        chipGroup.removeAllViews();
        addChip(job.getType().getValue());
        addChip(job.getLevel());

        // Setup lists
        setupList(requirementsList, job.getRequirements());
        setupList(responsibilitiesList, job.getResponsibilities());
        setupList(benefitsList, job.getBenefits());
    }

    private void setupCompanyAvatar(String companyName) {
        if (!TextUtils.isEmpty(companyName)) {
            TextView avatarText = new TextView(requireContext());
            avatarText.setText(String.valueOf(companyName.charAt(0)));
            avatarText.setTextColor(Color.WHITE);
            avatarText.setTextSize(20);
            avatarText.setGravity(android.view.Gravity.CENTER);
        }
    }

    private void updateApplicationUI(Application application) {
        if (application != null) {
            btnApply.setVisibility(View.GONE);
            btnViewApplication.setVisibility(View.VISIBLE);
        } else {
            btnApply.setVisibility(View.VISIBLE);
            btnViewApplication.setVisibility(View.GONE);
        }
    }

    private void updateLoadingState(boolean isLoading) {
        loadingContainer.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        contentContainer.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        errorText.setVisibility(View.GONE);
    }

    private void showError(String error) {
        if (error != null && !error.isEmpty()) {
            loadingContainer.setVisibility(View.GONE);
            contentContainer.setVisibility(View.GONE);
            errorText.setVisibility(View.VISIBLE);
            errorText.setText(error);
        }
    }

    private void addChip(String text) {
        Chip chip = new Chip(requireContext());
        chip.setText(text);
        chip.setClickable(false);
        chipGroup.addView(chip);
    }

    private void setupList(LinearLayout container, java.util.List<String> items) {
        container.removeAllViews();
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
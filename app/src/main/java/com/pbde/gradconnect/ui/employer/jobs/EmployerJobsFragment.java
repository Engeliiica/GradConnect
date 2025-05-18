package com.pbde.gradconnect.ui.employer.jobs;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.pbde.gradconnect.R;
import com.pbde.gradconnect.data.models.Job;
import com.pbde.gradconnect.ui.candidate.jobs.JobListingAdapter;

import java.util.List;

public class EmployerJobsFragment extends Fragment implements JobListingAdapter.OnJobClickListener {
    private EmployerJobsViewModel viewModel;
    private JobListingAdapter adapter;
    private RecyclerView recyclerView;
    private ProgressBar loadingIndicator;
    private AutoCompleteTextView workModeDropdown;
    private AutoCompleteTextView jobTypeDropdown;
    private Button btnApplyFilters;
    private Button btnClearFilters;
    private Button btnCreateJob;
    private TextView emptyStateText;
    private TextView errorStateText;

    public EmployerJobsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(EmployerJobsViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_employer_jobs, container, false);
        
        initializeViews(view);
        setupDropdowns();
        setupClickListeners();
        observeViewModel();

        return view;
    }

    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.rvEmployerJobs);
        loadingIndicator = view.findViewById(R.id.loadingIndicator);
        workModeDropdown = view.findViewById(R.id.workModeDropdown);
        jobTypeDropdown = view.findViewById(R.id.jobTypeDropdown);
        btnApplyFilters = view.findViewById(R.id.btnApplyFilters);
        btnClearFilters = view.findViewById(R.id.btnClearFilters);
        btnCreateJob = view.findViewById(R.id.btnCreateJob);
        emptyStateText = view.findViewById(R.id.emptyStateText);
        errorStateText = view.findViewById(R.id.errorStateText);
        
        adapter = new JobListingAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    private void setupDropdowns() {
        String[] workModes = {"All", "Remote", "On-site", "Hybrid"};
        String[] jobTypes = {"All", "Full-time", "Part-time", "Contract"};

        ArrayAdapter<String> workModeAdapter = new ArrayAdapter<>(requireContext(), 
            android.R.layout.simple_dropdown_item_1line, workModes);
        ArrayAdapter<String> jobTypeAdapter = new ArrayAdapter<>(requireContext(), 
            android.R.layout.simple_dropdown_item_1line, jobTypes);

        workModeDropdown.setAdapter(workModeAdapter);
        jobTypeDropdown.setAdapter(jobTypeAdapter);
    }

    private void setupClickListeners() {
        btnApplyFilters.setOnClickListener(v -> applyFilters());
        btnClearFilters.setOnClickListener(v -> clearFilters());
        btnCreateJob.setOnClickListener(v -> navigateToCreateJob());
    }

    private void observeViewModel() {
        viewModel.getEmployerData().observe(getViewLifecycleOwner(), employer -> {
            if (employer != null && employer.getId() != null) {
                viewModel.getEmployerJobs(employer.getId());
                observeJobs();
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null) {
                showError(errorMessage);
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), this::updateLoadingState);
    }

    private void observeJobs() {
        LiveData<List<Job>> jobsLiveData = viewModel.getAllEmployerJobs();
        if (jobsLiveData != null) {
            jobsLiveData.observe(getViewLifecycleOwner(), jobs -> {
                if (jobs != null) {
                    adapter.setJobs(jobs);
                    updateUIState(jobs.size());
                } else {
                    updateUIState(0);
                }
            });
        }
    }

    private void updateLoadingState(boolean isLoading) {
        loadingIndicator.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        emptyStateText.setVisibility(View.GONE);
        errorStateText.setVisibility(View.GONE);
    }

    private void updateUIState(int jobCount) {
        recyclerView.setVisibility(jobCount > 0 ? View.VISIBLE : View.GONE);
        emptyStateText.setVisibility(jobCount == 0 ? View.VISIBLE : View.GONE);
        errorStateText.setVisibility(View.GONE);
        loadingIndicator.setVisibility(View.GONE);
    }

    private void showError(String message) {
        errorStateText.setText(message);
        recyclerView.setVisibility(View.GONE);
        emptyStateText.setVisibility(View.GONE);
        errorStateText.setVisibility(View.VISIBLE);
        loadingIndicator.setVisibility(View.GONE);
        btnCreateJob.setEnabled(true);
    }

    private void applyFilters() {
        String workMode = workModeDropdown.getText().toString();
        String jobType = jobTypeDropdown.getText().toString();
        
        if (workMode.equals("All")) workMode = "";
        if (jobType.equals("All")) jobType = "";
        
        viewModel.getFilteredEmployerJobs(workMode, jobType).observe(getViewLifecycleOwner(),
            jobs -> adapter.setJobs(jobs));
    }

    private void clearFilters() {
        workModeDropdown.setText("All", false);
        jobTypeDropdown.setText("All", false);
        // Use the cached jobs instead of making a new request
        viewModel.getAllEmployerJobs().observe(getViewLifecycleOwner(), jobs -> {
            adapter.setJobs(jobs);
            updateUIState(jobs.size());
        });
    }

    private void navigateToCreateJob() {
        Navigation.findNavController(requireView())
                .navigate(R.id.action_navigation_employer_jobs_to_navigation_employer_create_edit_job);
    }

    @Override
    public void onJobClick(Job job) {
        Bundle bundle = new Bundle();
        bundle.putString("jobId", job.getId());
        Navigation.findNavController(requireView())
                .navigate(R.id.action_navigation_employer_jobs_to_navigation_employer_single_job, bundle);
    }
}
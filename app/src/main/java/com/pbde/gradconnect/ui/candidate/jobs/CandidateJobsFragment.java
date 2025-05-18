package com.pbde.gradconnect.ui.candidate.jobs;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.pbde.gradconnect.R;
import com.pbde.gradconnect.data.models.Job;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.List;

public class CandidateJobsFragment extends Fragment implements JobListingAdapter.OnJobClickListener {
    private CandidateJobsViewModel viewModel;
    private JobListingAdapter adapter;
    private RecyclerView recyclerView;
    private AutoCompleteTextView workModeDropdown;
    private AutoCompleteTextView jobTypeDropdown;
    private MaterialButton btnApplyFilters;
    private MaterialButton btnClearFilters;
    private MaterialButton btnToggleFilters;
    private MaterialCardView filtersCard;
    private boolean filtersVisible = false;
    private ProgressBar loadingIndicator;
    private TextView emptyStateText;
    private TextView errorStateText;

    public CandidateJobsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(CandidateJobsViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_candidate_jobs, container, false);
        
        // Initialize views
        recyclerView = view.findViewById(R.id.rvJobs);
        workModeDropdown = view.findViewById(R.id.workModeDropdown);
        jobTypeDropdown = view.findViewById(R.id.jobTypeDropdown);
        btnApplyFilters = view.findViewById(R.id.btnApplyFilters);
        btnClearFilters = view.findViewById(R.id.btnClearFilters);
        btnToggleFilters = view.findViewById(R.id.btnToggleFilters);
        filtersCard = view.findViewById(R.id.filtersCard);
        loadingIndicator = view.findViewById(R.id.loadingIndicator);
        emptyStateText = view.findViewById(R.id.emptyStateText);
        errorStateText = view.findViewById(R.id.errorStateText);

        // Setup RecyclerView
        adapter = new JobListingAdapter(this);
        recyclerView.setAdapter(adapter);

        // Setup dropdowns with improved focus handling
        setupDropdowns();

        // Setup button listeners
        btnApplyFilters.setOnClickListener(v -> applyFilters());
        btnClearFilters.setOnClickListener(v -> clearFilters());
        btnToggleFilters.setOnClickListener(v -> toggleFilters());

        // Observe jobs data
        viewModel.getActiveJobs().observe(getViewLifecycleOwner(), this::updateJobsUI);

        // Observe loading state
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), this::updateLoadingState);
        
        // Observe error state
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), this::handleError);
        
        // Restore any previously selected filters
        viewModel.getCurrentFilters().observe(getViewLifecycleOwner(), this::restoreFilterState);

        return view;
    }
    
    private void setupDropdowns() {
        // Setup Work Mode dropdown
        ArrayAdapter<CharSequence> workModeAdapter = ArrayAdapter.createFromResource(
            getContext(), R.array.work_mode_options, R.layout.dropdown_menu_popup_item);
        workModeDropdown.setAdapter(workModeAdapter);
        workModeDropdown.setText("All", false);
        
        // Setup Job Type dropdown
        ArrayAdapter<CharSequence> jobTypeAdapter = ArrayAdapter.createFromResource(
            getContext(), R.array.job_type_options, R.layout.dropdown_menu_popup_item);
        jobTypeDropdown.setAdapter(jobTypeAdapter);
        jobTypeDropdown.setText("All", false);
        
        // Add focus listeners to handle keyboard visibility
        View.OnFocusChangeListener focusListener = (v, hasFocus) -> {
            if (!hasFocus) {
                hideKeyboard(v);
            }
        };
        
        workModeDropdown.setOnFocusChangeListener(focusListener);
        jobTypeDropdown.setOnFocusChangeListener(focusListener);
        
        // Set onItemClickListener to dismiss dropdown after selection
        workModeDropdown.setOnItemClickListener((parent, view, position, id) -> {
            workModeDropdown.clearFocus();
        });
        
        jobTypeDropdown.setOnItemClickListener((parent, view, position, id) -> {
            jobTypeDropdown.clearFocus();
        });
    }
    
    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) requireActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void updateJobsUI(List<Job> jobs) {
        adapter.setJobs(jobs);
        
        boolean isEmpty = jobs == null || jobs.isEmpty();
        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        emptyStateText.setVisibility(isEmpty && errorStateText.getVisibility() != View.VISIBLE ? 
                                     View.VISIBLE : View.GONE);
    }
    
    private void updateLoadingState(boolean isLoading) {
        loadingIndicator.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        if (isLoading) {
            recyclerView.setVisibility(View.GONE);
            emptyStateText.setVisibility(View.GONE);
            errorStateText.setVisibility(View.GONE);
        }
    }
    
    private void handleError(String errorMessage) {
        if (errorMessage != null && !errorMessage.isEmpty()) {
            errorStateText.setText(errorMessage);
            errorStateText.setVisibility(View.VISIBLE);
            emptyStateText.setVisibility(View.GONE);
            Snackbar.make(requireView(), errorMessage, Snackbar.LENGTH_LONG).show();
        } else {
            errorStateText.setVisibility(View.GONE);
        }
    }
    
    private void restoreFilterState(CandidateJobsViewModel.FilterState filterState) {
        if (filterState == null) return;
        
        if (!filterState.getWorkMode().isEmpty()) {
            workModeDropdown.setText(filterState.getWorkMode(), false);
        }
        
        if (!filterState.getJobType().isEmpty()) {
            jobTypeDropdown.setText(filterState.getJobType(), false);
        }
    }

    @Override
    public void onJobClick(Job job) {
        // Navigate to job details
        Bundle bundle = new Bundle();
        bundle.putString("jobId", job.getId());
        Navigation.findNavController(requireView())
                .navigate(R.id.action_navigation_candidate_jobs_to_navigation_candidate_single_job, bundle);
    }

    private void applyFilters() {
        // Hide keyboard when applying filters
        if (workModeDropdown.hasFocus()) {
            workModeDropdown.clearFocus();
        }
        if (jobTypeDropdown.hasFocus()) {
            jobTypeDropdown.clearFocus();
        }
        
        String selectedWorkMode = workModeDropdown.getText().toString();
        String selectedJobType = jobTypeDropdown.getText().toString();

        // Convert "All" to empty string to match backend implementation
        selectedWorkMode = "All".equals(selectedWorkMode) ? "" : selectedWorkMode;
        selectedJobType = "All".equals(selectedJobType) ? "" : selectedJobType;

        viewModel.getFilteredJobs(selectedWorkMode, selectedJobType)
                .observe(getViewLifecycleOwner(), this::updateJobsUI);
        
        // Hide filters after applying
        if (filtersVisible) {
            toggleFilters();
        }
    }

    private void clearFilters() {
        workModeDropdown.setText("All", false);
        jobTypeDropdown.setText("All", false);
        viewModel.resetFilters();
        viewModel.getActiveJobs().observe(getViewLifecycleOwner(), this::updateJobsUI);
        
        // Hide filters after clearing
        if (filtersVisible) {
            toggleFilters();
        }
    }
    
    private void toggleFilters() {
        filtersVisible = !filtersVisible;
        
        if (filtersVisible) {
            filtersCard.setVisibility(View.VISIBLE);
            btnToggleFilters.setText("Hide Filters");
        } else {
            filtersCard.setVisibility(View.GONE);
            btnToggleFilters.setText("Show Filters");
        }
    }
}
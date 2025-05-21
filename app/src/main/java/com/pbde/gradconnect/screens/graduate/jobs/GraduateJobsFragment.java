package com.pbde.gradconnect.screens.graduate.jobs;

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

import java.util.List;

public class GraduateJobsFragment extends Fragment implements JobListingAdapter.OnJobClickListener {
    private GraduateJobsViewModel viewModel;
    private JobListingAdapter adapter;
    private RecyclerView recyclerView;
    private MaterialCardView filtersCard;
    private boolean filtersVisible = false;
    private ProgressBar loadingIndicator;
    private TextView emptyStateText;
    private TextView errorStateText;

    public GraduateJobsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(GraduateJobsViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_graduate_jobs, container, false);
        
        // Initialize views
        recyclerView = view.findViewById(R.id.rvJobs);
        loadingIndicator = view.findViewById(R.id.loadingIndicator);
        emptyStateText = view.findViewById(R.id.emptyStateText);
        errorStateText = view.findViewById(R.id.errorStateText);

        // Setup RecyclerView
        adapter = new JobListingAdapter(this);
        recyclerView.setAdapter(adapter);

        // Observe jobs data
        viewModel.getActiveJobs().observe(getViewLifecycleOwner(), this::updateJobsUI);

        // Observe loading state
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), this::updateLoadingState);
        
        // Observe error state
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), this::handleError);

        return view;
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

    @Override
    public void onJobClick(Job job) {
        // Navigate to job details
        Bundle bundle = new Bundle();
        bundle.putString("jobId", job.getId());
        Navigation.findNavController(requireView())
                .navigate(R.id.action_navigation_graduate_jobs_to_navigation_graduate_single_job, bundle);
    }
}
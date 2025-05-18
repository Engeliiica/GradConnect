package com.pbde.gradconnect.ui.candidate.applications;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.pbde.gradconnect.R;
import com.pbde.gradconnect.data.models.Application;

import java.util.List;


public class CandidateApplicationsFragment extends Fragment {
    private CandidateApplicationsViewModel viewModel;
    private ApplicationAdapter adapter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvError;
    private TextView tvEmptyState;
    private SwipeRefreshLayout swipeRefresh;

    public CandidateApplicationsFragment() {
        // Required empty public constructor
    }

    public static CandidateApplicationsFragment newInstance() {
        return new CandidateApplicationsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(CandidateApplicationsViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_candidate_applications, container, false);
        
        // Initialize views
        recyclerView = view.findViewById(R.id.rvJobApplications);
        progressBar = view.findViewById(R.id.progressBar);
        tvError = view.findViewById(R.id.tvError);
        tvEmptyState = view.findViewById(R.id.tvEmptyState);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        
        // Setup adapter
        adapter = new ApplicationAdapter(this);
        recyclerView.setAdapter(adapter);
        
        // Setup pull to refresh
        swipeRefresh.setOnRefreshListener(() -> {
            viewModel.refresh();
        });

        setupObservers();
        
        return view;
    }
    
    private void setupObservers() {
        // Observe loading state
        viewModel.isLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            swipeRefresh.setRefreshing(isLoading);
        });
        
        // Observe error state
        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                tvError.setText(error);
                tvError.setVisibility(View.VISIBLE);
                Snackbar.make(requireView(), error, Snackbar.LENGTH_LONG).show();
            } else {
                tvError.setVisibility(View.GONE);
            }
        });

        // Observe applications data
        viewModel.getApplications().observe(getViewLifecycleOwner(), applications -> {
            adapter.setApplications(applications);
            updateEmptyState(applications);
        });

        viewModel.getApplicationsJobs().observe(getViewLifecycleOwner(), applicationsJobs -> {
            adapter.setApplicationsJobs(applicationsJobs);
        });
    }
    
    private void updateEmptyState(List<Application> applications) {
        if (applications == null || applications.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tvEmptyState.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvEmptyState.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Consider refreshing data when fragment resumes
        viewModel.refresh();
    }
}
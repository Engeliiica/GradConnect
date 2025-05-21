package com.pbde.gradconnect.screens.employer.applications;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.pbde.gradconnect.R;


public class EmployerApplicationsFragment extends Fragment {
    private EmployerApplicationsViewModel viewModel;
    private JobApplicationAdapter adapter;
    private RecyclerView recyclerView;
    private String employerId;

    public EmployerApplicationsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(EmployerApplicationsViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_employer_applications, container, false);
        
        recyclerView = view.findViewById(R.id.rvEmployerApplications);
        adapter = new JobApplicationAdapter(this);
        recyclerView.setAdapter(adapter);

        // Observe employer data first
        viewModel.getEmployerData().observe(getViewLifecycleOwner(), employer -> {
            if (employer != null) {
                employerId = employer.getId();
                viewModel.loadApplicationsJobsAndCandidates(employerId);
            }
        });
        
        // Load applications and observe changes
        viewModel.getApplications().observe(getViewLifecycleOwner(), applications -> {
            adapter.setApplications(applications);
        });

        viewModel.getApplicationsJobs().observe(getViewLifecycleOwner(), applicationsJobs -> {
            adapter.setApplicationsJobs(applicationsJobs);
        });

        viewModel.getApplicationsCandidates().observe(getViewLifecycleOwner(), candidates -> {
            adapter.setApplicationsCandidates(candidates);
        });

        return view;
    }
}
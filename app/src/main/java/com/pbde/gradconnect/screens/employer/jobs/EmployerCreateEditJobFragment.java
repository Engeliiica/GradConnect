package com.pbde.gradconnect.screens.employer.jobs;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.pbde.gradconnect.R;

import java.util.ArrayList;
import java.util.List;

public class EmployerCreateEditJobFragment extends Fragment {

    private EmployerCreateEditJobViewModel viewModel;

    public static EmployerCreateEditJobFragment newInstance() {
        return new EmployerCreateEditJobFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_employer_create_edit_job, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(EmployerCreateEditJobViewModel.class);

        // Get job ID from arguments if editing
        String jobId = getArguments() != null ? getArguments().getString("jobId") : null;
        if (jobId != null) {
            viewModel.loadJob(jobId);
        }

        // Setup input fields
        TextInputEditText titleInput = view.findViewById(R.id.titleInput);
        TextInputEditText locationInput = view.findViewById(R.id.locationInput);
        TextInputEditText descriptionInput = view.findViewById(R.id.descriptionInput);
        TextInputEditText levelInput = view.findViewById(R.id.levelInput);
        TextInputEditText requirementsInput = view.findViewById(R.id.requirementsInput);
        TextInputEditText responsibilitiesInput = view.findViewById(R.id.responsibilitiesInput);
        TextInputEditText benefitsInput = view.findViewById(R.id.benefitsInput);
        TextInputEditText salaryInput = view.findViewById(R.id.salaryInput);

        // Setup job type dropdown
        AutoCompleteTextView jobTypeInput = view.findViewById(R.id.jobTypeInput);
        String[] jobTypes = new String[]{"Full-time", "Part-time", "Contract"};
        ArrayAdapter<String> jobTypeAdapter = new ArrayAdapter<>(
                requireContext(), 
                android.R.layout.simple_dropdown_item_1line, 
                jobTypes
        );
        jobTypeInput.setAdapter(jobTypeAdapter);
        jobTypeInput.setThreshold(1); // Show dropdown after typing 1 character
        jobTypeInput.setOnClickListener(v -> jobTypeInput.showDropDown()); // Show dropdown on click

        // Setup work mode dropdown
        AutoCompleteTextView workModeInput = view.findViewById(R.id.workModeInput);
        String[] workModes = new String[]{"Remote", "On-site", "Hybrid"};
        ArrayAdapter<String> workModeAdapter = new ArrayAdapter<>(
                requireContext(), 
                android.R.layout.simple_dropdown_item_1line, 
                workModes
        );
        workModeInput.setAdapter(workModeAdapter);
        workModeInput.setThreshold(1); // Show dropdown after typing 1 character
        workModeInput.setOnClickListener(v -> workModeInput.showDropDown()); // Show dropdown on click

        // Observe job data for editing
        viewModel.getJobData().observe(getViewLifecycleOwner(), job -> {
            if (job != null) {
                titleInput.setText(job.getTitle());
                locationInput.setText(job.getLocation());
                jobTypeInput.setText(job.getType().getValue(), false);
                workModeInput.setText(job.getWorkMode().getValue(), false);
                descriptionInput.setText(job.getDescription());
                levelInput.setText(job.getLevel());
                requirementsInput.setText(String.join("\n", job.getRequirements()));
                responsibilitiesInput.setText(String.join("\n", job.getResponsibilities()));
                benefitsInput.setText(String.join("\n", job.getBenefits()));
                salaryInput.setText(job.getSalary());
            }
        });

        // Setup observers
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                com.google.android.material.snackbar.Snackbar.make(
                    view, message, com.google.android.material.snackbar.Snackbar.LENGTH_LONG
                ).show();
            }
        });

        // Setup buttons
        MaterialButton cancelButton = view.findViewById(R.id.cancelButton);
        MaterialButton saveButton = view.findViewById(R.id.saveButton);

        // Update save button text based on mode
        saveButton.setText(viewModel.isEditMode() ? "Update" : "Create");

        cancelButton.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        saveButton.setOnClickListener(v -> {
            String title = titleInput.getText().toString();
            String location = locationInput.getText().toString();
            String type = jobTypeInput.getText().toString();
            String workMode = workModeInput.getText().toString();
            String description = descriptionInput.getText().toString();
            String level = levelInput.getText().toString();
            String salary = salaryInput.getText().toString();
            
            List<String> requirements = parseLines(requirementsInput.getText().toString());
            List<String> responsibilities = parseLines(responsibilitiesInput.getText().toString());
            List<String> benefits = parseLines(benefitsInput.getText().toString());

            // Show loading state
            saveButton.setEnabled(false);
            saveButton.setText("Saving...");

            viewModel.createJob(title, location, type, level, workMode, description, requirements,
                responsibilities, benefits, salary).observe(getViewLifecycleOwner(),
                    job -> {
                    if (job != null) {
                        requireActivity().onBackPressed();
                    } else {
                        // Reset button state on error
                        saveButton.setEnabled(true);
                        saveButton.setText(viewModel.isEditMode() ? "Update" : "Create");
                    }
                });
        });
    }

    private List<String> parseLines(String text) {
        List<String> result = new ArrayList<>();
        for (String line : text.split("\\n")) {
            String trimmed = line.trim();
            if (!trimmed.isEmpty()) {
                result.add(trimmed);
            }
        }
        return result;
    }

}
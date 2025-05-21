package com.pbde.gradconnect.screens.employer.applications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.pbde.gradconnect.R;
import com.pbde.gradconnect.data.models.Application;
import com.pbde.gradconnect.data.models.Graduate;
import com.pbde.gradconnect.data.models.Job;

import java.text.SimpleDateFormat;
import java.util.*;

public class JobApplicationAdapter extends RecyclerView.Adapter<JobApplicationAdapter.ApplicationViewHolder> {
    private List<Application> applications = new ArrayList<>();
    private Map<String, List<Job>> applicationsJobs = new HashMap<>();
    private Map<String, List<Graduate>> applicationsCandidates = new HashMap<>();
    private final EmployerApplicationsFragment fragment;

    public JobApplicationAdapter(EmployerApplicationsFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public ApplicationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.job_application_item, parent, false);
        return new ApplicationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ApplicationViewHolder holder, int position) {
        Application application = applications.get(position);
        holder.bindApplication(application);
    }

    @Override
    public int getItemCount() {
        return applications.size();
    }

    public void setApplications(List<Application> applications) {
        this.applications = applications;
        notifyDataSetChanged();
    }

    public void setApplicationsJobs(Map<String, List<Job>> applicationsJobs) {
        this.applicationsJobs = applicationsJobs;
        notifyDataSetChanged();
    }

    public void setApplicationsCandidates(Map<String, List<Graduate>> candidates) {
        this.applicationsCandidates = candidates;
        notifyDataSetChanged();
    }

    class ApplicationViewHolder extends RecyclerView.ViewHolder {
        private final TextView jobTitleView;
        private final TextView statusView;
        private final TextView dateView;
        private final TextView candidateNameView;

        ApplicationViewHolder(View itemView) {
            super(itemView);
            jobTitleView = itemView.findViewById(R.id.tvJobTitle);
            statusView = itemView.findViewById(R.id.tvStatus);
            candidateNameView = itemView.findViewById(R.id.tvCompanyName);
            dateView = itemView.findViewById(R.id.tvAppliedDate);
        }

        void bindApplication(Application application) {
            Job job = null;
            if (applicationsJobs.containsKey(application.getJobId())) {
                List<Job> jobs = applicationsJobs.get(application.getJobId());
                if (jobs != null && !jobs.isEmpty()) {
                    job = jobs.get(0);
                }
            }

            Graduate graduate = null;
            if (applicationsCandidates.containsKey(application.getJobId())) {
                List<Graduate> graduates = applicationsCandidates.get(application.getJobId());
                if (graduates != null && !graduates.isEmpty()) {
                    graduate = graduates.get(0);
                }
            }

            if (job != null) {
                jobTitleView.setText(job.getTitle());
            } else {
                jobTitleView.setText("Unknown Job");
            }

            if (graduate != null) {
                candidateNameView.setText(graduate.getFullName());
            } else {
                candidateNameView.setText("Unknown Graduate");
            }

            statusView.setText(application.getStatus().getValue());
            dateView.setText(formatDate(application.getAppliedAt()));
            
            itemView.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putString("applicationId", application.getId());
                Navigation.findNavController(v)
                    .navigate(R.id.action_navigation_employer_applications_to_navigation_employer_single_application, bundle);
            });
        }

        private String formatDate(Date date) {
            if (date == null) return "";
            return new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(date);
        }
    }
}
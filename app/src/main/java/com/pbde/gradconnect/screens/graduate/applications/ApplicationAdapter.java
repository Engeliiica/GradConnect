package com.pbde.gradconnect.screens.graduate.applications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.pbde.gradconnect.R;
import com.pbde.gradconnect.data.models.Application;
import com.pbde.gradconnect.data.models.Job;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.ApplicationViewHolder> {
    private List<Application> applications = new ArrayList<>();
    private Map<String, List<Job>> applicationsJobs = new HashMap<>();
    private final GraduateApplicationsFragment fragment;

    public ApplicationAdapter(GraduateApplicationsFragment fragment) {
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
    }

    public void setApplicationsJobs(Map<String, List<Job>> applicationsJobs) {
        this.applicationsJobs = applicationsJobs;
        notifyDataSetChanged();
    }

    class ApplicationViewHolder extends RecyclerView.ViewHolder {
        private final TextView jobTitleView;
        private final TextView statusView;
        private final TextView dateView;
        private final TextView companyNameView;

        ApplicationViewHolder(View itemView) {
            super(itemView);
            jobTitleView = itemView.findViewById(R.id.tvJobTitle);
            statusView = itemView.findViewById(R.id.tvStatus);
            companyNameView = itemView.findViewById(R.id.tvCompanyName);
            dateView = itemView.findViewById(R.id.tvAppliedDate);
        }

        void bindApplication(Application application) {
            Job job = null;
            if (applicationsJobs.containsKey(application.getId())) {
                List<Job> jobs = applicationsJobs.get(application.getId());
                if (jobs != null && !jobs.isEmpty()) {
                    job = jobs.get(0);
                }
            }
            if (job != null) {
                jobTitleView.setText(job.getTitle());
                companyNameView.setText(job.getCompanyName());
            } else {
                jobTitleView.setText("Unknown Job");
                companyNameView.setText("Unknown Company");
            }
            statusView.setText(application.getStatus().getValue());
            dateView.setText(String.format("%s", formatDate(application.getAppliedAt())));
            
            itemView.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putString("applicationId", application.getId());
                Navigation.findNavController(v)
                    .navigate(R.id.action_navigation_graduate_applications_to_navigation_graduate_single_application, bundle);
            });
        }

        private String formatDate(java.util.Date date) {
            if (date == null) return "";
            return new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(date);
        }

    }
}
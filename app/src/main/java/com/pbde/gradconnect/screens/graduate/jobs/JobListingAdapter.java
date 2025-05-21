package com.pbde.gradconnect.screens.graduate.jobs;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.Chip;
import com.pbde.gradconnect.R;
import com.pbde.gradconnect.data.models.Job;
import java.util.ArrayList;
import java.util.List;

public class JobListingAdapter extends RecyclerView.Adapter<JobListingAdapter.JobViewHolder> {
    private List<Job> jobs = new ArrayList<>();
    private final OnJobClickListener listener;

    public interface OnJobClickListener {
        void onJobClick(Job job);
    }

    public JobListingAdapter(OnJobClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.job_listing_item, parent, false);
        return new JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobViewHolder holder, int position) {
        Job job = jobs.get(position);
        holder.bind(job, listener);
    }

    @Override
    public int getItemCount() {
        return jobs.size();
    }

    public void setJobs(List<Job> jobs) {
        this.jobs = jobs != null ? jobs : new ArrayList<>();
        notifyDataSetChanged();
    }

    static class JobViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvJobTitle;
        private final TextView tvCompanyName;
        private final TextView tvLocation;
        private final TextView tvJobDescription;
        private final TextView tvSalaryRange;
        private final Chip cvJobType;
        private final Chip cvWorkMode;
        private final Chip cvLevel;

        public JobViewHolder(@NonNull View itemView) {
            super(itemView);
            tvJobTitle = itemView.findViewById(R.id.tvJobTitle);
            tvCompanyName = itemView.findViewById(R.id.tvCompanyName);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            cvJobType = itemView.findViewById(R.id.cvJobType);
            cvWorkMode = itemView.findViewById(R.id.cvWorkMode);
            cvLevel = itemView.findViewById(R.id.cvLevel);
            tvJobDescription = itemView.findViewById(R.id.tvJobDescription);
            tvSalaryRange = itemView.findViewById(R.id.tvSalaryRange);
        }

        public void bind(Job job, OnJobClickListener listener) {
            tvJobTitle.setText(job.getTitle());
            tvCompanyName.setText(job.getCompanyName());
            tvLocation.setText(job.getLocation());
            cvJobType.setText(job.getType().getValue());
            cvWorkMode.setText(job.getWorkMode().getValue());
            cvLevel.setText(job.getLevel());
            tvJobDescription.setText(job.getDescription());
            tvSalaryRange.setText(job.getSalary());

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onJobClick(job);
                }
            });
        }
    }
}
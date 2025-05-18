package com.pbde.gradconnect.data.models;

import androidx.annotation.NonNull;
import com.pbde.gradconnect.data.models.enums.JobType;
import com.pbde.gradconnect.data.models.enums.WorkMode;
import java.util.List;
import java.util.stream.Collectors;

public class JobFilters {
    private List<JobType> types;
    private List<WorkMode> workModes;

    public JobFilters(List<JobType> types, List<WorkMode> workModes) {
        this.types = types;
        this.workModes = workModes;
    }

    public List<String> getTypes() {
        return types.stream()
                .map(JobType::toString)
                .collect(Collectors.toList());
    }

    public List<String> getWorkModes() {
        return workModes.stream()
                .map(WorkMode::toString)
                .collect(Collectors.toList());
    }

    public void setTypes(List<JobType> types) {
        this.types = types;
    }

    public void setWorkModes(List<WorkMode> workModes) {
        this.workModes = workModes;
    }
}

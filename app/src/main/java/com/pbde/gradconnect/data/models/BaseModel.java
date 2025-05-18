package com.pbde.gradconnect.data.models;

import androidx.annotation.NonNull;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.PropertyName;
import java.util.Date;

public class BaseModel {
    @NonNull
    private String id = "";
    @NonNull
    private Date createdAt = new Date();
    @NonNull
    private Date updatedAt = new Date();

    // Required no-argument constructor for Firebase
    public BaseModel() {
    }

    public BaseModel(@NonNull String id, @NonNull Date createdAt, @NonNull Date updatedAt) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    @NonNull
    @PropertyName("createdAt")
    public Date getCreatedAt() {
        return createdAt;
    }

    @PropertyName("createdAt")
    public void setCreatedAt(@NonNull Date createdAt) {
        this.createdAt = createdAt;
    }

    @NonNull
    @PropertyName("updatedAt")
    public Date getUpdatedAt() {
        return updatedAt;
    }

    @PropertyName("updatedAt")
    public void setUpdatedAt(@NonNull Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Helper methods for Firebase Timestamp conversion
    @PropertyName("createdAt")
    public void setCreatedAtTimestamp(Timestamp timestamp) {
        this.createdAt = timestamp != null ? timestamp.toDate() : new Date();
    }

    @PropertyName("updatedAt")
    public void setUpdatedAtTimestamp(Timestamp timestamp) {
        this.updatedAt = timestamp != null ? timestamp.toDate() : new Date();
    }
}

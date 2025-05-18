package com.pbde.gradconnect.data.repository;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pbde.gradconnect.data.models.EmployerProfile;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class EmployerProfileRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String USERS_COLLECTION = "users";

    private Date parseDate(String dateString) {
        if (dateString == null) return null;
        try {
            SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            iso8601Format.setTimeZone(TimeZone.getTimeZone("UTC"));
            return iso8601Format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Added getEmployerProfile method that retrieves profile details
    public Task<EmployerProfile> getEmployerProfile(String userId) {
        return db.collection(USERS_COLLECTION)
            .document(userId)
            .get()
            .continueWith(task -> {
                if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                    DocumentSnapshot document = task.getResult();
                    Map<String, Object> profileData = (Map<String, Object>) document.get("profile");
                    if (profileData != null) {
                        String companyName = (String) profileData.get("companyName");
                        String companyDescription = (String) profileData.get("companyDescription");
                        String website = (String) profileData.get("website");
                        return new EmployerProfile(companyName != null ? companyName : "", companyDescription, website);
                    }
                }
                // Return an empty profile if not found
                return new EmployerProfile("");
            });
    }

    public Task<Void> saveEmployerProfile(String userId, EmployerProfile profile) {
        return db.collection(USERS_COLLECTION)
            .document(userId)
            .update("profile", profile)
            .continueWith(task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return null;
            });
    }
}

package com.example.purrfectmatch;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ScheduledAppsList extends AppCompatActivity {

    private List<HashMap<String, String>> scheduledAppsList;
    private FirebaseFirestore db;
    private RecyclerView recyclerViewScheduled;
    private ScheduledAppAdapter adapterScheduled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_scheduled_apps_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        scheduledAppsList = new ArrayList<>();
        recyclerViewScheduled = findViewById(R.id.recyclerViewScheduled);

        db = FirebaseFirestore.getInstance();

        adapterScheduled = new ScheduledAppAdapter(scheduledAppsList, ScheduledAppsList.this);
        recyclerViewScheduled.setAdapter(adapterScheduled);

        fetchScheduledApps();

    }

    private void fetchScheduledApps() {
        // Initialize the list to store applications
        scheduledAppsList.clear();

        Log.d("fetching", "Fetching scheduled apps...");

        // Query to fetch applications with the "scheduled" status
        db.collection("Applications")
                .whereEqualTo("status", "scheduled") // Filter for scheduled applications
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        // Get application data
                        String appId = document.getId();
                        String userId = document.getString("userId");
                        String catId = document.getString("catId");
                        String dateSchedule = document.getString("finalDate");
                        String finalTime = document.getString("finalTime");

                        // Create an object or HashMap to store application data
                        HashMap<String, String> appData = new HashMap<>();
                        appData.put("appId", appId);
                        appData.put("catId", catId);
                        appData.put("finalDate", dateSchedule);
                        appData.put("finalTime", finalTime);
                        appData.put("reason", document.getString("reason"));

                        appData.put("appDate", formatFirebaseTimestamp(document.getTimestamp("applicationDate")));
                        // Inner query to fetch user details
                        db.collection("Users")
                                .document(userId) // Access the specific user's document
                                .get()
                                .addOnSuccessListener(userDoc -> {
                                    if (userDoc.exists()) {
                                        // Add user details to app data
                                        appData.put("userSched", userDoc.getString("firstName") + " " + userDoc.get("lastName"));
                                        appData.put("profileImg", userDoc.getString("profileimg"));
                                        appData.put("userId", userDoc.getId());
                                    }

                                    // Add the complete app data to the list
                                    scheduledAppsList.add(appData);

                                    // Notify your adapter or UI about the new data
                                    adapterScheduled.notifyDataSetChanged();
                                    Log.d("scheduled", "App data added: " + appData.toString());

                                })
                                .addOnFailureListener(e -> {
                                    Log.e("scheduled", "Failed to fetch user details: " + e.getMessage());
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("scheduled", "Failed to fetch scheduled applications: " + e.getMessage());
                });
    }


    private String formatFirebaseTimestamp(Timestamp timestamp) {
        if (timestamp == null) {
            return "Invalid Timestamp"; // Handle null values gracefully
        }

        // Convert Timestamp to Date
        Date date = timestamp.toDate();

        // Format the Date to "January 26, 2014"
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
        return sdf.format(date);
    }


}
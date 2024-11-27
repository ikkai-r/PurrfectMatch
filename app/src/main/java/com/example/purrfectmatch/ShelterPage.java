package com.example.purrfectmatch;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class ShelterPage extends AppCompatActivity {

    private FirebaseFirestore db;
    private TextView txtCats, txtScheduled, txtPending, noPendingApplications, noScheduledApplications;
    private int numCats, numScheduledAppointments, numPendingApplications;
    private RecyclerView recyclerViewPending, recyclerViewScheduled;
    private PendingAppHomeAdapter adapterPending;
    private ScheduledAppHomeAdapter adapterScheduled;
    private List<Cat> catsWithPendingApps;
    private List<HashMap<String, String>> scheduledAppsList;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_shelter_page);

        mAuth = FirebaseAuth.getInstance();
        txtCats = findViewById(R.id.totalCats);
        txtScheduled = findViewById(R.id.appointmentTxt);
        txtPending = findViewById(R.id.adoptionAppTxt);
        noPendingApplications = findViewById(R.id.noPendingApplicationsTxt);
        noScheduledApplications = findViewById(R.id.noScheduledApplicationsTxt);

        catsWithPendingApps = new ArrayList<>();
        scheduledAppsList = new ArrayList<>();

        recyclerViewPending = findViewById(R.id.recyclerViewPending);
        adapterPending = new PendingAppHomeAdapter(catsWithPendingApps, ShelterPage.this);
        recyclerViewPending.setLayoutManager(new LinearLayoutManager(ShelterPage.this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewPending.setAdapter(adapterPending);

        recyclerViewScheduled = findViewById(R.id.recyclerViewScheduled);
        adapterScheduled = new ScheduledAppHomeAdapter(scheduledAppsList, ShelterPage.this);
        recyclerViewScheduled.setLayoutManager(new LinearLayoutManager(ShelterPage.this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewScheduled.setAdapter(adapterScheduled);

        db = FirebaseFirestore.getInstance();
        initializeNumbers();
        Log.d("fetching", "fetching pending apps");
        fetchPendingApps();
        Log.d("scheduled", "fetching scheduled apps");
        fetchScheduledApps();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button logOutBtn = findViewById(R.id.logoutBtn);
        logOutBtn.setOnClickListener(view -> {
            signOutUser();
        });

    }

    private void signOutUser() {
        mAuth.signOut(); // Sign out the user
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    private void fetchPendingApps() {
        // Initialize the list to store cats
        catsWithPendingApps.clear();

        Log.d("fetching", "fetching pending apps here");


        // Query to fetch the cats
        db.collection("Cats")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot catSnapshot : task.getResult()) {
                            try {
                                Cat cat = catSnapshot.toObject(Cat.class);  // Convert the document into Cat object
                                Log.d("catP", cat.getName());
                                if (cat != null) {
                                    Log.d("catP", cat.getName() + " exists");
                                    if (cat.getPendingApplications() != null && !cat.getPendingApplications().isEmpty()) {
                                        Log.d("catP", cat.getName() + " has pending application");

                                        catsWithPendingApps.add(cat);

                                    }
                                }
                            } catch (Exception e) {
                                Log.d("catP", "Error parsing cat data: "
                                        + e.getMessage());
                            }
                        }

                        if (catsWithPendingApps.isEmpty()) {
                            noPendingApplications.setVisibility(View.VISIBLE);
                        } else {
                            noPendingApplications.setVisibility(View.GONE);
                        }

                        adapterPending.notifyDataSetChanged();
                    } else {
                        Toast.makeText(ShelterPage.this, "Error fetching cat data.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
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


                                        if (scheduledAppsList.isEmpty()) {
                                            noScheduledApplications.setVisibility(View.VISIBLE);
                                        } else {
                                            noScheduledApplications.setVisibility(View.GONE);
                                        }

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

    public void goPendingApps(View v) {
        Intent i = new Intent(this, PendingApplications.class);
        this.startActivity(i);
    }

    public void goScheduledApp(View v) {
        Intent i = new Intent(this, ScheduledAppsList.class);
        this.startActivity(i);
    }

    public void viewCats(View v) {
        Intent i = new Intent(this, ViewCatsAdoption.class);
        this.startActivity(i);
    }

    public void addCat(View v) {
        Intent i = new Intent(this, AddCat.class);
        this.startActivity(i);
    }

    // Gets total number of cats for adoption, scheduled appointments and pending applications
    private void initializeNumbers() {
        // Fetch number of cats
        db.collection("Cats")
                .whereEqualTo("isAdopted", false)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    numCats = querySnapshot.size();
                    txtCats.setText(String.valueOf(numCats));
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "Error fetching cat count", e);
                    txtCats.setText("0");
                });


        // Fetch number of scheduled appointments
        db.collection("Applications")
                .whereEqualTo("status", "scheduled")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    numScheduledAppointments = querySnapshot.size();
                    txtScheduled.setText(String.valueOf(numScheduledAppointments));
                });

        // Fetch number of pending applications
        db.collection("Applications")
                .whereIn("status", Arrays.asList("pending", "reviewed"))
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    numPendingApplications = querySnapshot.size();
                    txtPending.setText(String.valueOf(numPendingApplications));
                });
    }
}
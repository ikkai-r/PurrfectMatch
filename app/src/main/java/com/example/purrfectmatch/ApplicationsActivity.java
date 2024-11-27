package com.example.purrfectmatch;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ApplicationsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private ImageView profile, explore, swipe;
    private RecyclerView recyclerActiveApplications, recyclerClosedApplications;
    private TextView noActiveApplicationsTxt, noClosedApplicationsTxt, numActiveTxt, numAcceptedTxt, numRejectedTxt;
    private ApplicationAdapter activeAdapter, closedAdapter;

    private List<ApplicationData> activeApplicationsList, closedApplicationsList;
    private int numActive = 0, numAccepted = 0, numRejected = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.applications);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        activeApplicationsList = new ArrayList<>();
        closedApplicationsList = new ArrayList<>();

        initializeViews();
        setupRecyclerViews();
        fetchApplications();

        numActiveTxt.setText(String.valueOf(numActive));
        numAcceptedTxt.setText(String.valueOf(numAccepted));
        numRejectedTxt.setText(String.valueOf(numRejected));
    }

    private void initializeViews() {
        profile = findViewById(R.id.profile);
        explore = findViewById(R.id.explore);
        swipe = findViewById(R.id.swipe);
        noActiveApplicationsTxt = findViewById(R.id.noActiveApplicationsTxt);
        noClosedApplicationsTxt = findViewById(R.id.noClosedApplicationsTxt);
        numActiveTxt = findViewById(R.id.numActive);
        numAcceptedTxt = findViewById(R.id.numAccepted);
        numRejectedTxt = findViewById(R.id.numRejected);

        profile.setOnClickListener(view -> {
            Intent i = new Intent(this, ProfileActivity.class);
            startActivity(i);
            finish();
        });

        swipe.setOnClickListener(view -> {
            Intent i = new Intent(this, SwipeActivity.class);
            startActivity(i);
            finish();
        });

        explore.setOnClickListener(view -> {
            Intent i = new Intent(this, ExploreActivity.class);
            startActivity(i);
            finish();
        });
    }

    private void setupRecyclerViews() {
        activeAdapter = new ApplicationAdapter(true, activeApplicationsList, this);
        recyclerActiveApplications = findViewById(R.id.recyclerActiveApplications);
        recyclerActiveApplications.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerActiveApplications.setAdapter(activeAdapter);

        activeAdapter.setOnItemClickListener(applicationData -> {
            Intent i = new Intent(this, ScheduleUser.class);
            i.putExtra("applicationId", applicationData.getApplicationId());
            startActivity(i);
        });

        // Set up Closed Applications RecyclerView
        closedAdapter = new ApplicationAdapter(false, closedApplicationsList, this);
        recyclerClosedApplications = findViewById(R.id.recyclerClosedApplications);
        recyclerClosedApplications.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerClosedApplications.setAdapter(closedAdapter);
    }



    private void fetchApplications() {
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        DocumentReference userRef = db.collection("Users").document(userId);

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<String> applicationIds = (List<String>) documentSnapshot.get("catApplications");

                if (applicationIds != null && !applicationIds.isEmpty()) {
                    db.collection("Applications")
                            .whereIn(FieldPath.documentId(), applicationIds)
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot value,
                                                    @Nullable FirebaseFirestoreException error) {
                                    if (error != null) {
                                        Toast.makeText(ApplicationsActivity.this, "Error fetching data: "
                                                + error.getMessage(), Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    activeApplicationsList.clear();
                                    closedApplicationsList.clear();

                                    // Reset counters
                                    numActive = 0;
                                    numAccepted = 0;
                                    numRejected = 0;

                                    for (DocumentSnapshot snapshot : value.getDocuments()) {
                                        try {
                                            ApplicationData applicationData = snapshot.toObject(ApplicationData.class);
                                            if (applicationData != null) {
                                                String status = applicationData.getStatus();

                                                if ("rejected".equalsIgnoreCase(status)) {
                                                    numRejected++;
                                                    applicationData.setApplicationId(snapshot.getId());
                                                    closedApplicationsList.add(applicationData);
                                                } else if ("accepted".equalsIgnoreCase(status)) {
                                                    numAccepted++;
                                                    applicationData.setApplicationId(snapshot.getId());
                                                    closedApplicationsList.add(applicationData);
                                                } else {
                                                    numActive++;
                                                    applicationData.setApplicationId(snapshot.getId());
                                                    activeApplicationsList.add(applicationData);
                                                }
                                            }
                                        } catch (Exception e) {
                                            Toast.makeText(ApplicationsActivity.this, "Error parsing data: "
                                                    + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    // Update visibility of text views
                                    noActiveApplicationsTxt.setVisibility(
                                            activeApplicationsList.isEmpty() ? View.VISIBLE : View.GONE);
                                    noClosedApplicationsTxt.setVisibility(
                                            closedApplicationsList.isEmpty() ? View.VISIBLE : View.GONE);

                                    // Update adapters
                                    activeAdapter.notifyDataSetChanged();
                                    closedAdapter.notifyDataSetChanged();

                                    // Update counters in UI
                                    numActiveTxt.setText(String.valueOf(numActive));
                                    numAcceptedTxt.setText(String.valueOf(numAccepted));
                                    numRejectedTxt.setText(String.valueOf(numRejected));

                                    // Log the counters
                                    Log.d("Counters", "Active: " + numActive +
                                            ", Accepted: " + numAccepted +
                                            ", Rejected: " + numRejected);
                                }
                            });
                } else {
                    noActiveApplicationsTxt.setVisibility(View.VISIBLE);
                    noClosedApplicationsTxt.setVisibility(View.VISIBLE);
                    Toast.makeText(ApplicationsActivity.this, "No applications found.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ApplicationsActivity.this, "User document not found.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(ApplicationsActivity.this, "Error fetching user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}
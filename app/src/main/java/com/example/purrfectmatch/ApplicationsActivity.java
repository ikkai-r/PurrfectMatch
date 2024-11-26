package com.example.purrfectmatch;

import android.content.Intent;
import android.os.Bundle;
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
    private TextView noActiveApplicationsTxt;
    private ApplicationAdapter activeAdapter, closedAdapter;

    private List<ApplicationData> activeApplicationsList, closedApplicationsList;

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

        fetchActiveApplications();
    }

    private void initializeViews() {
        profile = findViewById(R.id.profile);
        explore = findViewById(R.id.explore);
        swipe = findViewById(R.id.swipe);
        noActiveApplicationsTxt = findViewById(R.id.noActiveApplicationsTxt);


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
        recyclerActiveApplications = findViewById(R.id.recyclerActiveApplications);
        recyclerActiveApplications.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        recyclerClosedApplications = findViewById(R.id.recyclerClosedApplications);
        recyclerClosedApplications.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        activeAdapter = new ApplicationAdapter(true, activeApplicationsList, this);
        closedAdapter = new ApplicationAdapter(false, closedApplicationsList, this);

        recyclerActiveApplications.setAdapter(activeAdapter);
        recyclerClosedApplications.setAdapter(closedAdapter);

        // If schedule meeting pressed
        activeAdapter.setOnItemClickListener(applicationData -> {
            // TODO: Implement schedule meeting here
            Toast.makeText(this, "Schedule meeting pressed ", Toast.LENGTH_SHORT).show();
        });
    }

    private void fetchActiveApplications() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(ApplicationsActivity.this, "User not logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

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

                                    for (DocumentSnapshot snapshot : value.getDocuments()) {
                                        try {
                                            ApplicationData applicationData = snapshot.toObject(ApplicationData.class);
                                            if (applicationData != null) {
                                                String status = applicationData.getStatus();
                                                // Add only applications that are not rejected or approved
                                                if (!"rejected".equalsIgnoreCase(status) &&
                                                        !"approved".equalsIgnoreCase(status)) {
                                                    applicationData.setApplicationId(snapshot.getId());
                                                    activeApplicationsList.add(applicationData);
                                                }
                                            }
                                        } catch (Exception e) {
                                            Toast.makeText(ApplicationsActivity.this, "Error parsing data: "
                                                    + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    if (activeApplicationsList.isEmpty()) {
                                        noActiveApplicationsTxt.setVisibility(View.VISIBLE);
                                    } else {
                                        noActiveApplicationsTxt.setVisibility(View.GONE);
                                    }

                                    activeAdapter.notifyDataSetChanged();
                                }
                            });
                } else {
                    noActiveApplicationsTxt.setVisibility(View.VISIBLE);
                    Toast.makeText(ApplicationsActivity.this, "No active applications found.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ApplicationsActivity.this, "User document not found.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(ApplicationsActivity.this, "Error fetching user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}
package com.example.purrfectmatch;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ApplicationsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private ImageView profile, explore, swipe;
    private RecyclerView recyclerActiveApplications, recyclerClosedApplications;
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
    }

    private void initializeViews() {
        profile = findViewById(R.id.profile);
        explore = findViewById(R.id.explore);
        swipe = findViewById(R.id.swipe);

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
}
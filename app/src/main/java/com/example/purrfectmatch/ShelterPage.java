package com.example.purrfectmatch;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

public class ShelterPage extends AppCompatActivity {

    private FirebaseFirestore db;
    private TextView txtCats, txtScheduled, txtPending;
    private int numCats, numScheduledAppointments, numPendingApplications;
    private RecyclerView recyclerViewPending, recyclerViewScheduled;
    private PendingAppHomeAdapter adapterPending;
    private List<Cat> catsWithPendingApps;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_shelter_page);

        txtCats = findViewById(R.id.totalCats);
        txtScheduled = findViewById(R.id.appointmentTxt);
        txtPending = findViewById(R.id.adoptionAppTxt);
        catsWithPendingApps = new ArrayList<>();

        recyclerViewPending = findViewById(R.id.recyclerViewPending);

        adapterPending = new PendingAppHomeAdapter(catsWithPendingApps, ShelterPage.this);
        recyclerViewPending.setAdapter(adapterPending);

        db = FirebaseFirestore.getInstance();
        initializeNumbers();
        Log.d("fetching", "fetching pending apps");
        fetchPendingApps();


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void fetchPendingApps() {
        // Initialize the list to store cats
        catsWithPendingApps.clear();

        Log.d("fetching", "fetching pending apps here");


        // Query to fetch the cats
        db.collection("Cats")  // Assuming you have a "Cats" collection
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot catSnapshot : task.getResult()) {
                            try {
                                Cat cat = catSnapshot.toObject(Cat.class);  // Convert the document into Cat object
                                Log.d("catP", cat.getName());
                                if (cat != null) {
                                    Log.d("catP", cat.getName() + " exists");
                                    if (cat.getPendingApplications() != null) {
                                        Log.d("catP", cat.getName() + " has pending application");

                                        catsWithPendingApps.add(cat);

                                    }
                                }
                            } catch (Exception e) {
                                Log.d("catP", "Error parsing cat data: "
                                        + e.getMessage());
                            }
                        }

                        recyclerViewPending.setLayoutManager(new GridLayoutManager(this, 1));
                        adapterPending.notifyDataSetChanged();
                    } else {
                        Toast.makeText(ShelterPage.this, "Error fetching cat data.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }




    public void goPendingApps(View v) {
        Intent i = new Intent(this, PendingApplications.class);
        this.startActivity(i);
    }

    public void goScheduledApp(View v) {
        Intent i = new Intent(this, ScheduledApplications.class);
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
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    numCats = querySnapshot.size();
                    txtCats.setText(String.valueOf(numCats));
                });

        // Fetch number of scheduled appointments
        db.collection("Applications")
                .whereEqualTo("status", "meeting_scheduled")
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
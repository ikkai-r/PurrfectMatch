package com.example.purrfectmatch;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import java.util.Arrays;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

public class ShelterPage extends AppCompatActivity {

    private FirebaseFirestore db;
    private TextView txtCats, txtScheduled, txtPending;
    private int numCats, numScheduledAppointments, numPendingApplications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_shelter_page);

        txtCats = findViewById(R.id.totalCats);
        txtScheduled = findViewById(R.id.appointmentTxt);
        txtPending = findViewById(R.id.adoptionAppTxt);

        db = FirebaseFirestore.getInstance();
        initializeNumbers();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
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
                    Toast.makeText(ShelterPage.this, "Number of Meeting Scheduled applications: "
                                    + numScheduledAppointments,
                            Toast.LENGTH_SHORT).show();
                });

        // Fetch number of pending applications
        db.collection("Applications")
                .whereIn("status", Arrays.asList("pending", "reviewed"))
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    numPendingApplications = querySnapshot.size();
                    txtPending.setText(String.valueOf(numPendingApplications));
                    Toast.makeText(ShelterPage.this, "Number of Pending or Reviewed " +
                                    "applications: " + numPendingApplications,
                            Toast.LENGTH_SHORT).show();
                });
    }
}
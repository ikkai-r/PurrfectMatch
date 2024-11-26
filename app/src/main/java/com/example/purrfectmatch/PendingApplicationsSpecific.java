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

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class PendingApplicationsSpecific extends AppCompatActivity {

    private TextView catPendingText;
    private List<ApplicationData> pendingAppList;
    private List<String> pendingAppListStr;
    private FirebaseFirestore db;

    private RecyclerView recyclerViewPending;
    private PendingAppAdapter adapterPending;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pending_specific_application);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        pendingAppList = new ArrayList<>();
        catPendingText = findViewById(R.id.catPendingText);
        Intent intent = getIntent();
        Cat currentCat = (Cat) intent.getSerializableExtra("cat");

        recyclerViewPending = findViewById(R.id.recyclerViewPending);

        db = FirebaseFirestore.getInstance();

        catPendingText.setText("Pending Applications for " + currentCat.getName());
        pendingAppListStr = currentCat.getPendingApplications();
        Log.d("catP", "pending apps of cat is" + currentCat.getName() + " " + currentCat.getPendingApplications());

        adapterPending = new PendingAppAdapter(pendingAppList, PendingApplicationsSpecific.this);
        recyclerViewPending.setAdapter(adapterPending);

        fetchPendingApps();

    }

    private void fetchPendingApps() {

        // Initialize the list to store cats
        pendingAppList.clear();

        Log.d("fetching", "fetching pending apps here");

        // Query to
        db.collection("Applications")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot appSnapshot : task.getResult()) {
                            try {
                                ApplicationData app = appSnapshot.toObject(ApplicationData.class);
                                Log.d("catP", app.getApplicationId());
                                Log.d("catP", "Raw Firestore data: " + appSnapshot.getData());
                                Log.d("catP", pendingAppListStr.toString());
                                Log.d("catP", String.valueOf(pendingAppListStr.contains(app.getApplicationId())));
                                if (app != null) {
                                    Log.d("catP", app.getApplicationId() + " exists");
                                    if (pendingAppListStr.contains((app.getApplicationId()))) {
                                        Log.d("catP", app.getApplicationId() + " has pending application");
                                        pendingAppList.add(app);
                                    }
                                }
                            } catch (Exception e) {
                                Log.d("catP", "Error parsing app data: "
                                        + e.getMessage());
                            }
                        }

                        recyclerViewPending.setLayoutManager(new GridLayoutManager(this, 1));
                        adapterPending.notifyDataSetChanged();
                    } else {
                        Toast.makeText(PendingApplicationsSpecific.this, "Error fetching cat data.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public void goViewApplication(View v) {
        Intent i = new Intent(this, ViewApplication.class);
        this.startActivity(i);
    }
}
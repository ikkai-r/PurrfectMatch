package com.example.purrfectmatch;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ShelterPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_shelter_page);
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

}
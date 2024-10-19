package com.example.purrfectmatch;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class ScheduleActivity extends AppCompatActivity {


    Button schedule, notAvailable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.schedule);

        schedule = findViewById(R.id.button2);
        notAvailable = findViewById(R.id.button3);

        schedule.setOnClickListener(view -> {
            Intent i = new Intent(this, ApplicationsActivity.class);
            startActivity(i);
        });

        notAvailable.setOnClickListener(view -> {
            Intent i = new Intent(this, ApplicationsActivity.class);
            startActivity(i);
        });
    }
}

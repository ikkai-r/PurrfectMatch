package com.example.purrfectmatch;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    TextView applications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ImageView profile, explore, swipe;

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.profile);

        applications = findViewById(R.id.textView22);

        applications.setOnClickListener(view -> {
            Intent i = new Intent(this, ApplicationsActivity.class);
            startActivity(i);
        });

        profile = findViewById(R.id.imageView16);

        explore = findViewById(R.id.imageView19);

        swipe = findViewById(R.id.imageView17);

        profile.setOnClickListener(view -> {
            Intent i = new Intent(this, ProfileActivity.class);
            startActivity(i);
        });

        swipe.setOnClickListener(view -> {
            Intent i = new Intent(this, SwipeActivity.class);
            startActivity(i);
        });

        explore.setOnClickListener(view -> {
            Intent i = new Intent(this, ExploreActivity.class);
            startActivity(i);
        });
    }

    @Override
    public void onBackPressed() {
        // Do nothing, so back navigation is disabled
    }

}

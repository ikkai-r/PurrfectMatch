package com.example.purrfectmatch;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class ExploreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ImageView profile, explore, swipe;
        LinearLayout card1, card2, card3, card4;

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.explore);

        profile = findViewById(R.id.imageView16);

        explore = findViewById(R.id.imageView19);

        swipe = findViewById(R.id.imageView17);

        card1 = findViewById(R.id.card1);

        card2 = findViewById(R.id.card2);

        card3 = findViewById(R.id.card3);

        card4 = findViewById(R.id.card4);

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

        card1.setOnClickListener(view -> {
            Intent i = new Intent(this, SwipeActivity.class);
            startActivity(i);
        });

        card2.setOnClickListener(view -> {
            Intent i = new Intent(this, SwipeActivity.class);
            startActivity(i);
        });

        card3.setOnClickListener(view -> {
            Intent i = new Intent(this, SwipeActivity.class);
            startActivity(i);
        });

        card4.setOnClickListener(view -> {
            Intent i = new Intent(this, SwipeActivity.class);
            startActivity(i);
        });

    }

    @Override
    public void onBackPressed() {
        // Do nothing, so back navigation is disabled
    }

}

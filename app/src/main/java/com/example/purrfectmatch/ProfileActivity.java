package com.example.purrfectmatch;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import com.google.firebase.auth.FirebaseAuth;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

public class ProfileActivity extends AppCompatActivity {

    TextView applications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ImageView explore, swipe;
        TextView logoutBtn;

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.profile);

        applications = findViewById(R.id.textView22);
        logoutBtn = findViewById(R.id.logoutBtn);


        applications.setOnClickListener(view -> {
            Intent i = new Intent(this, ApplicationsActivity.class);
            startActivity(i);
        });

        logoutBtn.setOnClickListener(view -> {
            signOutUser();
        });

        explore = findViewById(R.id.imageView19);

        swipe = findViewById(R.id.imageView17);

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

    public void signOutUser() {
        FirebaseAuth auth = FirebaseAuth.getInstance(); // Get FirebaseAuth instance
        auth.signOut(); // Sign out the user
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onBackPressed() {
        // Do nothing, so back navigation is disabled
    }

}

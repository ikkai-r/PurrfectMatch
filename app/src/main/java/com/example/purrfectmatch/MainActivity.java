package com.example.purrfectmatch;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Button button;
        mAuth = FirebaseAuth.getInstance();


        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        button = findViewById(R.id.button);

        button.setOnClickListener(view -> {
            Intent i = new Intent(this, Login.class);
            this.startActivity(i);
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Retrieve the user data from Firestore to check userType
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("Users").document(currentUser.getUid());

            userRef.get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Document retrieved successfully
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String userType = document.getString("userType");

                                // Check userType and navigate accordingly
                                if ("user".equals(userType)) {
                                    // User is a regular user, navigate to SwipeActivity
                                    Intent i = new Intent(MainActivity.this, SwipeActivity.class);
                                    MainActivity.this.startActivity(i);
                                } else if ("shelter".equals(userType)) {
                                    // User is a shelter, navigate to ShelterActivity (or another activity)
                                    Intent i = new Intent(MainActivity.this, ShelterPage.class);
                                    MainActivity.this.startActivity(i);
                                }
                                finish(); // Close the current activity
                            } else {
                                // Document does not exist, handle this case if needed
                                Toast.makeText(MainActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // If the task fails, log the error and show a message
                            Log.w("SignUp", "Error getting document", task.getException());
                            Toast.makeText(MainActivity.this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public void register(View v) {
        Intent i = new Intent(this, SignUp.class);
        this.startActivity(i);
    }
}
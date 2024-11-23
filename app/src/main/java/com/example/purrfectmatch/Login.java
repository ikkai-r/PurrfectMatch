package com.example.purrfectmatch;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();


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
                                    Intent i = new Intent(Login.this, SwipeActivity.class);
                                    Login.this.startActivity(i);
                                } else if ("shelter".equals(userType)) {
                                    // User is a shelter, navigate to ShelterActivity (or another activity)
                                    Intent i = new Intent(Login.this, ShelterPage.class);
                                    Login.this.startActivity(i);
                                }
                                finish(); // Close the current activity
                            } else {
                                // Document does not exist, handle this case if needed
                                Toast.makeText(Login.this, "User data not found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // If the task fails, log the error and show a message
                            Log.w("SignUp", "Error getting document", task.getException());
                            Toast.makeText(Login.this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public void loginSuccess(View v) {
        // Get all texts
        String email, password;
        email = String.valueOf(((EditText) findViewById(R.id.email)).getText());
        password = String.valueOf(((EditText) findViewById(R.id.password)).getText());

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, navigate to the appropriate activity
                            Log.d("signin", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            // Retrieve the user data from Firestore to check userType
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            DocumentReference userRef = db.collection("Users").document(user.getUid());

                            userRef.get().addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    DocumentSnapshot document = task1.getResult();
                                    if (document.exists()) {
                                        String userType = document.getString("userType");

                                        if ("user".equals(userType)) {
                                            Intent i = new Intent(Login.this, SwipeActivity.class);
                                            Login.this.startActivity(i);
                                        } else if ("shelter".equals(userType)) {
                                            Intent i = new Intent(Login.this, ShelterPage.class);
                                            Login.this.startActivity(i);
                                        }
                                        finish(); // Close the login activity
                                    } else {
                                        Toast.makeText(Login.this, "User data not found", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Log.w("Login", "Error getting document", task1.getException());
                                    Toast.makeText(Login.this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            // Handle login failure
                            Exception exception = task.getException();
                            if (exception instanceof FirebaseAuthException) {
                                String errorCode = ((FirebaseAuthException) exception).getErrorCode();
                                switch (errorCode) {
                                    case "ERROR_INVALID_CREDENTIAL":
                                        Toast.makeText(Login.this, "Invalid credentials.", Toast.LENGTH_SHORT).show();
                                        break;
                                    case "ERROR_INVALID_EMAIL":
                                        Toast.makeText(Login.this, "Invalid email format.", Toast.LENGTH_SHORT).show();
                                        break;
                                    default:
                                        Toast.makeText(Login.this, "Authentication failed. Please try again.", Toast.LENGTH_SHORT).show();
                                        break;
                                }
                            } else {
                                Toast.makeText(Login.this, "An unexpected error occurred.", Toast.LENGTH_SHORT).show();
                                Log.e("signin", "signInWithEmail:failure", exception);
                            }
                        }
                    }
                });
    }


    public void register(View v) {
        Intent i = new Intent(this, SignUp.class);
        this.startActivity(i);
    }
}
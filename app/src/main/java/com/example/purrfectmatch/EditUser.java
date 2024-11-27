package com.example.purrfectmatch;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditUser extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Bundle finalBundle;
    String profileImageUrl;
    String userType = "user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();

        if (savedInstanceState == null) {
            RegisterEditForm registerForm = new RegisterEditForm();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.formEdit, registerForm)  // Fragment will be added to the container
                    .commit();
        }
    }

    public void login(View v) {
        Intent i = new Intent(this, Login.class);
        this.startActivity(i);
        finish();
    }

    public void onDataPassed(Bundle bundle) {
        // Retrieve data from the bundle
        finalBundle = bundle;
        editUser();
    }
    private void editUser() {
        // Retrieve data from the previous bundle
        String password = finalBundle.getString("newPassword");
        String firstName = finalBundle.getString("firstname");
        String lastName = finalBundle.getString("lastname");
        String householdMembers = finalBundle.getString("householdMembers");
        String otherPets = finalBundle.getString("otherPets");
        String preferences1 = finalBundle.getString("preferences1");
        String preferences2 = finalBundle.getString("preferences2");
        String username = finalBundle.getString("username");

        String phoneNumber = finalBundle.getString("phoneNumber");
        String country = finalBundle.getString("country");
        String region = finalBundle.getString("region");
        String city = finalBundle.getString("city");
        String age = finalBundle.getString("age");
        String gender = finalBundle.getString("gender");

        String bio = finalBundle.getString("bio");
        String profileimg = finalBundle.getString("profileimg");

        Log.d("process", "I'm now here before");

        if (firstName != null && lastName != null &&
                householdMembers != null && otherPets != null && preferences1 != null && preferences2 != null &&
                profileimg != null && phoneNumber != null && country != null && region != null && city != null && age != null && gender != null) {

            // Check if username is not blank
            if (username != null && !username.isEmpty()) {
                checkIfUsernameExists(username, usernameExists -> {
                    if (usernameExists) {
                        // If username exists, show a toast and do not proceed
                        Toast.makeText(EditUser.this, "Username already exists. Please choose another one.", Toast.LENGTH_SHORT).show();
                    } else {
                        // Proceed with editing the user and update Firestore
                        updateUser(password, username, firstName, lastName, phoneNumber, country, region, city, age, gender, householdMembers, otherPets, preferences1, preferences2, bio, profileimg);
                    }
                });
            } else {
                // Skip username update and directly update other fields
                updateUser(password, null, firstName, lastName, phoneNumber, country, region, city, age, gender, householdMembers, otherPets, preferences1, preferences2, bio, profileimg);
            }
        }
    }

    private void updateUser(String password, String username, String firstName, String lastName, String phoneNumber, String country, String region, String city, String age, String gender, String householdMembers, String otherPets, String preferences1, String preferences2, String bio, String profileimg) {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("Users").document(user.getUid());

            // Map of fields to update
            Map<String, Object> updates = new HashMap<>();
            if (username != null) {
                updates.put("username", username);
            }
            updates.put("firstName", firstName);
            updates.put("lastName", lastName);
            updates.put("phoneNumber", phoneNumber);
            updates.put("country", country);
            updates.put("region", region);
            updates.put("city", city);
            updates.put("age", Integer.parseInt(age));
            updates.put("gender", gender);
            updates.put("householdMembers", householdMembers);
            updates.put("otherPets", otherPets);
            updates.put("preferences1", preferences1);
            updates.put("preferences2", preferences2);
            updates.put("bio", bio);
            updates.put("profileimg", profileimg);

            if (password != null && !password.isEmpty()) {
                user.updatePassword(password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d("updatePassword", "Password updated successfully.");
                            } else {
                                Log.w("updatePassword", "Error updating password", task.getException());
                            }
                        });
            }

            // Update Firestore
            userRef.update(updates)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("edit", "User data successfully updated in Firestore");

                        // Move to the success screen
                        Intent i = new Intent(EditUser.this, SuccessForm.class);
                        i.putExtra("title", "User details");
                        i.putExtra("title_big", "Updated");
                        i.putExtra("subtitle_1", "Successfully edited user account");
                        i.putExtra("button_text", "Okay");
                        i.putExtra("user_type", "user");
                        EditUser.this.startActivity(i);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Log.w("edit", "Error updating document", e);
                        Toast.makeText(EditUser.this, "Failed to save user data.", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(EditUser.this, "No authenticated user found.", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkIfUsernameExists(String username, UsernameCheckCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Users")
                .whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // Username exists, show toast
                        Toast.makeText(EditUser.this, "Username already exists. Please choose another one.", Toast.LENGTH_SHORT).show();
                        callback.onUsernameCheckComplete(true); // Username exists
                    } else {
                        callback.onUsernameCheckComplete(false); // Username doesn't exist
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("SignUp", "Error checking username", e);
                    callback.onUsernameCheckComplete(false); // Assume username is available if an error occurs
                });
    }


}

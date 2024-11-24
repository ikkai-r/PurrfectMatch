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

interface UsernameCheckCallback {
    void onUsernameCheckComplete(boolean usernameExists);
}

public class SignUp extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Bundle finalBundle;
    String profileImageUrl;
    String userType = "user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();

        checkUserIfSignedIn();

        if (savedInstanceState == null) {
            RegisterForm registerForm = new RegisterForm();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.form, registerForm)  // Fragment will be added to the container
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
        registerUser();
    }

    private void registerUser() {

        // Retrieve data from the previous bundle
        String email = finalBundle.getString("email");
        String password = finalBundle.getString("password");
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

        // Check if all values are retrieved properly
        if (email != null && password != null && firstName != null && lastName != null &&
                householdMembers != null && otherPets != null && preferences1 != null && preferences2 != null &&
                profileimg != null && phoneNumber != null && country != null && region != null && city != null && age != null && gender != null) {

            // Check if username exists asynchronously
            checkIfUsernameExists(username, usernameExists -> {
                if (usernameExists) {
                    // If username exists, show a toast and do not proceed
                    Toast.makeText(SignUp.this, "Username already exists. Please choose another one.", Toast.LENGTH_SHORT).show();
                } else {
                    // If username does not exist, proceed with user registration
                    Log.d("finish", "values are retrieved properly");

                    // Create user
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(SignUp.this, task -> {
                                if (task.isSuccessful()) {
                                    // Sign-in success, update UI with the signed-in user's information
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    // Create user data map for Firestore
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    DocumentReference userRef = db.collection("Users").document(user.getUid());

                                    //uploadImageToFirebase(Uri.parse(profileimg), user.getUid());

                                    // User data to be stored
                                    User userData = new User(username, firstName, lastName, email, phoneNumber, country, region, city, Integer.parseInt(age), gender,
                                            householdMembers,otherPets, preferences1, preferences2, userType, bio, profileimg);

                                    // Store the user data in Firestore
                                    userRef.set(userData)
                                            .addOnSuccessListener(aVoid -> {
                                                Log.d("empass", "User data successfully written to Firestore");
                                                // Move to the next screen
                                                Intent i = new Intent(SignUp.this, SuccessForm.class);
                                                i.putExtra("title", "Sign up");
                                                i.putExtra("title_big", "Success");
                                                i.putExtra("subtitle_1", "Welcome to our Purrfect family!");
                                                i.putExtra("subtitle_2", "Are you ready to meet the cat of your dreams?");
                                                i.putExtra("button_text", "Yes!");
                                                i.putExtra("user_type", "user");
                                                SignUp.this.startActivity(i);
                                                finish();
                                            })
                                            .addOnFailureListener(e -> {
                                                Log.w("empass", "Error writing document", e);
                                                Toast.makeText(SignUp.this, "Failed to save user data.", Toast.LENGTH_SHORT).show();
                                            });
                                } else {
                                    // If sign-in fails, display a message to the user.
                                    Log.w("empass", "createUserWithEmail:failure", task.getException());

                                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                        Toast.makeText(SignUp.this, "Email is already registered.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(SignUp.this, "Sign up failed.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            });

        } else {
            Log.d("finish", "values aren't retrieved properly");

            // Handle the case where any required field is missing
            Toast.makeText(this, "Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

//    // Upload the image to Firebase Storage
//    private void uploadImageToFirebase(Uri photoUri, String UUID) {
//        if (photoUri != null) {
//            // Create a reference to Firebase Storage in the "profile_images" folder
//            StorageReference storageReference = FirebaseStorage.getInstance().getReference("profile_images/" + UUID);
//
//            // Upload the image file
//            storageReference.putFile(photoUri)
//                    .addOnSuccessListener(taskSnapshot -> {
//                        // Get the download URL after the image has been uploaded
//                        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
//                                    // Get the URL of the uploaded image
//                                    profileImageUrl = uri.toString();
//                                    Log.d("Firebase", "Image uploaded successfully. URL: " + profileImageUrl);
//                                    // Now you can save the image URL to your Firestore or perform further actions
//                                })
//                                .addOnFailureListener(e -> {
//                                    // Handle errors
//                                    Log.e("Firebase", "Failed to get download URL", e);
//                                });
//                    })
//                    .addOnFailureListener(e -> {
//                        // Handle upload errors
//                        Log.e("Firebase", "Failed to upload image", e);
//                    });
//        }
//    }

    private void checkUserIfSignedIn() {
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
                                    Intent i = new Intent(SignUp.this, SwipeActivity.class);
                                    SignUp.this.startActivity(i);
                                } else if ("shelter".equals(userType)) {
                                    // User is a shelter, navigate to ShelterActivity (or another activity)
                                    Intent i = new Intent(SignUp.this, ShelterPage.class);
                                    SignUp.this.startActivity(i);
                                }
                                finish(); // Close the current activity
                            } else {
                                // Document does not exist, handle this case if needed
                                Toast.makeText(SignUp.this, "User data not found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // If the task fails, log the error and show a message
                            Log.w("SignUp", "Error getting document", task.getException());
                            Toast.makeText(SignUp.this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
                        }
                    });
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
                        Toast.makeText(SignUp.this, "Username already exists. Please choose another one.", Toast.LENGTH_SHORT).show();
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

package com.example.purrfectmatch;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

public class ProfileActivity extends AppCompatActivity {

    TextView applications, editProfileBtn, bioText, locationProfile, nameProfile, phoneNumberProfile, lifestyleText;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ImageView explore, swipe;
        TextView logoutBtn;

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.profile);

        mAuth = FirebaseAuth.getInstance();
        applications = findViewById(R.id.textView22);
        logoutBtn = findViewById(R.id.logoutBtn);
        bioText = findViewById(R.id.bioTextProfile);
        locationProfile = findViewById(R.id.locationProfile);
        nameProfile = findViewById(R.id.nameProfile);
        phoneNumberProfile = findViewById(R.id.phoneNumberProfile);
        lifestyleText = findViewById(R.id.lifestyleText);
        editProfileBtn = findViewById(R.id.editProfileBtn);

        setUserInfo();

        applications.setOnClickListener(view -> {
            Intent i = new Intent(this, ApplicationsActivity.class);
            startActivity(i);
        });

        logoutBtn.setOnClickListener(view -> {
            signOutUser();
        });

        editProfileBtn.setOnClickListener(view -> {
            Intent i = new Intent(this, EditUser.class);
            startActivity(i);
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

    private void setUserInfo() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("Users").document(user.getUid());

            // Fetch data from Firestore using the reference
            userRef.get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Extract data from the document snapshot
                            String nameText = documentSnapshot.getString("firstName") + " " + documentSnapshot.getString("lastName");

                            String lifestylePref = "Household Members: " + documentSnapshot.getString("householdMembers") + "\n" +
                                    "Other pets: " + documentSnapshot.getString("otherPets") + "\n" +
                                    "Cat Preferences: " + documentSnapshot.getString("preferences1") + ", " + documentSnapshot.getString("preferences2");
                            String phoneNumberTxt = documentSnapshot.getString("phoneNumber");
                            String bioTxt = documentSnapshot.getString("bio");
                            String locationTxt = documentSnapshot.getString("city") + ", " + documentSnapshot.get("province") + ", " + documentSnapshot.get("country");

                            // Display the data in the UI
                            nameProfile.setText(nameText);
                            lifestyleText.setText(lifestylePref);
                            phoneNumberProfile.setText(phoneNumberTxt);
                            bioText.setText(bioTxt);
                            locationProfile.setText(locationTxt);
                        } else {
                           Log.d("fe", "user doesnt exist");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.d("fe", "failed to get info");
                    });
        } else {
            Log.d("fe", "user is not signed in");
        }
    }


    @Override
    public void onBackPressed() {
        // Do nothing, so back navigation is disabled
        super.onBackPressed();
    }

}


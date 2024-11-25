package com.example.purrfectmatch;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ExploreAdapter adapter;
    private List<ExploreData> bookmarkedCats;

    private TextView applications, editProfileBtn, bioText, locationProfile, nameProfile,
             phoneNumberProfile, lifestyleText, logoutBtn, noBookmarkedCatsTxt;
    private ImageView explore, swipe, profilePicture;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        bookmarkedCats = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        adapter = new ExploreAdapter(bookmarkedCats, ProfileActivity.this);
        recyclerView.setAdapter(adapter);

        initializeViews();
        fetchBookmarkedCats();
        setUserInfo();
    }

    private void initializeViews() {
        applications = findViewById(R.id.applications);
        logoutBtn = findViewById(R.id.logoutBtn);
        bioText = findViewById(R.id.bioTextProfile);
        locationProfile = findViewById(R.id.locationProfile);
        nameProfile = findViewById(R.id.nameProfile);
        phoneNumberProfile = findViewById(R.id.phoneNumberProfile);
        lifestyleText = findViewById(R.id.lifestyleText);
        editProfileBtn = findViewById(R.id.editProfileBtn);
        explore = findViewById(R.id.explore);
        swipe = findViewById(R.id.swipe);
        profilePicture = findViewById(R.id.profilePicture);
        noBookmarkedCatsTxt = findViewById(R.id.noBookmarkedCatsTxt);

        adapter.setOnItemClickListener(cat -> {
            Intent i = new Intent(ProfileActivity.this, ClickedExploreActivity.class);
            i.putExtra("documentId", cat.getId());
            startActivity(i);
        });

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
        mAuth.signOut(); // Sign out the user
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    //TODO: Set profile picture
    private void setUserInfo() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
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
                            String locationTxt = documentSnapshot.getString("city") + ", " + documentSnapshot.get("region");

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

    private void fetchBookmarkedCats() {
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        DocumentReference userRef = db.collection("Users").document(userId);

        // Fetch the bookmarked cat IDs
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<String> bookmarkedCatIds = (List<String>) documentSnapshot.get("bookmarkedCats");

                if (bookmarkedCatIds != null && !bookmarkedCatIds.isEmpty()) {
                    db.collection("Cats")
                            .whereIn(FieldPath.documentId(), bookmarkedCatIds)
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot value,
                                                    @Nullable FirebaseFirestoreException error) {
                                    if (error != null) {
                                        Toast.makeText(ProfileActivity.this, "Error fetching data: "
                                                + error.getMessage(), Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    bookmarkedCats.clear();

                                    for (DocumentSnapshot snapshot : value.getDocuments()) {
                                        try {
                                            ExploreData cat = snapshot.toObject(ExploreData.class);
                                            if (cat != null) {
                                                cat.setId(snapshot.getId());
                                                bookmarkedCats.add(cat);
                                            }
                                        } catch (Exception e) {
                                            Toast.makeText(ProfileActivity.this, "Error parsing data: "
                                                    + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    if (bookmarkedCats.isEmpty()) {
                                        noBookmarkedCatsTxt.setVisibility(View.VISIBLE);
                                    } else {
                                        noBookmarkedCatsTxt.setVisibility(View.GONE);
                                    }

                                    adapter.notifyDataSetChanged();
                                }
                            });
                } else {
                    Toast.makeText(this, "No bookmarked cats found.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "User document not found.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error fetching user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }


    @Override
    public void onBackPressed() {
        // Do nothing, so back navigation is disabled
        super.onBackPressed();
    }
}
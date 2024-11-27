package com.example.purrfectmatch;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ScheduledApplications extends AppCompatActivity {

    private TextView appTitle, nameAge, householdMembers, otherPets, gender, address, social,
            applicationDate, schedule, percentage, energy,incomeBracket, reasonAdopt;
    private Dialog dialog;
    private String catCompatibility, catTemp2, catTemp1, appId, catId;
    private FirebaseFirestore db;
    private int userAge;
    private Button acceptButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_scheduled_app_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();

        HashMap<String, String> app = (HashMap<String, String>) getIntent().getSerializableExtra("app");

        appTitle = findViewById(R.id.appTitle);
        nameAge = findViewById(R.id.nameAge);
        householdMembers = findViewById(R.id.householdMembers);
        otherPets = findViewById(R.id.otherPets);
        gender = findViewById(R.id.gender);
        address = findViewById(R.id.address);
        social = findViewById(R.id.social);
        energy = findViewById(R.id.energy);
        applicationDate = findViewById(R.id.applicationDate);
        percentage = findViewById(R.id.percentage);
        incomeBracket = findViewById(R.id.incomeBracket);
        schedule = findViewById(R.id.schedule);
        acceptButton = findViewById(R.id.acceptButton);

        Log.d("app inside", app.toString());
        reasonAdopt = findViewById(R.id.reasonAdopt);

        if (app != null) {

            Log.d("scheduled", "in view" + app.toString());
            appId = app.get("appId");
            applicationDate.setText(app.get("appDate"));
            schedule.setText(formatSchedule(app.get("finalDate"), app.get("finalTime")));
            reasonAdopt.setText(app.get("reason"));
            String userId = app.get("userId");

            Log.d("scheduled", "userId: " + userId);


            // Inner query to fetch user details
            db.collection("Users")
                    .document(userId) // Access the specific user's document
                    .get()
                    .addOnSuccessListener(userDoc -> {
                        if (userDoc.exists()) {
                            Long age = (Long) userDoc.get("age");
                            userAge = age.intValue();
                            nameAge.setText(userDoc.get("firstName") + " " + userDoc.get("lastName") + ", " + userAge); // name + age
                            householdMembers.setText("Household Members: " + userDoc.get("householdMembers"));
                            otherPets.setText("Other Pets: " + userDoc.get("otherPets"));
                            gender.setText("Gender: " + userDoc.get("gender")); // Static or dynamic based on your field
                            address.setText("Address: " + userDoc.get("city") + ", " + userDoc.get("region")); // Replace with user-provided address if available
                            energy.setText("Energy level: " + userDoc.get("preferences2"));
                            social.setText("Temperament: " + userDoc.get("preferences1"));

                            int ageInt = ((Long) userDoc.get("age")).intValue();
                            if(ageInt < 18) {
                                incomeBracket.setText("Likely a student and dependent.");
                            } else if(ageInt > 18 && Integer.parseInt((String) userDoc.get("age")) < 23) {
                                incomeBracket.setText("Likely a student and may be working.");
                            } else if(ageInt > 23)  {
                                incomeBracket.setText("Likely working already.");
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("fetching", "Failed to fetch user details: " + e.getMessage());
                    });

            catId = app.get("catId");


            Log.d("scheduled", "catId: " + catId);


            db.collection("Cats")
                    .document(catId) // Access the specific user's document
                    .get()
                    .addOnSuccessListener(catDoc -> {
                        if (catDoc.exists()) {
                            // Add user details to app data
                            appTitle.setText("Application for " + catDoc.get("name"));
                            catCompatibility = (String) catDoc.get("compatibleWith");
                            catTemp1 = catDoc.getString("temperament1");
                            catTemp2 = catDoc.getString("temperament2");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("cats", "Failed to fetch cat details: " + e.getMessage());
                    });

            //compute matching percentage
            // Calculate the matching traits and percentage
            double matchingPercentage = calculateMatchPercentage(householdMembers.getText().toString(), otherPets.getText().toString(), catCompatibility, social.getText().toString(), catTemp1, energy.getText().toString(), catTemp2, userAge);
            percentage.setText(String.format("%.2f", matchingPercentage) + "% match");
        }

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_reject_app);

        Button rejectButton = dialog.findViewById(R.id.dialog_reject_button);
        Button cancelButton = dialog.findViewById(R.id.dialog_cancel_button);
        String userId = app.get("userId");

        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // change status of application to rejected

                if(appId != null) {
                    rejectApplication(appId, catId, userId);
                }
            }
        });

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(appId != null) {
                    acceptApp(v);
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Just dismiss the dialog
                dialog.dismiss();
            }
        });

    }

    private String formatSchedule(String finalDate, String finalTime) {

        try {
            // Parse the input finalDate in the given format
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date date = dateFormat.parse(finalDate);

            // Format the parsed date to the desired format
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("MMMM dd");
            String formattedDate = outputDateFormat.format(date);

            // Combine the formatted date and time
            return "Schedule is on " + formattedDate + ", " + finalTime;
        } catch (Exception e) {
            // Handle parsing error
            e.printStackTrace();
            return "Invalid date format";
        }
    }
    private void rejectApplication(String appId, String catId, String userId) {
        EditText reasonEditText = dialog.findViewById(R.id.reasonEditText);
        String reason = reasonEditText.getText().toString();

        // Update application status to "rejected" with reason
        db.collection("Applications") // Replace with your collection name
                .document(appId)
                .update(
                        "status", "rejected",
                        "feedback", reason,
                        "acknowledged", "No"
                )
                .addOnSuccessListener(aVoid -> {
                    Log.d("RejectApp", "Application status updated to rejected.");

                    // Remove the appId from the cat's pendingApplications list
                    db.collection("Cats") // Replace "Cats" with your cat collection name
                            .document(catId)
                            .update("pendingApplications", FieldValue.arrayRemove(appId))
                            .addOnSuccessListener(catUpdate -> {
                                Log.d("RejectApp", "Application ID removed from cat's pendingApplications list.");

                                // Remove the appId from the user's pendingApplications list
                                db.collection("Users") // Replace "Users" with your users collection name
                                        .document(userId)
                                        .update("pendingApplications", FieldValue.arrayRemove(appId))
                                        .addOnSuccessListener(userUpdate -> {
                                            Log.d("RejectApp", "Application ID removed from user's pendingApplications list.");

                                            // Navigate back to ShelterPage
                                            Intent i = new Intent(ScheduledApplications.this, ShelterPage.class);
                                            ScheduledApplications.this.startActivity(i);
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("RejectApp", "Failed to remove appId from user's pendingApplications list: " + e.getMessage());
                                            Toast.makeText(ScheduledApplications.this, "Failed to update user's information. Try again later.", Toast.LENGTH_SHORT).show();
                                        });
                            })
                            .addOnFailureListener(e -> {
                                Log.e("RejectApp", "Failed to update cat's pendingApplications list: " + e.getMessage());
                                Toast.makeText(ScheduledApplications.this, "Failed to update cat's information. Try again later.", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("RejectApp", "Error updating application status: " + e.getMessage());
                    Toast.makeText(ScheduledApplications.this, "Failed to reject application. Try again later.", Toast.LENGTH_SHORT).show();
                });
    }

    public double calculateMatchPercentage(String userHousehold,
                                           String userOtherPets,
                                           String catPetsCompatibility,
                                           String userTempSocial,
                                           String catTemp1,
                                           String userTempEnergy,
                                           String catTemp2,
                                           int userAge) {
        // Define weights
        final int HOUSEHOLD_WEIGHT = 25;
        final int OTHER_PETS_WEIGHT = 25;
        final int SOCIAL_TEMPERAMENT_WEIGHT = 20;
        final int ENERGY_TEMPERAMENT_WEIGHT = 20;
        final int AGE_WEIGHT = 10; // Weight for age factor

        // Track total weights
        int totalWeight = HOUSEHOLD_WEIGHT + OTHER_PETS_WEIGHT + SOCIAL_TEMPERAMENT_WEIGHT + ENERGY_TEMPERAMENT_WEIGHT + AGE_WEIGHT;
        int totalMatchWeight = 0;

        // Compare household members to cat's activity level
        if ((userHousehold.equals("3-4 members") && catTemp2.equals("Active / Playful")) ||
                (userHousehold.equals("1-2 members") && catTemp2.equals("Quiet / Shy")) ||
                (userHousehold.equals("5+ members") && catTemp2.equals("Active / Playful"))) {
            totalMatchWeight += HOUSEHOLD_WEIGHT;
        }

        // Compare other pets with compatibility
        if ((userOtherPets.equals("Yes") && catPetsCompatibility.contains("Yes")) ||
                (userOtherPets.equals("No") && catPetsCompatibility.contains("No"))) {
            totalMatchWeight += OTHER_PETS_WEIGHT;
        }

        // Compare social temperament
        if (userTempSocial.equals(catTemp1)) {
            totalMatchWeight += SOCIAL_TEMPERAMENT_WEIGHT;
        }

        // Compare energy temperament
        if (userTempEnergy.equals(catTemp2)) {
            totalMatchWeight += ENERGY_TEMPERAMENT_WEIGHT;
        }

        // Calculate age score
        int ageScore = calculateAgeScore(userAge);
        totalMatchWeight += ageScore * AGE_WEIGHT / 30; // Scale ageScore (max 30) to AGE_WEIGHT

        // Calculate percentage match
        return (double) totalMatchWeight / totalWeight * 100;
    }

    private int calculateAgeScore(int age) {
        // Age scoring based on ranges
        if (age < 18) {
            return 5; // Not suitable
        } else if (age >= 18 && age <= 24) {
            return 10;
        } else if (age >= 25 && age <= 34) {
            return 20;
        } else {
            return 30; // Highest suitability
        }
    }

    public void rejectApp(View v) {
        dialog.show();
    }

    public void acceptApp(View v) {
        HashMap<String, String> app = (HashMap<String, String>) getIntent().getSerializableExtra("app");
        String appId = app.get("appId");
        String catId = app.get("catId");
        String userId = app.get("userId");

    // Start by updating the application status to "accepted"
    db.collection("Applications")
            .document(appId)
            .update("status", "accepted")
            .addOnSuccessListener(aVoid -> {
                Log.d("AcceptApp", "Application status updated to accepted.");

                // Now update the cat's status to adopted
                db.collection("Cats")
                        .document(catId)
                        .update("isAdopted", true)
                        .addOnSuccessListener(catUpdate -> {
                            Log.d("AcceptApp", "Cat's adoption status updated to true.");

                            // Remove the applicationId from the cat's pendingApplications array
                            db.collection("Cats")
                                    .document(catId)
                                    .update("pendingApplications", FieldValue.arrayRemove(appId))
                                    .addOnSuccessListener(removePendingApp -> {
                                        Log.d("AcceptApp", "ApplicationId removed from cat's pendingApplications.");

                                        // Remove the applicationId from the user's pendingApplications array
                                        db.collection("Users")
                                                .document(userId)
                                                .update("pendingApplications", FieldValue.arrayRemove(appId))
                                                .addOnSuccessListener(removeUserPendingApp -> {
                                                    Log.d("AcceptApp", "ApplicationId removed from user's pendingApplications.");

                                                    // Update cat adoption details for the user
                                                    db.collection("Users")
                                                            .document(userId)
                                                            .update(
                                                                    "adoptedCatIds", FieldValue.arrayUnion(catId),
                                                                    "adoptedCatCount", FieldValue.increment(1)
                                                            )
                                                            .addOnSuccessListener(userUpdate -> {
                                                                Log.d("AcceptApp", "User's adoption record updated.");

                                                                // Also update the adoptedBy field for the cat
                                                                db.collection("Cats")
                                                                        .document(catId)
                                                                        .update("adoptedBy", userId) // Record who adopted the cat
                                                                        .addOnSuccessListener(adoptedCatUpdate -> {
                                                                            Log.d("AcceptApp", "Adopted cat details updated.");

                                                                            // Navigate back to ShelterPage
                                                                            Intent i = new Intent(ScheduledApplications.this, ShelterPage.class);
                                                                            ScheduledApplications.this.startActivity(i);
                                                                        })
                                                                        .addOnFailureListener(e -> {
                                                                            Log.e("AcceptApp", "Error updating adopted cat details: " + e.getMessage());
                                                                        });
                                                            })
                                                            .addOnFailureListener(e -> {
                                                                Log.e("AcceptApp", "Error updating user's adoption record: " + e.getMessage());
                                                            });
                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.e("AcceptApp", "Error removing applicationId from user's pendingApplications: " + e.getMessage());
                                                    Toast.makeText(ScheduledApplications.this, "Failed to update user. Try again later.", Toast.LENGTH_SHORT).show();
                                                });
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("AcceptApp", "Error removing applicationId from cat's pendingApplications: " + e.getMessage());
                                        Toast.makeText(ScheduledApplications.this, "Failed to update cat adoption. Try again later.", Toast.LENGTH_SHORT).show();
                                    });
                        })
                        .addOnFailureListener(e -> {
                            Log.e("AcceptApp", "Failed to update cat's adoption status: " + e.getMessage());
                            Toast.makeText(ScheduledApplications.this, "Failed to update cat adoption. Try again later.", Toast.LENGTH_SHORT).show();
                        });
            })
            .addOnFailureListener(e -> {
                Log.e("AcceptApp", "Error updating application status: " + e.getMessage());
                Toast.makeText(ScheduledApplications.this, "Failed to accept the application. Try again later.", Toast.LENGTH_SHORT).show();
            });
    }

}
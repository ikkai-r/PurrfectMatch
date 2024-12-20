package com.example.purrfectmatch;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
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
    private Button acceptButton, rejectButton;
    private ImageView userImage;

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
        userImage = findViewById(R.id.userImage);
        applicationDate = findViewById(R.id.applicationDate);
        percentage = findViewById(R.id.percentage);
        incomeBracket = findViewById(R.id.incomeBracket);
        schedule = findViewById(R.id.schedule);
        acceptButton = findViewById(R.id.acceptButton);
        rejectButton = findViewById(R.id.rejectButton);

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
                            Glide.with(getApplicationContext()).load(userDoc.get("profileimg")).into(userImage);

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

        String userId = app.get("userId");


        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // change status of application to rejected

                if(appId != null) {
                    rejectApp(v);
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
    private void rejectApplication(String feedback) {
        HashMap<String, String> app = (HashMap<String, String>) getIntent().getSerializableExtra("app");
        String appId = app.get("appId");
        String catId = app.get("catId");
        String userId = app.get("userId");


        // Update application status to "rejected" with reason
        db.collection("Applications") // Replace with your collection name
                .document(appId)
                .update(
                        "status", "rejected",
                        "feedback", feedback,
                        "acknowledged", "No"
                )
                .addOnSuccessListener(aVoid -> {
                    Log.d("RejectApp", "Application status updated to rejected.");

                    // Remove the appId from the cat's pendingApplications list
                    db.collection("Cats") 
                            .document(catId)
                            .update("pendingApplications", FieldValue.arrayRemove(appId))
                            .addOnSuccessListener(catUpdate -> {
                                Log.d("RejectApp", "Application ID removed from cat's pendingApplications list.");

                                // Remove the appId from the user's pendingApplications
                                db.collection("Users") 
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
        View popupView = LayoutInflater.from(ScheduledApplications.this).inflate(R.layout.dialog_reject_app, null);
        PopupWindow popupWindow = createPopup(popupView, v);

        Button rejectButton = popupView.findViewById(R.id.dialog_reject_button);
        Button cancelButton = popupView.findViewById(R.id.dialog_cancel_button);
        EditText reasonEditText = popupView.findViewById(R.id.reasonEditText);



        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String feedback = reasonEditText.getText().toString();
                rejectApplication(feedback);
                popupWindow.dismiss();
            }
        });

        // Set OnClickListener for the cancel button
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }

    public void acceptAppBackend(View v, String feedback) {
        HashMap<String, String> app = (HashMap<String, String>) getIntent().getSerializableExtra("app");
        String appId = app.get("appId");
        String catId = app.get("catId");
        String userId = app.get("userId");

        // Start by updating the application status to "accepted"
        db.collection("Applications")
                .document(appId)
                .update(
                    "status", "accepted",
                    "feedback", feedback
                )
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
                                                                        "adoptedCatIds", FieldValue.arrayUnion(catId)
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

        public void acceptApp(View v) {
            // Inflate the dialog layout for accepting the cat adoption
            View popupView = LayoutInflater.from(ScheduledApplications.this).inflate(R.layout.dialog_accept_app, null);
            // Create the PopupWindow
            PopupWindow popupWindow = createPopup(popupView, v);

            // Initialize the buttons in the dialog
            Button confirmButton = popupView.findViewById(R.id.confirm_adoption_button);
            Button cancelButton = popupView.findViewById(R.id.close_button);
            EditText feedbackEditText = popupView.findViewById(R.id.feedback);

           confirmButton.setOnClickListener(view -> {
               String feedbackMessage = feedbackEditText.getText().toString();
                acceptAppBackend(view, feedbackMessage);
                popupWindow.dismiss();
           });

            cancelButton.setOnClickListener(view -> {
                popupWindow.dismiss();
            });
        }


        private PopupWindow createPopup(View popupView, View anchorView) {
            // Define the layout parameters
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int screenWidth = displayMetrics.widthPixels;
            int width = (int) (screenWidth * 0.8);
            int height = LinearLayout.LayoutParams.WRAP_CONTENT;

            // Create a new PopupWindow
            final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

            // Set the background of the PopupWindow to transparent
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            // Create an overlay with transparent background
            View overlay = new View(this);
            overlay.setBackgroundColor(Color.parseColor("#111111"));
            overlay.setAlpha(0.6f);  // Adjust transparency as needed

            // Add the overlay to the root layout
            ViewGroup rootLayout = findViewById(android.R.id.content);
            rootLayout.addView(overlay);

            // Show the PopupWindow at the center of the screen
            popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0);

            // Remove the overlay when the PopupWindow is dismissed
            popupWindow.setOnDismissListener(() -> rootLayout.removeView(overlay));

            return popupWindow;
        }

}
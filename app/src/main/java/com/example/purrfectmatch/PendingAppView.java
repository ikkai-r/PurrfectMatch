package com.example.purrfectmatch;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import com.google.firebase.firestore.FieldValue;


public class PendingAppView extends AppCompatActivity {

    TextView appTitle, nameAge, householdMembers, otherPets, gender, address, social,
    applicationDate, percentage, energy,incomeBracket, reasonAdopt;
    Dialog dialog;
    String appId, catId;
    FirebaseFirestore db;
    Button buttonSchedule;
    ImageView userImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pending_app_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();

        // Retrieve the user and cat fields from the intent
        HashMap<String, String> userFields = (HashMap<String, String>) getIntent().getSerializableExtra("user");
        HashMap<String, String> catFields = (HashMap<String, String>) getIntent().getSerializableExtra("cat");

        // Retrieve the ApplicationData object via the Intent
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
        reasonAdopt = findViewById(R.id.reasonAdopt);
        percentage = findViewById(R.id.percentage);
        incomeBracket = findViewById(R.id.incomeBracket);
        buttonSchedule = findViewById(R.id.buttonSchedule);


        // // Set text values based on ApplicationData fields
        if (app != null) {
            applicationDate.setText(app.get("appDate")); // Convert Timestamp to Date
            appId = app.get("appId");
            reasonAdopt.setText(app.get("appReason"));

            if(app.get("appStatus").equals("reviewed")) {
                buttonSchedule.setText("Accept");
                buttonSchedule.setOnClickListener(v -> goAcceptApp(v));

            } else if(app.get("appStatus").equals("pending")) {
                buttonSchedule.setText("Schedule");
                buttonSchedule.setOnClickListener(v -> goScheduledApp(v));
            }
        }

        // Set text values based on userFields

        Log.d("papa", String.valueOf(userFields));
        Log.d("papa", String.valueOf(catFields));

        if (userFields != null) {
            nameAge.setText(userFields.get("firstName") + " " + userFields.get("lastName") + ", " + userFields.get("age")); // name + age
            householdMembers.setText("Household Members: " + userFields.get("householdMembers"));
            otherPets.setText("Other Pets: " + userFields.get("otherPets"));
            gender.setText("Gender: " + userFields.get("gender")); // Static or dynamic based on your field
            address.setText("Address: " + userFields.get("region")); // Replace with user-provided address if available
            energy.setText("Energy level: " + userFields.get("preferences2"));
            social.setText("Temperament: " + userFields.get("preferences1"));
            Glide.with(getApplicationContext())
                    .load(userFields.get("profileimg")) // Load the URI/URL
                    .into(userImage);

            if(Integer.parseInt(userFields.get("age")) < 18) {
                incomeBracket.setText("Likely a student and dependent.");
            } else if(Integer.parseInt(userFields.get("age")) > 18 && Integer.parseInt(userFields.get("age")) < 23) {
                incomeBracket.setText("Likely a student and may be working.");
            } else if(Integer.parseInt(userFields.get("age")) > 23)  {
                incomeBracket.setText("Likely working already.");
            }

        }

        // Set text values based on catFields
        if (catFields != null) {
            catId = catFields.get("catId");
            appTitle.setText("Application for " + catFields.get("name"));
            percentage.setText(catFields.get("percentage") + "% match");
        }


        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_reject_app);

        Button rejectButton = dialog.findViewById(R.id.dialog_reject_button);
        Button cancelButton = dialog.findViewById(R.id.dialog_cancel_button);

        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // change status of application to rejected

                if(appId != null) {
                    rejectApplication(appId, catId);
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
    private void rejectApplication(String appId, String catId) {
        EditText reasonEditText = dialog.findViewById(R.id.reasonEditText);
        String feedback = reasonEditText.getText().toString();

        // Update application status to "rejected" with feedback
        db.collection("Applications") // Replace with your collection name
                .document(appId)
                .update(
                        "status", "rejected",
                        "feedback", feedback
                )
                .addOnSuccessListener(aVoid -> {
                    Log.d("RejectApp", "Application status updated to rejected.");
                    // Remove the appId from the cat's pendingApps list
                    db.collection("Cats") // Replace "Cats" with your cat collection name
                            .document(catId)
                            .update("pendingApplications", FieldValue.arrayRemove(appId))
                            .addOnSuccessListener(catUpdate -> {
                                Log.d("RejectApp", "Application ID removed from cat's pendingApps list.");

                                // After saving, start the ShelterPage activity
                                Intent i = new Intent(PendingAppView.this, SuccessForm.class);
                                i.putExtra("title", "Adopter Application");
                                i.putExtra("title_big", "Rejected");
                                i.putExtra("subtitle_1", "Successfully rejected adoption request");
                                i.putExtra("button_text", "Okay");
                                i.putExtra("user_type", "shelter");
                                startActivity(i);
                            })
                            .addOnFailureListener(e -> {
                                Log.e("RejectApp", "Failed to update cat's pendingApps list: " + e.getMessage());
                                Toast.makeText(PendingAppView.this, "Failed to update cat's information. Try again later.", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("RejectApp", "Error updating application status: " + e.getMessage());
                    Toast.makeText(PendingAppView.this, "Failed to reject application. Try again later.", Toast.LENGTH_SHORT).show();
                });
    }
    public void rejectApp(View v) {
        dialog.show();
    }

    public void goScheduledApp(View v) {
        Intent i = new Intent(this, ScheduleShelter.class);
        i.putExtra("appId", appId);
        this.startActivity(i);
    }


    public void goAcceptApp(View v) {
        //accept adopter
        // application

        // Update application status to "rejected" with feedback
        db.collection("Applications") // Replace with your collection name
                .document(appId)
                .update(
                        "status", "accepted"
                )
                .addOnSuccessListener(aVoid -> {
                    Log.d("AcceptApp", "Application status updated to accepted.");
                    // Remove the appId from the cat's pendingApps list
                    db.collection("Cats") // Replace "Cats" with your cat collection name
                            .document(catId)
                            .update("pendingApplications", FieldValue.arrayRemove(appId),
                                    "isAdopted", true)
                            .addOnSuccessListener(catUpdate -> {
                                Log.d("AcceptApp", "Application ID removed from cat's pendingApps list and isAdopted true.");

                                // After saving, start the ShelterPage activity
                                Intent i = new Intent(PendingAppView.this, SuccessForm.class);
                                i.putExtra("title", "Adopter Application");
                                i.putExtra("title_big", "Accepted");
                                i.putExtra("subtitle_1", "Successfully accepted adoption request");
                                i.putExtra("button_text", "Okay");
                                i.putExtra("user_type", "shelter");
                                startActivity(i);
                            })
                            .addOnFailureListener(e -> {
                                Log.e("AcceptApp", "Failed to update cat's pendingApps list: " + e.getMessage());
                                Toast.makeText(PendingAppView.this, "Failed to update cat's information. Try again later.", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("AcceptApp", "Error updating application status: " + e.getMessage());
                    Toast.makeText(PendingAppView.this, "Failed to accept application. Try again later.", Toast.LENGTH_SHORT).show();
                });
    }
}
package com.example.purrfectmatch;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class PendingAppView extends AppCompatActivity {

    TextView appTitle, nameAge, householdMembers, otherPets, gender, address, social,
    applicationDate, percentage, energy,incomeBracket;

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
         applicationDate = findViewById(R.id.applicationDate);
        percentage = findViewById(R.id.percentage);
        incomeBracket = findViewById(R.id.incomeBracket);

        // Set text values based on ApplicationData fields
        if (app != null) {
            applicationDate.setText(app.get("appDate")); // Convert Timestamp to Date
        }

        // Set text values based on userFields

        Log.d("papa", String.valueOf(userFields));
        Log.d("papa", String.valueOf(catFields));

        if (userFields != null) {
            nameAge.setText(userFields.get("name") + ", " + userFields.get("age")); // name + age
            householdMembers.setText("Household Members: " + userFields.get("householdMembers"));
            otherPets.setText("Other Pets: " + userFields.get("otherPets"));
            gender.setText("Gender: " + userFields.get("gender")); // Static or dynamic based on your field
            address.setText("Address: " + userFields.get("address2")); // Replace with user-provided address if available
            energy.setText("Energy level: " + userFields.get("preferences2"));
            social.setText("Temperament: " + userFields.get("preferences1"));

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
            appTitle.setText("Application for " + catFields.get("catName"));
            percentage.setText(catFields.get("percentage") + "% match");
        }

    }
}
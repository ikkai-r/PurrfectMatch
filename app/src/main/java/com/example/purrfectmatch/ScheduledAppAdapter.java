package com.example.purrfectmatch;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ScheduledAppAdapter extends RecyclerView.Adapter<ScheduledAppAdapter.ViewHolder> {

    private List<HashMap<String, String>> scheduledApps;
    private Context context;
    private HashMap<String, String> app;
    private OnItemClickListener listener;
    Intent i;

    public ScheduledAppAdapter(List<HashMap<String, String>> scheduledApps, Context context) {
        this.scheduledApps = scheduledApps;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate your pending application row layout
        View view = LayoutInflater.from(context).inflate(R.layout.app_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        app = scheduledApps.get(position);
        Log.d("scheduled", "clicked view" + app.toString());

        if (app != null) {

            // Get name from db using user id in app

            holder.date.setText("Date: " + app.get("appDate"));

            // Fetch both applicant and cat data and display matching traits
            fetchApplicantandCatData(app.get("userId"), app.get("catId"), holder);

            holder.itemView.setOnClickListener(view -> {
                if (listener != null) {
                    listener.onItemClick(app);
                }


                // Create an Intent to navigate to the specific application's page for the current cat
                Intent intent = new Intent(context, ScheduledApplications.class);
                intent.putExtra("app", app);  // Pass the application object via Intent
                context.startActivity(intent);  // Start the new activity
            });

        }
    }

    private String formatFirebaseTimestamp(Timestamp timestamp) {
        if (timestamp == null) {
            return "Invalid Timestamp"; // Handle null values gracefully
        }

        // Convert Timestamp to Date
        Date date = timestamp.toDate();

        // Format the Date to "January 26, 2014"
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
        return sdf.format(date);
    }

    private void fetchApplicantandCatData(String userId, String catId, final ViewHolder holder) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Fetch user data
        db.collection("Users").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String userName = document.getString("firstName") + " " + document.getString("lastName");
                            String userHousehold = document.getString("householdMembers");
                            String userOtherPets = document.getString("otherPets");
                            String userTempSocial = document.getString("preferences1");
                            String userTempEnergy = document.getString("preferences2");
                            int userAge = document.getLong("age").intValue();
                            // Set applicant's name
                            holder.applicant.setText("Applicant: " + userName);
                            Glide.with(context).load(document.getString("profileimg")).into(holder.userImage);


                            // Fetch cat data
                            db.collection("Cats").document(catId).get()
                                    .addOnCompleteListener(catTask -> {
                                        if (catTask.isSuccessful()) {
                                            DocumentSnapshot catDocument = catTask.getResult();
                                            if (catDocument.exists()) {
                                                String catTemp1 = catDocument.getString("temperament1");
                                                String catTemp2 = catDocument.getString("temperament2");
                                                String catCompatibility = catDocument.getString("compatibleWith");

                                                // Calculate the matching traits and percentage
                                                String matchingTraits = getMatchingTraits(userHousehold, userOtherPets, catCompatibility, userTempSocial, catTemp1, userTempEnergy, catTemp2);
                                                double matchingPercentage = calculateMatchPercentage(userHousehold, userOtherPets, catCompatibility, userTempSocial, catTemp1, userTempEnergy, catTemp2, userAge);

                                                // Display matching traits and percentage
                                                holder.matching.setText(matchingTraits);
                                                holder.percentage.setText("They are " + String.format("%.2f", matchingPercentage) + "% compatible based on traits and preferences");
                                                holder.catSelect.setText(catDocument.getString("name"));
                                            }
                                        } else {
                                            Log.d("PendingAppAdapter", "Error getting cat data: ", catTask.getException());
                                        }
                                    });
                        }
                    } else {
                        Log.d("PendingAppAdapter", "Error getting user data: ", task.getException());
                    }
                });
    }


    public String getMatchingTraits(String userHousehold,
                                    String userOtherPets, String catPetsCompatibility,
                                    String userTempSocial, String catTemp1,
                                    String userTempEnergy, String catTemp2) {
        StringBuilder matchingTraits = new StringBuilder();

        // Compatible in: living situation
        // Compare household members to cat's social characteristic
        if ((userHousehold.equals("3-4 members") && catTemp2.equals("Active / Playful")) ||
                (userHousehold.equals("1-2 members") && catTemp2.equals("Quiet / Shy")) ||
                (userHousehold.equals("5+ members") && catTemp2.equals("Active / Playful"))) {
            matchingTraits.append("Living situation, ");
        }

        // Other pets
        // Compare other pets with compatibility
        if ((userOtherPets.equals("Yes") && catPetsCompatibility.contains("Yes")) ||
                (userOtherPets.equals("No") && catPetsCompatibility.contains("No"))) {
            matchingTraits.append("Other pets, ");
        }

        // Social attitude
        if (userTempSocial.equals(catTemp1)) {
            matchingTraits.append("Social attitude, ");
        }

        // Energy level
        if (userTempEnergy.equals(catTemp2)) {
            matchingTraits.append("Energy level, ");
        }

        // If no traits match, return a message indicating no matches
        if (matchingTraits.length() == 0) {
            return "No matching traits found.";
        }

        // Remove the trailing comma and space, and return the string with the prefix
        matchingTraits.setLength(matchingTraits.length() - 2); // Remove last ", "
        return "Compatible in: " + matchingTraits.toString();
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



    @Override
    public int getItemCount() {
        return scheduledApps.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView userImage;
        TextView applicant, date, matching, percentage, catSelect;

        public ViewHolder(View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.imageView1);
            applicant = itemView.findViewById(R.id.applicant);
            date = itemView.findViewById(R.id.date);
            matching = itemView.findViewById(R.id.traits);
            percentage = itemView.findViewById(R.id.percentage);
            catSelect = itemView.findViewById(R.id.selectedCat);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(HashMap<String, String> currentApp);
    }
}

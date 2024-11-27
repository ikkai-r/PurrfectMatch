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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class PendingAppAdapter extends RecyclerView.Adapter<PendingAppAdapter.ViewHolder> {

    private List<ApplicationData> pendingApps;
    private Context context;
    private boolean selectedCat;
    private OnItemClickListener listener;
    HashMap<String, String> userFields = new HashMap<>();
    HashMap<String, String> catFields = new HashMap<>();
    HashMap<String, String> appFields = new HashMap<>();
    Intent i;

    public PendingAppAdapter(List<ApplicationData> pendingApps, boolean selectedCat, Context context) {
        this.pendingApps = pendingApps;
        this.selectedCat = selectedCat;
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
        ApplicationData app = pendingApps.get(position);
        Log.d("binding", app.getApplicationId());

        if (app != null) {

            // Display the application date
            holder.date.setText("Date: " + formatFirebaseTimestamp(app.getApplicationDate()));

            // Create HashMaps to store the applicant and cat data (only strings)
            HashMap<String, String> userFields = new HashMap<>();
            HashMap<String, String> catFields = new HashMap<>();

            // Fetch applicant data directly from Firestore
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Users").document(app.getUserId()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {

                for (String key : documentSnapshot.getData().keySet()) {
                    Object value = documentSnapshot.get(key);
                    if (value instanceof String) {
                        userFields.put(key, (String) value);
                    } else if (value instanceof Number) {
                        userFields.put(key, String.valueOf(value));  // Convert number to String
                    }
                }

                    }
                })
                .addOnFailureListener(e -> Log.e("fetchApplicantData", "Error fetching applicant data", e));

            // Fetch cat data directly from Firestore
            db.collection("Cats").document(app.getCatId()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {

                        for (String key : documentSnapshot.getData().keySet()) {
                            Object value = documentSnapshot.get(key);
                            if (value instanceof String) {
                                catFields.put(key, (String) value);
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("fetchCatData", "Error fetching cat data", e));

                // Extract relevant user and cat data from the userFields and catFields HashMaps
           fetchApplicantandCatData(app.getUserId(), app.getCatId(), holder);

            holder.itemView.setOnClickListener(view -> {
                if (listener != null) {
                    listener.onItemClick(app);
                }

                // Store application details for intent
                HashMap<String, String> appFields = new HashMap<>();
                appFields.put("appDate", formatFirebaseTimestamp(app.getApplicationDate()));
                appFields.put("appId", app.getApplicationId());
                appFields.put("appStatus", app.getStatus());
                appFields.put("appReason", app.getReason());

                String userHousehold = userFields.get("householdMembers");
                String userOtherPets = userFields.get("otherPets");
                String catCompatibility = catFields.get("compatibleWith");
                String userTempSocial = userFields.get("preferences1");
                String catTemp1 = catFields.get("temperament1");
                String userTempEnergy = userFields.get("preferences2");
                String catTemp2 = catFields.get("temperament2");
                String ageString = userFields.get("age");
                int age = (ageString != null) ? Integer.parseInt(ageString) : 0;

                // // Call the methods using the extracted values
                 String matchingTraits = getMatchingTraits(userHousehold, userOtherPets, catCompatibility, userTempSocial, catTemp1, userTempEnergy, catTemp2);
                 double matchingPercentage = calculateMatchPercentage(userHousehold, userOtherPets, catCompatibility, userTempSocial, catTemp1, userTempEnergy, catTemp2, age);

                catFields.put("percentage", String.format("%.2f", matchingPercentage));

                Intent intent = new Intent(context, PendingAppView.class);
                intent.putExtra("app", appFields);  // Pass the application object via Intent
                intent.putExtra("cat", catFields);  // Pass the cat data via Intent
                intent.putExtra("user", userFields);

                Log.d("pabe", appFields.toString());
                Log.d("pabe", catFields.toString());
                Log.d("pabe", userFields.toString());
                Log.d("pabe", String.valueOf(age));
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
                            Glide.with(context)
                                    .load(document.getString("profileimg")) // Load the URI/URL
                                    .into(holder.userImage);

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

                                                userFields.put("name", userName);
                                                userFields.put("age", String.valueOf(userAge));
                                                userFields.put("profileimg", document.getString("profileimg"));
                                                userFields.put("householdMembers", userHousehold);
                                                userFields.put("otherPets", userOtherPets);
                                                userFields.put("gender", document.getString("gender"));
                                                userFields.put("preferences1", userTempSocial);
                                                userFields.put("preferences2", userTempEnergy);
                                                userFields.put("address2", document.getString("city") + ", " + document.getString("region"));

                                                catFields.put("catId", catDocument.getId());
                                                catFields.put("catTemp1", catTemp1);
                                                catFields.put("catTemp2", catTemp2);
                                                catFields.put("catCompatibility", catCompatibility);
                                                catFields.put("catName", catDocument.getString("name"));
                                                catFields.put("percentage", String.format("%.2f", matchingPercentage));

                                                if (selectedCat) {
                                                    holder.catSelect.setVisibility(View.VISIBLE); // Make the TextView visible
                                                    holder.catSelect.setText("Selected Cat: " + catDocument.getString("name"));
                                                } else {
                                                    holder.catSelect.setVisibility(View.GONE); // Hide the TextView
                                                }
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
        return pendingApps.size();
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
        void onItemClick(ApplicationData currentCat);
    }
}

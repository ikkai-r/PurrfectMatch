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

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class PendingAppAdapter extends RecyclerView.Adapter<PendingAppAdapter.ViewHolder> {

    private List<ApplicationData> pendingApps;
    private Context context;
    private OnItemClickListener listener;

    public PendingAppAdapter(List<ApplicationData> pendingApps, Context context) {
        this.pendingApps = pendingApps;
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

        // Get name from db using user id in app
        holder.userImage.setImageResource(R.drawable.user_1); // Replace with actual image
        holder.date.setText("Date: " + app.getApplicationDate());

        // Fetch both applicant and cat data and display matching traits
        fetchApplicantandCatData(app.getUserId(), app.getCatId(), holder);

        holder.itemView.setOnClickListener(view -> {
            if (listener != null) {
                listener.onItemClick(app);
            }

            // Create an Intent to navigate to the specific application's page for the current cat
            Intent intent = new Intent(context, PendingApplicationsSpecific.class);
            intent.putExtra("app", app);  // Pass the application object via Intent
            context.startActivity(intent);  // Start the new activity
        });
    }

    private void fetchApplicantandCatData(String userId, String catId, final ViewHolder holder) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Fetch user data
        db.collection("Users").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String userName = document.getString("name");
                            String userHousehold = document.getString("householdDynamics");
                            String userOtherPets = document.getString("otherPets");
                            String userTempSocial = document.getString("preferredTempSocial");
                            String userTempEnergy = document.getString("preferredTempEnergy");

                            // Set applicant's name
                            holder.applicant.setText("Applicant: " + userName);

                            // Fetch cat data
                            db.collection("Cats").document(catId).get()
                                    .addOnCompleteListener(catTask -> {
                                        if (catTask.isSuccessful()) {
                                            DocumentSnapshot catDocument = catTask.getResult();
                                            if (catDocument.exists()) {
                                                String catName = catDocument.getString("name");
                                                String catSize = catDocument.getString("size");
                                                String catTemp1 = catDocument.getString("temperament1");
                                                String catTemp2 = catDocument.getString("temperament2");
                                                String catCompatibility = catDocument.getString("petsCompatibility");

                                                // Calculate the matching traits and percentage
                                                int matchingTraits = getMatchingTraits(userHousehold, catSize, userOtherPets, catCompatibility, userTempSocial, catTemp1, userTempEnergy, catTemp2);
                                                double matchingPercentage = calculateMatchingPercentage(matchingTraits);

                                                // Display matching traits and percentage
                                                holder.matching.setText("Matching traits: " + matchingTraits);
                                                holder.percentage.setText("Matching percentage: " + matchingPercentage + "%");
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

    // Method to calculate matching traits
    public int getMatchingTraits(String userHousehold, String catSize,
                                 String userOtherPets, String catPetsCompatibility,
                                 String userTempSocial, String catTemp1,
                                 String userTempEnergy, String catTemp2) {
        int matches = 0;

        // Compare household dynamics with cat size
        if (userHousehold.equals("2-4 members") && catSize.equals("medium")) {
            matches++;
        }

        // Compare other pets with compatibility
        if ((userOtherPets.equals("Yes") && catPetsCompatibility.contains("Yes")) ||
                (userOtherPets.equals("No") && catPetsCompatibility.contains("No"))) {
            matches++;
        }

        // Compare temperament (social)
        if (userTempSocial.equals(catTemp1)) {
            matches++;
        }

        // Compare temperament (energy)
        if (userTempEnergy.equals(catTemp2)) {
            matches++;
        }

        return matches;
    }

    // Calculate the matching percentage based on the number of matching traits
    public double calculateMatchingPercentage(int matchingTraits) {
        // Assuming there are 4 traits to compare, we calculate the percentage
        return (matchingTraits / 4.0) * 100;
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
        TextView applicant, date, matching, percentage;

        public ViewHolder(View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.imageView1);
            applicant = itemView.findViewById(R.id.applicant);
            date = itemView.findViewById(R.id.date);
            matching = itemView.findViewById(R.id.traits);
            percentage = itemView.findViewById(R.id.percentage);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(ApplicationData currentCat);
    }
}

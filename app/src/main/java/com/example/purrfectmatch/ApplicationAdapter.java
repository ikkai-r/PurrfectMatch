package com.example.purrfectmatch;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.ViewHolder> {

    private final List<ApplicationData> applicationList;
    private final Context context;
    private final boolean isActive;

    private OnItemClickListener listener;
    private FirebaseFirestore db;

    public interface OnItemClickListener {
        void onActionClicked(ApplicationData applicationData);
    }

    public ApplicationAdapter(boolean isActive, List<ApplicationData> applicationList, Context context) {
        this.applicationList = applicationList;
        this.context = context;
        this.isActive = isActive;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_application_details, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ApplicationData application = applicationList.get(position);

        if (isActive) {
            setupActiveApplicationCard(holder, application);
        } else {
            setupClosedApplicationCard(holder, application);
        }

        holder.bind(application, listener); // Pass the listener to the ViewHolder
    }

    private void setupActiveApplicationCard(@NonNull ViewHolder holder, ApplicationData application) {
        fetchCatDetails(application.getCatId(), holder);

        // Schedule button visibility and setup
        String status = application.getStatus();
        switch (status) {
            case "reviewed":
                holder.titleSmall.setText("Application Reviewed");
                holder.titleSmall.setTextColor(Color.parseColor("#AD762E"));
                holder.textBig.setText("Action needed: Find a date and time where you can meet this cat");
                holder.actionBtn.setVisibility(View.VISIBLE);
                break;

            case "pending":
                holder.titleSmall.setText("Application Pending");
                holder.titleSmall.setTextColor(Color.parseColor("#AD762E"));
                holder.textBig.setText("Shelter has yet to review your application");
                holder.actionBtn.setVisibility(View.GONE);
                break;

            case "scheduled":
                String finalDate = application.getFinalDate();
                String finalTime = application.getFinalTime();
                holder.titleSmall.setText("Meeting Scheduled: " + finalDate + ", " + finalTime);
                holder.titleSmall.setTextColor(Color.parseColor("#AD762E"));
                holder.textBig.setText("Kindly wait for any application updates");
                holder.actionBtn.setVisibility(View.GONE);
                break;
        }
    }

    private void setupClosedApplicationCard(@NonNull ViewHolder holder, ApplicationData application) {
        fetchCatDetails(application.getCatId(), holder);
        holder.actionBtn.setVisibility(View.GONE);

        String feedback = application.getFeedback();
        if (feedback == null || feedback.trim().isEmpty()) {
            feedback = "N/A";
        }

        holder.textBig.setText("Reason: " + feedback);

        if ("accepted".equalsIgnoreCase(application.getStatus())) {
            holder.titleSmall.setText("Accepted");
            holder.titleSmall.setTextColor(Color.parseColor("#2DC244"));
        } else if ("rejected".equalsIgnoreCase(application.getStatus())) {
            holder.titleSmall.setText("Rejected");
            holder.titleSmall.setTextColor(Color.parseColor("#A33B3B"));
        }
    }

    private void fetchCatDetails(String catId, ViewHolder holder) {
        if (db == null) {
            db = FirebaseFirestore.getInstance();
        }

        db.collection("Cats").document(catId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String catImageStr = documentSnapshot.getString("catImage");
                String catName = documentSnapshot.getString("name");

                // Set the cat image
                if (catImageStr != null && !catImageStr.isEmpty()) {
                    Glide.with(holder.itemView.getContext()).load(catImageStr).into(holder.catImage);
                } else {
                    holder.catImage.setImageResource(R.drawable.app_icon); // default image
                }

                holder.titleBig.setText("For cat: " + (catName != null ? catName : "Unknown Cat"));
            }
        }).addOnFailureListener(e -> holder.catImage.setImageResource(R.drawable.app_icon)); // Default on failure
    }

    @Override
    public int getItemCount() {
        return applicationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleBig, titleSmall, textBig, actionBtn;
        public ImageView catImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleBig = itemView.findViewById(R.id.titleBig);
            titleSmall = itemView.findViewById(R.id.titleSmall);
            textBig = itemView.findViewById(R.id.textBig);
            actionBtn = itemView.findViewById(R.id.actionBtn);
            catImage = itemView.findViewById(R.id.image);
        }

        public void bind(ApplicationData applicationData, OnItemClickListener listener) {
            actionBtn.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onActionClicked(applicationData);
                }
            });
        }
    }
}

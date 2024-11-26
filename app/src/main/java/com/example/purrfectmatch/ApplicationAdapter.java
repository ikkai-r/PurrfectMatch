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
import com.example.purrfectmatch.ApplicationData;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.ViewHolder> {

    private List<ApplicationData> applicationList;
    private Context context;
    private OnItemClickListener listener;
    private OnItemClickListener onItemClickListener;
    private boolean isActive;

    private FirebaseFirestore db;

    public interface OnItemClickListener {
        void onActionClicked(ApplicationData applicationData);
    }

    public ApplicationAdapter(boolean isActive, List<ApplicationData> applicationList, Context context) {
        this.applicationList = applicationList;
        this.context = context;
        this.isActive = isActive;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_application_details, parent, false);
        return new ViewHolder(view);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
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
            // Attach the listener to the action button
            actionBtn.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onActionClicked(applicationData);
                }
            });
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ApplicationData application = applicationList.get(position);

        if (isActive) {
            setupActiveApplicationCard(holder, application);
        } else {
            setupClosedApplicationCard(holder, application);
        }

        holder.bind(application, listener);
    }

    private void setupActiveApplicationCard(@NonNull ViewHolder holder, ApplicationData application) {
        fetchCatDetails(application.getCatId(), holder);

        //  Schedule button visibility
        if (application.getStatus().equals("reviewed")) {
            holder.titleSmall.setText("Application Reviewed");
            holder.titleSmall.setTextColor(Color.parseColor("#AD762E"));
            holder.textBig.setText("Action needed: Find a date and time where you can meet this cat");
            holder.actionBtn.setVisibility(View.VISIBLE);
        }
        else if (application.getStatus().equals("pending")){
            holder.titleSmall.setText("Application Pending");
            holder.titleSmall.setTextColor(Color.parseColor("#AD762E"));
            holder.textBig.setText("Shelter has yet to review your application");
            holder.actionBtn.setVisibility(View.GONE);
        }
        //TODO: Add agreed upon schedule
        else if (application.getStatus().equals("scheduled")){
            holder.titleSmall.setText("Meeting Scheduled: <date>");
            holder.titleSmall.setTextColor(Color.parseColor("#AD762E"));
            holder.textBig.setText("Kindly wait for any application updates");
            holder.actionBtn.setVisibility(View.GONE);
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

        if (application.getStatus().equals("accepted")) {
            holder.titleSmall.setText("Accepted");
            holder.titleSmall.setTextColor(Color.parseColor("#2DC244"));
        }
        else if (application.getStatus().equals("rejected")){
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
                    int catImageResId = holder.itemView.getContext().getResources()
                            .getIdentifier(catImageStr, "drawable", holder.itemView.getContext().getPackageName());
                    holder.catImage.setImageResource(catImageResId);
                } else {
                    holder.catImage.setImageResource(R.drawable.app_icon); // default image
                }

                holder.titleBig.setText("For cat: " + (catName != null ? catName : "Unknown Cat"));
            }
        }).addOnFailureListener(e -> {
            holder.catImage.setImageResource(R.drawable.app_icon); // default
        });
    }


    @Override
    public int getItemCount() {
        return applicationList.size();
    }
}

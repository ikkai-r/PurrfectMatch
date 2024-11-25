package com.example.purrfectmatch;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
public class PendingAppHomeAdapter extends RecyclerView.Adapter<PendingAppHomeAdapter.ViewHolder> {

    private List<Cat> catsWithPendingApps;
    private Context context;
    private OnItemClickListener listener;

    public PendingAppHomeAdapter(List<Cat> catsWithPendingApps, Context context) {
        this.catsWithPendingApps = catsWithPendingApps;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate your pending application row layout
        View view = LayoutInflater.from(context).inflate(R.layout.home_pending_app_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Cat currentCat = catsWithPendingApps.get(position);
        Log.d("binding", currentCat.getName());

        // Populate the views with data from PendingAppData
        holder.catImage.setImageResource(R.drawable.dweety); // Replace with actual image
        holder.catName.setText(currentCat.getName());

        // handle the notification badge
        // count how many pending apps length and set text
        holder.badgeText.setText(String.valueOf(currentCat.getPendingApplications().size()));

        holder.itemView.setOnClickListener(view -> {
            if (listener != null) {
                listener.onItemClick(currentCat);
            }

            // Create an Intent to navigate to the specific application's page for the current cat
            Intent intent = new Intent(context, PendingApplicationsSpecific.class);
            intent.putExtra("cat", currentCat);  // Pass the cat object via Intent
            context.startActivity(intent);  // Start the new activity
        });
    }

    @Override
    public int getItemCount() {
        return catsWithPendingApps.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView catImage;
        TextView catName, badgeText;
        View notificationBadge;

        public ViewHolder(View itemView) {
            super(itemView);
            // Link views from pending_app_row.xml
            catImage = itemView.findViewById(R.id.imageView5);
            catName = itemView.findViewById(R.id.catName);
            badgeText = itemView.findViewById(R.id.notifPendingApp);
            notificationBadge = itemView.findViewById(R.id.notificationBadge1);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Cat currentCat);
    }
}

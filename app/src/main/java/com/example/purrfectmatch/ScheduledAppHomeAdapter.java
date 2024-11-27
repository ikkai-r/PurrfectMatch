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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.io.Serializable;

public class ScheduledAppHomeAdapter extends RecyclerView.Adapter<ScheduledAppHomeAdapter.ViewHolder> {

    private List<HashMap<String, String>> scheduledAppList;
    private Context context;
    private OnItemClickListener listener;

    public ScheduledAppHomeAdapter(List<HashMap<String, String>> scheduledAppList, Context context) {
        this.scheduledAppList = scheduledAppList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate your pending application row layout
        View view = LayoutInflater.from(context).inflate(R.layout.scheduled_app_home, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HashMap<String, String> currentApp = scheduledAppList.get(position);
        Log.d("scheduled", currentApp.toString());

        // Populate the views with data from ScheduledAppData
        holder.userImage.setImageResource(R.drawable.user_1); // Replace with actual image
        holder.userSched.setText(currentApp.get("userSched"));
        holder.dateSched.setText(formatDate(currentApp.get("finalDate")));
        holder.itemView.setOnClickListener(view -> {
            if (listener != null) {
                listener.onItemClick(currentApp);
            }


            Log.d("currentApp scheduled", currentApp.toString());

            //Create an Intent to navigate to the specific application's page for the current cat
            Intent intent = new Intent(context, ScheduledApplications.class);
            intent.putExtra("app", currentApp);  // Pass the app object via Intent
            context.startActivity(intent);
        });
    }

    private String formatDate(String inputDate) {
        // Create a SimpleDateFormat for the input format
        Date date = null;
        SimpleDateFormat outputFormat = null;
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");

            // Parse the input string into a Date object
            date = inputFormat.parse(inputDate);

            // Create a SimpleDateFormat for the desired output format
            outputFormat = new SimpleDateFormat("MMMM dd, yyyy");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return outputFormat.format(date);
    }

    @Override
    public int getItemCount() {
        return scheduledAppList.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView userImage;
        TextView userSched, dateSched;

        public ViewHolder(View itemView) {
            super(itemView);
            // Link views from pending_app_row.xml
            userImage = itemView.findViewById(R.id.imageView6);
            userSched = itemView.findViewById(R.id.userSched);
            dateSched = itemView.findViewById(R.id.dateSched);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(HashMap<String, String> currentApp);
    }
}

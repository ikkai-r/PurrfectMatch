package com.example.purrfectmatch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.purrfectmatch.ApplicationData;
import java.util.List;

public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.ViewHolder> {

    private List<ApplicationData> applicationList;
    private Context context;
    private OnItemClickListener listener;
    private OnItemClickListener onItemClickListener;
    private boolean isActive;

    public interface OnItemClickListener {
        void onActionClicked(ApplicationData applicationData);
    }

    public ApplicationAdapter(boolean isActive, List<ApplicationData> applicationList, Context context) {
        this.applicationList = applicationList;
        this.context = context;
        this.isActive = isActive;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleBig, titleSmall, textBig, actionBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleBig = itemView.findViewById(R.id.titleBig);
            titleSmall = itemView.findViewById(R.id.titleSmall);
            textBig = itemView.findViewById(R.id.textBig);
            actionBtn = itemView.findViewById(R.id.actionBtn);
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

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_application_details, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ApplicationData application = applicationList.get(position);
        holder.titleBig.setText(application.getStatus());
        holder.titleSmall.setText("Test");
        holder.textBig.setText(application.getReason());
        holder.actionBtn.setText("Test");

        holder.bind(application, listener);
    }

    @Override
    public int getItemCount() {
        return applicationList.size();
    }
}

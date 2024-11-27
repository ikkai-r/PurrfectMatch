package com.example.purrfectmatch;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ExploreAdapter extends RecyclerView.Adapter<ExploreAdapter.ViewHolder> {

    private List<ExploreData> exploreList;
    private Context context;
    private OnItemClickListener listener;

    public ExploreAdapter(List<ExploreData> exploreList, Context context) {
        this.exploreList = exploreList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_cat_details, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExploreData currentCat = exploreList.get(position);

        Glide.with(context)
                .load(currentCat.getCatImage()) // Load the URI/URL
                .into(holder.catImage);

        //holder.catImage.setImageResource(currentCat.getCatImage());
        //Log.d("Test", "HERE2" + currentCat.getCatImage());
        holder.catName.setText(currentCat.getName());
        holder.catAge.setText(currentCat.getAge() + " months old");
        holder.catBreed.setText(currentCat.getBreed());

        // Check if the cat is female/male and update text and color accordingly
        if (currentCat.getSex().equals("F")) {
            holder.catSex.setText("♀");
            holder.catSex.setTextColor(ColorStateList.valueOf(0xFFEC3B8B));
        } else {
            holder.catSex.setText("♂");
            holder.catSex.setTextColor(ColorStateList.valueOf(0xFF0000FF));
        }

        // Check if the cat is neutered and update text and background color accordingly
        if (currentCat.getIsNeutered()) {
            holder.catNeutered.setText("Neutered");
            holder.catNeutered.setBackgroundTintList(ColorStateList.valueOf(0xFFAAE09B));
        } else {
            holder.catNeutered.setText("Not Neutered");
            holder.catNeutered.setBackgroundTintList(ColorStateList.valueOf(0xFFE96D6D));
        }

        holder.itemView.setOnClickListener(view -> {
            if (listener != null) {
                listener.onItemClick(currentCat);
            }
//            Toast.makeText(context, "Clicked on: " + currentCat.getName() + " " +
//                        "(ID: " + currentCat.getId() + ")", Toast.LENGTH_SHORT).show();
        });
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return exploreList.size();
    }

    public void updateList(List<ExploreData> newExploreList) {
        this.exploreList = newExploreList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView catImage;
        TextView catName, catAge, catSex, catNeutered, catBreed;

        public ViewHolder(View itemView) {
            super(itemView);
            catImage = itemView.findViewById(R.id.catImage);
            catName = itemView.findViewById(R.id.catName);
            catAge = itemView.findViewById(R.id.catAge);
            catSex = itemView.findViewById(R.id.catSex);
            catNeutered = itemView.findViewById(R.id.catNeutered);
            catBreed = itemView.findViewById(R.id.catBreed);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(ExploreData cat);
    }
}
package com.example.purrfectmatch;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;

public class SwipeAdapter extends RecyclerView.Adapter<SwipeAdapter.ViewHolder>{


    SwipeData[] catDataItemArrayList;
    Context context;
    boolean flip;
    int imageIndex = 0;

    public SwipeAdapter(SwipeData[] catDataItemArrayList, SwipeActivity activity) {
        this.catDataItemArrayList = catDataItemArrayList;
        this.context = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.swipe_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        SwipeData swipeDataItem = catDataItemArrayList[position];

        holder.catImage.setImageResource(swipeDataItem.catImages[0]);

        holder.catImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageIndex = (imageIndex + 1) % swipeDataItem.catImages.length;
                holder.catImage.setImageResource(swipeDataItem.catImages[imageIndex]);
            }
        });

        holder.neuteredImage.setImageResource(swipeDataItem.neuteredImage);
        holder.vaccinatedImage.setImageResource(swipeDataItem.vaccinationImage);
        holder.litterTrainedImage.setImageResource(swipeDataItem.litterTrainedImage);

        holder.ageText.setText(String.valueOf(swipeDataItem.age) + "months");
        holder.weightText.setText(String.valueOf(swipeDataItem.weight) + "lbs");
        holder.sexText.setText(String.valueOf(swipeDataItem.sex));
        holder.breedText.setText(swipeDataItem.breed);
        holder.birthdayText.setText(swipeDataItem.birthday);
        holder.temperamentText.setText(swipeDataItem.temperament);
        holder.bioText.setText(swipeDataItem.bio);
        holder.medicalHistoryText.setText(swipeDataItem.medicalHistory);
        holder.adoptionFeeText.setText(String.valueOf(swipeDataItem.adoptionFee));
        holder.contactInformationText.setText(swipeDataItem.contactInformation);
        holder.nameText.setText(swipeDataItem.name);

        if (flip) {
            holder.scrollView.setRotationY(180);
        } else {
            // Reset rotation if not flipped
            holder.scrollView.setRotationY(0);
        }


    }

    public void setFlip(boolean b){
        this.flip = b;
    }

    @Override
    public int getItemCount() {
        return catDataItemArrayList.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView catImage, neuteredImage, vaccinatedImage, litterTrainedImage;
        TextView ageText, weightText, sexText, breedText, birthdayText, temperamentText, bioText, medicalHistoryText, adoptionFeeText, contactInformationText, nameText;
        ScrollView scrollView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            catImage = itemView.findViewById(R.id.catPic);
            neuteredImage = itemView.findViewById(R.id.neuteredImageView);
            vaccinatedImage = itemView.findViewById(R.id.vaccinatedImageView);
            litterTrainedImage = itemView.findViewById(R.id.litterTrainedImageView);

            ageText = itemView.findViewById(R.id.ageText);
            weightText = itemView.findViewById(R.id.weightText);
            sexText = itemView.findViewById(R.id.sexText);
            breedText = itemView.findViewById(R.id.breedText);
            birthdayText = itemView.findViewById(R.id.birthdayText);
            temperamentText = itemView.findViewById(R.id.tempermentText);
            bioText = itemView.findViewById(R.id.bioText);
            medicalHistoryText = itemView.findViewById(R.id.medicalHistoryText);
            adoptionFeeText = itemView.findViewById(R.id.adoptionFeeText);
            contactInformationText = itemView.findViewById(R.id.contactInformationText);
            nameText = itemView.findViewById(R.id.nameText);

            scrollView = itemView.findViewById(R.id.scrollView2);
        }

    }
}

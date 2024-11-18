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
import android.graphics.PorterDuff;

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


    if (swipeDataItem.isBookmarked) {
        holder.bookmarkIcon.setColorFilter(0xFFCE0000, PorterDuff.Mode.SRC_IN);
    } else {
        holder.bookmarkIcon.setColorFilter(0xFF808080, PorterDuff.Mode.SRC_IN); 
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

    ImageView catImage, bookmarkIcon;
    TextView ageText, weightText, sexText, breedText, birthdayText, temperamentText, bioText, medicalHistoryText, adoptionFeeText, contactInformationText, nameText;
    ScrollView scrollView;


    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        catImage = itemView.findViewById(R.id.catPic);
        
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
        bookmarkIcon = itemView.findViewById(R.id.bookmarkIcon); 
    }

}
}

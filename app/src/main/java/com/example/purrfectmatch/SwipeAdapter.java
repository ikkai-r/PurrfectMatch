package com.example.purrfectmatch;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

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

        holder.catImage.setImageResource(swipeDataItem.catImage);
        holder.ageText.setText(String.valueOf(swipeDataItem.age) + " months");
        holder.weightText.setText(String.valueOf(swipeDataItem.weight) + "lbs");
        holder.sexText.setText(String.valueOf(swipeDataItem.sex));
        holder.breedText.setText(swipeDataItem.breed);
        if(swipeDataItem.isNeutered == true) { holder.neuterText.setText("Neutered");}
        else {  holder.neuterText.setText("Not neutered"); }
        holder.temperamentText.setText(swipeDataItem.temperament);
        holder.bioText.setText(swipeDataItem.bio);
        holder.compatibleWithText.setText(swipeDataItem.compatibleWith);
        holder.adoptionFeeText.setText(String.valueOf(swipeDataItem.adoptionFee));
        holder.contactInformationText.setText(swipeDataItem.contactInformation);
        holder.nameText.setText(swipeDataItem.name);

        if (flip) {
            holder.scrollView.setRotationY(180);
        } else {
            holder.scrollView.setRotationY(0);
        }

        if (swipeDataItem.isBookmarked) {
            holder.bookmarkIcon.setColorFilter(0xFFFE327F, PorterDuff.Mode.SRC_IN);
        } else {
            holder.bookmarkIcon.setColorFilter(0xFF808080, PorterDuff.Mode.SRC_IN); 
        }

        holder.bookmarkIcon.setOnClickListener(v -> {
            swipeDataItem.isBookmarked = !swipeDataItem.isBookmarked;
            notifyItemChanged(position);

            // need to make this about THE USER
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = auth.getCurrentUser();

            String userId = currentUser.getUid();
            String catId = swipeDataItem.catId;

            if (swipeDataItem.isBookmarked) {
                db.collection("Cats")
                    .document(catId)
                    .update("bookmarkedBy", FieldValue.arrayUnion(userId))
                    .addOnSuccessListener(aVoid -> {
                        db.collection("Users")
                            .document(userId)
                            .update("bookmarkedCats", FieldValue.arrayUnion(catId))
                            .addOnSuccessListener(innerVoid -> {
                                notifyItemChanged(position); // Update UI
                            })
                            .addOnFailureListener(e -> {
                                Log.e("Error", "Failed to update bookmarkedCats in Users", e);
                            });
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Error", "Failed to update bookmarkedBy in Cats", e);
                    });
            } else {
                db.collection("Cats")
                    .document(catId)
                    .update("bookmarkedBy", FieldValue.arrayRemove(userId))
                    .addOnSuccessListener(aVoid -> {
                        db.collection("Users")
                            .document(userId)
                            .update("bookmarkedCats", FieldValue.arrayRemove(catId))
                            .addOnSuccessListener(innerVoid -> {
                                notifyItemChanged(position); // Update UI
                            })
                            .addOnFailureListener(e -> {
                                Log.e("Error", "Failed to update bookmarkedCats in Users", e);
                            });
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Error", "Failed to update bookmarkedBy in Cats", e);
                    });
            }
        });
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
        TextView ageText, weightText, sexText, breedText, neuterText, temperamentText, bioText, compatibleWithText, adoptionFeeText, contactInformationText, nameText;
        ScrollView scrollView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            catImage = itemView.findViewById(R.id.catPic);
            
            ageText = itemView.findViewById(R.id.ageText);
            weightText = itemView.findViewById(R.id.weightText);
            sexText = itemView.findViewById(R.id.sexText);
            breedText = itemView.findViewById(R.id.breedText);
            neuterText = itemView.findViewById(R.id.neuterText);
            temperamentText = itemView.findViewById(R.id.tempermentText);
            bioText = itemView.findViewById(R.id.bioText);
            compatibleWithText = itemView.findViewById(R.id.compatibleWithText);
            adoptionFeeText = itemView.findViewById(R.id.adoptionFeeText);
            contactInformationText = itemView.findViewById(R.id.contactInformationText);
            nameText = itemView.findViewById(R.id.nameText);

            scrollView = itemView.findViewById(R.id.scrollView2);
            bookmarkIcon = itemView.findViewById(R.id.bookmarkIcon); 
        }
    }

    public void updateData(SwipeData[] newData) {
        this.catDataItemArrayList = newData;
        notifyDataSetChanged();
    }
}
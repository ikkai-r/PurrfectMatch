package com.example.purrfectmatch;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;


import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.view.ViewGroup;

public class ClickedExploreActivity extends AppCompatActivity{

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ImageView profile, explore, swipe, catImage, bookmarkIcon;
    private TextView ageText, weightText, sexText, breedText, neuterText, temperamentText, bioText,
            compatibleWithText, adoptionFeeText, contactInformationText, nameText;
    private String documentId;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_explore_clicked);

        profile = findViewById(R.id.profile);
        explore = findViewById(R.id.explore);
        swipe = findViewById(R.id.swipe);

        catImage = findViewById(R.id.catPic);
        ageText = findViewById(R.id.ageText);
        weightText = findViewById(R.id.weightText);
        sexText = findViewById(R.id.sexText);
        breedText = findViewById(R.id.breedText);
        neuterText = findViewById(R.id.neuterText);
        temperamentText = findViewById(R.id.tempermentText);
        bioText = findViewById(R.id.bioText);
        compatibleWithText = findViewById(R.id.compatibleWithText);
        adoptionFeeText = findViewById(R.id.adoptionFeeText);
        contactInformationText = findViewById(R.id.contactInformationText);
        nameText = findViewById(R.id.nameText);
        scrollView = findViewById(R.id.scrollView2);
        bookmarkIcon = findViewById(R.id.bookmarkIcon);

        loadCatData();

        Intent i = getIntent();
        documentId = i.getStringExtra("documentId");

        profile.setOnClickListener(view -> {
            Intent newIntent = new Intent(this, ProfileActivity.class);
            startActivity(newIntent);
            finish();
        });

        swipe.setOnClickListener(view -> {
            Intent newIntent = new Intent(this, SwipeActivity.class);
            startActivity(newIntent);
            finish();
        });

        explore.setOnClickListener(view -> {
            onBackPressed();
        });
    }

    private void loadCatData() {
        // Assuming 'catId' is the document ID passed to this activity
        String catId = getIntent().getStringExtra("documentId");  // Get the document ID from the intent

        if (catId != null) {
            // Get a reference to the specific cat document by its ID
            DocumentReference catRef = db.collection("Cats").document(catId);

            catRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Create the SwipeData using the document data
                        SwipeData swipeDataItem = createSwipeDataFromDocument(document, catId);

                        //TODO: Change to actual cat images
                        catImage.setImageResource(R.drawable.cat1);

                        ageText.setText(String.valueOf(swipeDataItem.age) + "months");
                        weightText.setText(String.valueOf(swipeDataItem.weight) + "lbs");
                        sexText.setText(String.valueOf(swipeDataItem.sex));
                        breedText.setText(swipeDataItem.breed);
                        if(swipeDataItem.isNeutered == true) { neuterText.setText("Neutered");}
                        else {  neuterText.setText("Not neutered"); }
                        temperamentText.setText(swipeDataItem.temperament);
                        bioText.setText(swipeDataItem.bio);
                        compatibleWithText.setText(swipeDataItem.compatibleWith);
                        adoptionFeeText.setText(String.valueOf(swipeDataItem.adoptionFee));
                        contactInformationText.setText(swipeDataItem.contactInformation);
                        nameText.setText(swipeDataItem.name);

                        if (swipeDataItem.isBookmarked) {
                            bookmarkIcon.setColorFilter(0xFFFE327F, PorterDuff.Mode.SRC_IN);
                        } else {
                            bookmarkIcon.setColorFilter(0xFF808080, PorterDuff.Mode.SRC_IN);
                        }
//                        // Set the page transformer for the view pager
//                        viewPager2.setPageTransformer((page, position) -> {
//                            float absPos = Math.abs(position);
//                            page.setAlpha(1.0f - absPos);
//                            page.setScaleY(1.0f - absPos * 0.15f);
//                        });
                    } else {
                        Toast.makeText(this, "Cat document not found.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Failed to load data.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "No cat ID passed.", Toast.LENGTH_SHORT).show();
        }
    }

    private SwipeData createSwipeDataFromDocument(DocumentSnapshot document, String catId) {
        // Extract the fields from the Firestore document
        int age = document.getLong("age").intValue();
        int weight = document.getLong("weight").intValue();
        int adoptionFee = document.getLong("adoptionFee").intValue();

        // Retrieve the list of image names and convert them to resource IDs
        List<String> catImages = (List<String>) document.get("catImages");
        int[] catPicSet = new int[catImages.size()];
        for (int i = 0; i < catImages.size(); i++) {
            catPicSet[i] = getResources().getIdentifier(catImages.get(i), "drawable", getPackageName());
        }

        // Retrieve other information
        char sex = document.getString("sex").charAt(0);
        String foodPreference = document.getString("foodPreference");
        String bio = document.getString("bio");
        String temperament = document.getString("temperament");
        String breed = document.getString("breed");
        String name = document.getString("name");
        String contact = document.getString("contact");
        String compatibleWith = document.getString("compatibleWith");
        boolean isNeutered = document.getBoolean("isNeutered");

        // Now, pass the catId (document ID) directly into the SwipeData constructor
        return new SwipeData(age, weight, adoptionFee, R.drawable.check, R.drawable.check, R.drawable.check, catPicSet,
                sex, foodPreference, bio, temperament, breed, name,
                contact, catId, compatibleWith, isNeutered);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

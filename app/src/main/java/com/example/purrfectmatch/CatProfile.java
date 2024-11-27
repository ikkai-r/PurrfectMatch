package com.example.purrfectmatch;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class CatProfile extends AppCompatActivity {

    Dialog dialog;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ImageView catImage;
    private TextView ageText, weightText, sexText, breedText, neuterText, temperamentText, bioText,
            compatibleWithText, adoptionFeeText, contactInformationText, nameText;
    private String catId, catName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cat_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_delete_cat);

        initializeViews();

        Intent i = getIntent();
        catId = i.getStringExtra("catId");

        loadCatData();

        Button deleteButton = dialog.findViewById(R.id.dialog_delete_button);
        Button cancelButton = dialog.findViewById(R.id.dialog_cancel_button);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform the delete action here
                if (catId != null && !catId.isEmpty()) {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("Cats")
                            .document(catId)
                            .delete();
                }

                Intent i = new Intent(CatProfile.this, SuccessForm.class);
                i.putExtra("title", "Profile successfully deleted:");
                i.putExtra("title_big", catName);
                i.putExtra("subtitle_1", "Sad to see you go :(");
                i.putExtra("subtitle_2", "");
                i.putExtra("button_text", "Okay");
                i.putExtra("user_type", "shelter");
                // Close the dialog
                CatProfile.this.startActivity(i);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Just dismiss the dialog
                dialog.dismiss();
            }
        });
    }

    private void initializeViews() {
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
    }

    private void loadCatData() {
        if (catId != null) {
            // Get a reference to the specific cat document by its ID
            DocumentReference catRef = db.collection("Cats").document(catId);

            catRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Create the SwipeData using the document data
                        SwipeData swipeDataItem = createCatDataFromDocument(document, catId);

                        //TODO: Change to actual cat images
                        //catImage.setImageResource(R.drawable.cat1);
                        Glide.with(this)
                                .load(swipeDataItem.catImage)
                                .into(catImage);


                        ageText.setText("Age:  " + String.valueOf(swipeDataItem.age) + " months");
                        weightText.setText("Weight:  " + String.valueOf(swipeDataItem.weight) + "lbs");
                        breedText.setText("Breed:  " + swipeDataItem.breed);
                        if ("F".equals(String.valueOf(swipeDataItem.sex))) {sexText.setText("Sex:  Female");}
                        else {sexText.setText("Sex:  Male");  }
                        if(swipeDataItem.isNeutered == true) { neuterText.setText("Neutered");}
                        else {  neuterText.setText("Not neutered"); }

                        temperamentText.setText((swipeDataItem.temperament1) + ", " + (swipeDataItem.temperament2));
                        bioText.setText(swipeDataItem.bio);
                        compatibleWithText.setText(swipeDataItem.compatibleWith);

                        adoptionFeeText.setText(String.valueOf(swipeDataItem.adoptionFee) + " php");
                        contactInformationText.setText(swipeDataItem.contactInformation);

                        nameText.setText(swipeDataItem.name);
                        catName = swipeDataItem.name;
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

    private SwipeData createCatDataFromDocument(DocumentSnapshot document, String catId) {
        // Extract the fields from the Firestore document
        int age = document.getLong("age").intValue();
        int weight = document.getLong("weight").intValue();
        int adoptionFee = document.getLong("adoptionFee").intValue();

        // Retrieve the list of image names and convert them to resource IDs
        String catImage = document.getString("catImage");
        //String catImageStr = document.getString("catImage");
        //int catImage =  getResources().getIdentifier(catImageStr, "drawable", getPackageName());

        // Retrieve other information
        char sex = document.getString("sex").charAt(0);
        String foodPreference = document.getString("foodPreference");
        String bio = document.getString("bio");
        String temperament1 = document.getString("temperament1");
        String temperament2 = document.getString("temperament2");
        String breed = document.getString("breed");
        String name = document.getString("name");
        String contact = document.getString("contact");
        String compatibleWith = document.getString("compatibleWith");
        boolean isNeutered = document.getBoolean("isNeutered");

        // Now, pass the catId (document ID) directly into the SwipeData constructor
        return new SwipeData(age, weight, adoptionFee, R.drawable.check, R.drawable.check, R.drawable.check, catImage,
                sex, foodPreference, bio, temperament1, temperament2, breed, name,
                contact, catId, compatibleWith, isNeutered);
    }

    public void editCat(View v) {
        Intent i = new Intent(this, EditCat.class);
        i.putExtra("name", catName);
        this.startActivity(i);
    }


    public void deleteCat(View v) {
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
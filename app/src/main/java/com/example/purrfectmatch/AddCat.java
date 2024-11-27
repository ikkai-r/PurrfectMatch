package com.example.purrfectmatch;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddCat extends AppCompatActivity {

    private FrameLayout viewPager;
    private Button nextButton;
    private FirebaseAuth mAuth;
    private Bundle finalBundle;
    private static final int NUM_PAGES = 4; // Adjust this based on the number of fragments (sections)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_cat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();

        if (savedInstanceState == null) {
            CatGenForm catGenForm = new CatGenForm();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.cat_form, catGenForm)  // Fragment will be added to the container
                    .commit();
        }

    }

    public void onDataPassed(Bundle bundle) {
        // Retrieve data from the bundle
        finalBundle = bundle;
        registerCat();
    }

    private void registerCat() {

        // Retrieve data from the previous bundle
        String name = finalBundle.getString("name");
        String ageStr = finalBundle.getString("age"); // Get age as a string
        String weightStr = finalBundle.getString("weight");
        String breed = finalBundle.getString("breed");
        String temperament = finalBundle.getString("temperament");
        String compatible = finalBundle.getString("compatible");
        String food = finalBundle.getString("food");
        String feeStr = finalBundle.getString("fee");
        String picture = finalBundle.getString("catimg");
        String about = finalBundle.getString("about");
        List<String> bookmarked = new ArrayList<>();
        String sex = finalBundle.getString("sex");
        boolean isNeutered = finalBundle.getBoolean("isneutered");

        Log.d("process", "I'm now here before");

        // Convert age and weight to numbers
        int age = 0;
        age = Integer.parseInt(ageStr);
        int weight = 0;
        weight = Integer.parseInt(weightStr);
        int fee = 0;
        fee = Integer.parseInt(feeStr);


        if (name != null && breed != null &&
                temperament != null && compatible != null && food != null &&
                picture != null && about != null) {

            Log.d("finish", "values are retrieved properly");
            boolean isAdopted = false;

            // Create a map or a model class to hold the cat data
            Map<String, Object> catData = new HashMap<>();
            catData.put("name", name);
            catData.put("age", age);
            catData.put("weight", weight);
            catData.put("breed", breed);
            catData.put("temperament", temperament);
            catData.put("compatibleWith", compatible);
            catData.put("foodPreference", food);
            catData.put("adoptionFee", fee);
            catData.put("catImage", picture);
            catData.put("bio", about);
            catData.put("bookmarkedBy", bookmarked);
            catData.put("sex", sex);
            catData.put("isNeutered", isNeutered);
            catData.put("isAdopted", isAdopted);


            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("Cats")
                    .add(catData)
                    .addOnSuccessListener(documentReference -> {
                        Log.d("registerCat", "Cat successfully added with ID: " + documentReference.getId());
                        Toast.makeText(this, "Cat registered successfully!", Toast.LENGTH_SHORT).show();

                        // Navigate to a success screen
                        Intent i = new Intent(this, SuccessForm.class);
                        i.putExtra("title", "Cat Registration");
                        i.putExtra("title_big", "Success");
                        i.putExtra("subtitle_1", "You've added a new cat to our database!");
                        i.putExtra("subtitle_2", "Let's help find a home for them.");
                        i.putExtra("button_text", "Done");
                        startActivity(i);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Log.w("registerCat", "Error adding cat", e);
                        Toast.makeText(this, "Failed to register the cat. Please try again.", Toast.LENGTH_SHORT).show();
                    });

        } else {
            Log.d("finish", "values aren't retrieved properly");

            // Handle the case where any required field is missing
            Toast.makeText(this, "Please try again.", Toast.LENGTH_SHORT).show();
        }
    }


    public void cancel(View v) {
        finish();
    }
}
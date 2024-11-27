package com.example.purrfectmatch;

import android.app.Dialog;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditCat extends AppCompatActivity {

    private FrameLayout viewPager;
    private Button nextButton;
    private FirebaseAuth mAuth;
    private Bundle finalBundle;
    private static final int NUM_PAGES = 4; // Adjust this based on the number of fragments (sections)


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_cat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent intent = getIntent();

        mAuth = FirebaseAuth.getInstance();

        Bundle bundle = new Bundle();
        bundle.putString("name" ,intent.getStringExtra("name"));

        if (savedInstanceState == null) {
            CatGenEditForm catGenEditForm = new CatGenEditForm();
            catGenEditForm.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.cat_edit_form, catGenEditForm)  // Fragment will be added to the container
                    .commit();
        }

    }

    public void onDataPassed(Bundle bundle) {
        // Retrieve data from the bundle
        finalBundle = bundle;
        editCat();
    }

    private void editCat() {
        // Retrieve data from the previous bundle
        String name = finalBundle.getString("name");
        String ageStr = finalBundle.getString("age");
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

        // Convert age, weight, and fee to integers
        int age = Integer.parseInt(ageStr);
        int weight = Integer.parseInt(weightStr);
        int fee = Integer.parseInt(feeStr);

        if (name != null && breed != null &&
                temperament != null && compatible != null && food != null &&
                picture != null && about != null) {

            // Create a map to hold the updated cat data
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

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Query the database to find the document by cat name
            db.collection("Cats")
                    .whereEqualTo("name", name)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            // Get the document ID of the matching cat
                            String documentId = task.getResult().getDocuments().get(0).getId();

                            // Update the existing document
                            db.collection("Cats").document(documentId)
                                    .update(catData)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("registerCat", "Cat successfully updated with ID: " + documentId);
                                        Toast.makeText(this, "Cat updated successfully!", Toast.LENGTH_SHORT).show();

                                        // Navigate to a success screen
                                        Intent i = new Intent(this, SuccessForm.class);
                                        i.putExtra("title", "Cat Update");
                                        i.putExtra("title_big", "Success");
                                        i.putExtra("subtitle_1", "You've updated the cat's information!");
                                        i.putExtra("subtitle_2", "Let's help find a home for them.");
                                        i.putExtra("button_text", "Done");
                                        i.putExtra("user_type", "shelter");
                                        startActivity(i);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.w("registerCat", "Error updating cat", e);
                                        Toast.makeText(this, "Failed to update the cat. Please try again.", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Log.w("registerCat", "No cat found with the name: " + name);
                            Toast.makeText(this, "No cat found with the provided name.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.w("registerCat", "Error querying cat", e);
                        Toast.makeText(this, "Failed to fetch the cat. Please try again.", Toast.LENGTH_SHORT).show();
                    });

        } else {
            Log.d("finish", "values aren't retrieved properly");
            Toast.makeText(this, "Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    public void cancel(View v) {
        finish();
    }
}
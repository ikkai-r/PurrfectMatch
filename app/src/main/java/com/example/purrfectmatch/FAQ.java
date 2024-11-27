package com.example.purrfectmatch;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FAQ extends AppCompatActivity {

    private TextView aboutUsContent, appUsageContent, appTrackContent;
    private ImageView profile, explore, swipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.faq);

        initializeViews();

        setupFAQ();
    }

    private void initializeViews() {
        profile = findViewById(R.id.profile);
        swipe = findViewById(R.id.swipe);
        explore = findViewById(R.id.explore);
        aboutUsContent = findViewById(R.id.about_us_content);
        appUsageContent =  findViewById(R.id.app_usage_content);
        appTrackContent = findViewById(R.id.app_track);

        profile.setOnClickListener(view -> {
            Intent i = new Intent(this, ProfileActivity.class);
            startActivity(i);
            finish();
        });

        swipe.setOnClickListener(view -> {
            Intent i = new Intent(this, SwipeActivity.class);
            startActivity(i);
            finish();
        });

        explore.setOnClickListener(view -> {
            Intent i = new Intent(this, ExploreActivity.class);
            startActivity(i);
            finish();
        });
    }

    private void setupFAQ(){
        String aboutUsStr = "DLSU Pusa is the org that cares mainly for the Manila campus' cats—feeding, " +
                "neutering/spaying to control the cat population, " +
                "vaccination/antirabies shots, medical care, adopting/rehoming of stray cats & " +
                "occasionally even stray dogs, etc.";

        String appUsageStr = "1. You can swipe left to skip to the next cat profile.\n\n" +
                "2. Swipe right to apply for a cat you're interested in adopting.\n\n" +
                "3. Once you submit an application, the shelter will review it to determine if you're a suitable match.\n" +
                "\n4. If shortlisted, the shelter will provide a date and time range for you to meet the pet.\n" +
                "\n5. You can select a specific date and time from the options provided within the app.\n" +
                "\n6. During the scheduled meeting, you'll interact with the pet, and the shelter will assess compatibility.\n" +
                "\n7. After the meeting, the shelter may approve or reject your application based on their evaluation.\n" +
                "\n8. Communication with the shelter happens outside the app, usually via the phone number you provided, so ensure your contact details are accurate.\n" +
                "\n9. You can track your application status in the 'My Applications' section of the app.";

        String appTrackStr = "You can view all your submitted applications and their respective status " +
                "under the \"View Submitted Applications\" section in your profile.";

        aboutUsContent.setText(aboutUsStr);
        appUsageContent.setText(appUsageStr);
        appTrackContent.setText(appTrackStr);
    }
}
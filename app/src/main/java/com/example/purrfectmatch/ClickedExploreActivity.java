package com.example.purrfectmatch;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.view.ViewGroup;

public class ClickedExploreActivity extends AppCompatActivity implements GestureDetector.OnGestureListener{

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ImageView profile, explore, swipe, catImage, bookmarkIcon;
    private TextView ageText, weightText, sexText, breedText, neuterText, temperamentText, bioText,
            compatibleWithText, adoptionFeeText, contactInformationText, nameText;
    private String documentId;
    private ScrollView scrollView;

    private static final int MIN_DISTANCE = 150;
    private float x1, x2, y1, y2;
    private GestureDetector gestureDetector;
    private LinearLayout swipeableArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_explore_clicked);

        this.gestureDetector = new GestureDetector(this, this);

        initializeViews();

        Intent i = getIntent();
        documentId = i.getStringExtra("documentId");

        loadCatData();

        setupNavigationListeners();
    }

    private void initializeViews() {
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
    }

    private void setupNavigationListeners() {
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

        explore.setOnClickListener(view -> onBackPressed());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        gestureDetector.onTouchEvent(event);

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                y1 = event.getY();
                break;

            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                y2 = event.getY();

                // value for horizontal swipe
                float valueX = x2 - x1;

                //value for vertical swipe
                float valueY = y2 - y1;

                if(Math.abs(valueX) > MIN_DISTANCE){
                    if(x2>x1){
                        System.out.println("Swipe Right");
                        showPopupRight();

                    }else{
                        System.out.println("Swipe Left");
                        Toast.makeText(this, "Rejected", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                }else if (Math.abs(valueY) > MIN_DISTANCE){
                    if(x2>y1){
                        System.out.println("Swipe Down");
                    }else{
                        System.out.println("Swipe Up");
                    }
                }
        }

        return super.onTouchEvent(event);
    }

    private List<Object> createPopup(int layoutRes, View view) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(layoutRes, null);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;

        int width = (int) (screenWidth * 0.8);
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;

        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.showAtLocation(view, Gravity.CENTER, 30, 0);

        View overlay = new View(this);
        overlay.setBackgroundColor(Color.parseColor("#111111"));
        overlay.setAlpha(0.6f); // Adjust transparency

        ViewGroup rootLayout = findViewById(android.R.id.content);
        rootLayout.addView(overlay);

        popupWindow.showAtLocation(view, Gravity.CENTER, 30, 0);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                rootLayout.removeView(overlay); // Remove overlay when popup is dismissed
            }
        });

        List<Object> result = new ArrayList<>();
        result.add(popupView);       // Index 0: popupView
        result.add(popupWindow);     // Index 1: popupWindow
        result.add(rootLayout);      // Index 2: rootLayout
        result.add(overlay);        // Index 3: Overlay

        return result;
    }

    private void showPopupRight() {
        // Inflate the popup layout
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.dialog_swipe_right_cat, null);

        // Create the popup window
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = (int) (displayMetrics.widthPixels * 0.8);

        final PopupWindow popupWindow = new PopupWindow(
                popupView,
                width,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                true
        );

        // Set background
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Add semi-transparent overlay
        View overlay = new View(this);
        overlay.setBackgroundColor(Color.parseColor("#111111"));
        overlay.setAlpha(0.6f);

        ViewGroup rootLayout = findViewById(android.R.id.content);
        rootLayout.addView(overlay);

        // Set up buttons
        Button closeButton = popupView.findViewById(R.id.close_button);
        Button sendButton = popupView.findViewById(R.id.send_application_button);

        closeButton.setOnClickListener(v -> {
            popupWindow.dismiss();
            rootLayout.removeView(overlay);
        });

        sendButton.setOnClickListener(v -> {
            EditText aboutMeEditText = popupView.findViewById(R.id.aboutMeEditText);
            String applicationText = aboutMeEditText.getText().toString().trim();

            if (applicationText.isEmpty()) {
                Toast.makeText(this, "Please provide a reason for adoption.", Toast.LENGTH_SHORT).show();
            } else {
                // Handle application submission
                sendApplication(applicationText);
                popupWindow.dismiss();
                rootLayout.removeView(overlay);
                finish();
            }
        });

        // Show popup at the center of the screen without using anchorView
        popupWindow.showAtLocation(rootLayout, Gravity.CENTER, 0, 0);

        // Clean up overlay when popup is dismissed
        popupWindow.setOnDismissListener(() -> rootLayout.removeView(overlay));
    }

    private void sendApplication(String applicationText) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getApplicationContext(), "User not logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        String applicationId = FirebaseFirestore.getInstance().collection("Applications")
                               .document().getId();
        String userId = currentUser.getUid();

        Map<String, Object> applicationData = createApplicationData(applicationId, documentId, userId, applicationText);

        CollectionReference applicationsRef = FirebaseFirestore.getInstance().collection("Applications");
        applicationsRef.document(applicationId)
                .set(applicationData)
                .addOnSuccessListener(aVoid -> {
                    updateCatDocumentWithApplication(documentId, applicationId);
                    appendApplicationToUser(userId, applicationId);
                    Toast.makeText(getApplicationContext(), "Application sent successfully!",
                                    Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), "Failed to send application.",
                                    Toast.LENGTH_SHORT).show();
                });
    }

    private Map<String, Object> createApplicationData(String applicationId, String catId,
                                                      String userId, String applicationText)
    {
        Map<String, Object> applicationData = new HashMap<>();
        applicationData.put("applicationDate", FieldValue.serverTimestamp());
        applicationData.put("applicationId", applicationId);
        applicationData.put("catId", catId);
        applicationData.put("reason", applicationText);
        applicationData.put("status", "pending");
        applicationData.put("userId", userId);
        return applicationData;
    }

    private void appendApplicationToUser(String userId, String applicationId) {
        DocumentReference userRef = FirebaseFirestore.getInstance().collection("Users")
                                    .document(userId);

        userRef.update("catApplications", FieldValue.arrayUnion(applicationId))
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Application ID added to user successfully!");
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Failed to add application ID to user: " +
                            e.getMessage());
                });
    }

    private void updateCatDocumentWithApplication(String catId, String applicationId) {
        DocumentReference catRef = FirebaseFirestore.getInstance().collection("Cats")
                                   .document(catId);

        catRef.update("pendingApplications", FieldValue.arrayUnion(applicationId))
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Application ID successfully added to pendingApplications.");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding application ID to pendingApplications.", e);
                });
    }

    private void loadCatData() {
        if (documentId != null) {
            // Get a reference to the specific cat document by its ID
            DocumentReference catRef = db.collection("Cats").document(documentId);

            catRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Create the SwipeData using the document data
                        SwipeData swipeDataItem = createSwipeDataFromDocument(document, documentId);

                        //TODO: Change to actual cat images
                        catImage.setImageResource(R.drawable.cat1);

                        ageText.setText(String.valueOf(swipeDataItem.age) + "months");
                        weightText.setText(String.valueOf(swipeDataItem.weight) + " lbs");
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
    public boolean onDown(@NonNull MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(@NonNull MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(@NonNull MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(@Nullable MotionEvent motionEvent, @NonNull MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(@NonNull MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(@Nullable MotionEvent motionEvent, @NonNull MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

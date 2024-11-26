package com.example.purrfectmatch;

import android.content.Intent;
import android.graphics.Color;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.viewpager2.widget.ViewPager2;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.view.ViewGroup;

public class SwipeActivity extends AppCompatActivity implements GestureDetector.OnGestureListener{


    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<SwipeData> swipeDataList = new ArrayList<>();


    ViewPager2 viewPager2;
    SwipeAdapter swipeAdapter;
    ImageView profile, explore, swipe;
    TextView filter;

    private static final String TAG = "Swipe Position";
    private float x1, x2, y1, y2;
    private static int MIN_DISTANCE = 150;
    private static int MIN_VELOCITY = 10;
    private GestureDetector gestureDetector;

    private static final float MIN_SCALE = 0.85f;
    private static final float MIN_ALPHA = 0.5f;

    // filter variables
    private int maxAge = -1;
    private int maxAdoptionFee = -1;
    private String selectedSex = "All";


    ImageView catPic;
    int[] catPicSet0= {R.drawable.cat0, R.drawable.cat1, R.drawable.cat2};
    int[] catPicSet1= {R.drawable.cat3, R.drawable.cat6};
    int[] catPicSet2= {R.drawable.cat5, R.drawable.cat6};
    int[] catPicSet3= {R.drawable.cat7};
    int[] catPicSet4= {R.drawable.cat8};
    int imageIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.homepage_swipe);

        viewPager2 = findViewById(R.id.viewPager2);
        viewPager2.setUserInputEnabled(false);
        profile = findViewById(R.id.profile);
        explore = findViewById(R.id.imageView19);
        swipe = findViewById(R.id.imageView17);
        filter = findViewById(R.id.filterText);


        this.gestureDetector = new GestureDetector(this, this);

        loadCatsForSwipe();

        profile.setOnClickListener(view -> {
            Intent i = new Intent(this, ProfileActivity.class);
            startActivity(i);
            finish();
        });

        explore.setOnClickListener(view -> {
            Intent i = new Intent(this, ExploreActivity.class);
            startActivity(i);
            finish();
        });

        filter.setOnClickListener(view -> {
            List<Object> result = createPopup(R.layout.popup_filter, view);

            View popupView = (View) result.get(0);
            PopupWindow popupWindow = (PopupWindow) result.get(1);
            ViewGroup rootLayout = (ViewGroup) result.get(2);

            EditText ageEditText = popupView.findViewById(R.id.filterAgeEditText);
            EditText feeEditText = popupView.findViewById(R.id.filterAdoptionFeeEditText);
            RadioGroup sexRadioGroup = popupView.findViewById(R.id.filterSexRadioGroup);

            Button applyButton = popupView.findViewById(R.id.filterApplyButton);
            Button resetButton = popupView.findViewById(R.id.filterResetButton);

            // Set previously saved values
            if (maxAge != -1) {
                ageEditText.setText(String.valueOf(maxAge));
            }

            if (maxAdoptionFee != -1) {
                feeEditText.setText(String.valueOf(maxAdoptionFee));
            }

            if (selectedSex.equals("M")) {
                sexRadioGroup.check(R.id.filterSexMale);
            } else if (selectedSex.equals("F")) {
                sexRadioGroup.check(R.id.filterSexFemale);
            }


            resetButton.setOnClickListener(filterView -> {
                maxAdoptionFee = -1;
                maxAge = -1;
                selectedSex = "All";

                filter.setText("Filter");
                swipeAdapter.updateData(swipeDataList.toArray(new SwipeData[0]));

                popupWindow.dismiss();
            });

            applyButton.setOnClickListener(filterView -> {
                // Capture filter values
                filter.setText("Filter (filtered)");
                if (!ageEditText.getText().toString().isEmpty()) {
                    maxAge = Integer.parseInt(ageEditText.getText().toString());
                }

                if (!feeEditText.getText().toString().isEmpty()) {
                    maxAdoptionFee = Integer.parseInt(feeEditText.getText().toString());
                }

                int selectedSexId = sexRadioGroup.getCheckedRadioButtonId();
                if (selectedSexId == R.id.filterSexMale) {
                    selectedSex = "M";
                } else if (selectedSexId == R.id.filterSexFemale) {
                    selectedSex = "F";
                } else {
                    selectedSex = "All";
                }

                // Apply the filter and update the swipe data
                applyFilter();
                popupWindow.dismiss();
            });

        });
    }

    private void applyFilter() {
        List<SwipeData> filteredList = new ArrayList<>();

        // Filter swipeDataList based on the conditions
        for (SwipeData data : swipeDataList) {
            boolean isValid = true;

            if (data.getAge() > maxAge && maxAge != -1) {
                isValid = false;
            }

            if (data.getAdoptionFee() > maxAdoptionFee && maxAdoptionFee != -1) {
                isValid = false;
            }

            if (!selectedSex.equals("All") &&  data.getSexS() != selectedSex.charAt(0)) {
                isValid = false;
            }

            if (isValid) {
                filteredList.add(data);
            }
        }

        // Update the adapter with the filtered data
        swipeAdapter.updateData(filteredList.toArray(new SwipeData[0]));
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

    private List<Object> createNonDismissablePopup(int layoutRes, View view) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(layoutRes, null);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;

        int width = (int) (screenWidth * 0.8);
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;

        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setOutsideTouchable(false);
        popupWindow.setTouchable(true);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        View overlay = new View(this);
        overlay.setBackgroundColor(Color.parseColor("#111111"));
        overlay.setAlpha(0.6f); // Adjust transparency to your preference

        // Add the overlay to the root layout
        ViewGroup rootLayout = findViewById(android.R.id.content);
        rootLayout.addView(overlay);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                rootLayout.removeView(overlay); 
                popupWindow.dismiss();
                viewPager2.setCurrentItem(0);

            }
        });

        // Return the necessary objects for further interaction
        List<Object> result = new ArrayList<>();
        result.add(popupView);       // Index 0: popupView
        result.add(popupWindow);     // Index 1: popupWindow
        result.add(rootLayout);      // Index 2: rootLayout
        result.add(overlay);         // Index 3: Overlay

        return result;
    }



    private void loadCatsForSwipe() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        DocumentReference userRef = db.collection("Users").document(userId);

        // Fetch user's application IDs first
        userRef.get().addOnSuccessListener(userSnapshot -> {
            if (userSnapshot.exists()) {
                List<String> applicationIds = (List<String>) userSnapshot.get("catApplications");
                List<String> bookmarkedCats = (List<String>) userSnapshot.get("bookmarkedCats");

                if (applicationIds == null) {
                    applicationIds = new ArrayList<>();
                }

                if (bookmarkedCats == null) {
                    bookmarkedCats = new ArrayList<>();
                }

                fetchCatIdsFromApplications(applicationIds, bookmarkedCats);
            } else {
                Toast.makeText(this, "User data not found.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
        });
    }

    private void fetchCatIdsFromApplications(List<String> applicationIds, List<String> bookmarkedCats) {
        if (applicationIds.isEmpty()) {
            // If there are no applications, load all cats
            fetchAndFilterCats(new ArrayList<>(), bookmarkedCats);
            return;
        }

        CollectionReference applicationsRef = db.collection("Applications");


        applicationsRef.whereIn(FieldPath.documentId(), applicationIds)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<String> excludedCatIds = new ArrayList<>();

                    // check cats that are already swiped right
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        String catId = document.getString("catId");
                        if (catId != null) {
                            excludedCatIds.add(catId);
                        }
                    }

                    // exclude those cats and set bookmarks
                    fetchAndFilterCats(excludedCatIds, bookmarkedCats);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to fetch application data.", Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchAndFilterCats(List<String> excludedCatIds, List<String> bookmarkedCats) {
        CollectionReference catsRef = db.collection("Cats");

        catsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String catId = document.getId();

                    // Exclude cats already applied for
                    if (!excludedCatIds.contains(catId)) {
                        SwipeData swipeData = createSwipeDataFromDocument(document, catId);

                        // set the bookmark
                        boolean isBookmarked = bookmarkedCats.contains(catId);
                        swipeData.setBookmarked(isBookmarked);
                        swipeDataList.add(swipeData);
                    }
                }

                swipeAdapter = new SwipeAdapter(swipeDataList.toArray(new SwipeData[0]), SwipeActivity.this);
                viewPager2.setAdapter(swipeAdapter);
                viewPager2.setPageTransformer((page, position) -> {
                    float absPos = Math.abs(position);
                    page.setAlpha(1.0f - absPos);
                    page.setScaleY(1.0f - absPos * 0.15f);
                });
            } else {
                Toast.makeText(this, "Failed to load cat data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private SwipeData createSwipeDataFromDocument(QueryDocumentSnapshot document, String catId) {
        int age = document.getLong("age").intValue();
        int weight = document.getLong("weight").intValue();
        int adoptionFee = document.getLong("adoptionFee").intValue();

        String catImageStr = document.getString("catImage");
        int catImage = getResources().getIdentifier(catImageStr, "drawable", getPackageName());

        char sex = document.getString("sex").charAt(0);
        String foodPreference = document.getString("foodPreference");
        String bio = document.getString("bio");
        String temperament = document.getString("temperament");
        String breed = document.getString("breed");
//        String medicalHistory = document.getString("medicalHistory");
        String name = document.getString("name");
//        String birthday = document.getString("birthday");
        String contact = document.getString("contact");
        String compatibleWith = document.getString("compatibleWith");
        boolean isNeutered = document.getBoolean("isNeutered");

        return new SwipeData(age, weight, adoptionFee, R.drawable.check, R.drawable.check, R.drawable.check, catImage,
                sex, foodPreference, bio, temperament, breed, name,
                contact, catId, compatibleWith, isNeutered);
    }

    private void showPopupRight(View anchorView) {
        List<Object> result = createPopup(R.layout.dialog_swipe_right_cat, anchorView);
        View popupView = (View) result.get(0);
        PopupWindow popupWindow = (PopupWindow) result.get(1);
        ViewGroup rootLayout = (ViewGroup) result.get(2);
        View overlay = (View) result.get(3);

        Button closeButton = popupView.findViewById(R.id.close_button);
        Button sendButton = popupView.findViewById(R.id.send_application_button);


        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                rootLayout.removeView(overlay);
            }
        });
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Collect the input data (assuming there's an EditText in the popup)
                EditText aboutMeEditText = popupView.findViewById(R.id.aboutMeEditText);
                String applicationText = aboutMeEditText.getText().toString().trim();

                // Perform validation if needed
                if (applicationText.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please provide a reason for adoption.", Toast.LENGTH_SHORT).show();
                } else {
                    sendApplication(applicationText);

                    popupWindow.dismiss();
                    rootLayout.removeView(overlay);

                    viewPager2.post(new Runnable() {
                        @Override
                        public void run() {
                            moveToNextCard();
                            swipeAdapter.notifyItemChanged(viewPager2.getCurrentItem());
                        }
                    });

                }
            }
        });
    }

    private void sendApplication(String applicationText) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getApplicationContext(), "User not logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        String applicationId = FirebaseFirestore.getInstance().collection("Applications").document().getId();
        String catId = swipeDataList.get(viewPager2.getCurrentItem()).getCatId();
        String userId = currentUser.getUid();

        Map<String, Object> applicationData = createApplicationData(applicationId, catId, userId, applicationText);

        CollectionReference applicationsRef = FirebaseFirestore.getInstance().collection("Applications");
        applicationsRef.document(applicationId)
                .set(applicationData)
                .addOnSuccessListener(aVoid -> {
                    updateCatDocumentWithApplication(catId, applicationId);
                    appendApplicationToUser(userId, applicationId);
                    Toast.makeText(getApplicationContext(), "Application sent successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), "Failed to send application.", Toast.LENGTH_SHORT).show();
                });
    }

    private Map<String, Object> createApplicationData(String applicationId, String catId, String userId, String applicationText) {
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
        DocumentReference userRef = FirebaseFirestore.getInstance().collection("Users").document(userId);

        userRef.update("catApplications", FieldValue.arrayUnion(applicationId))
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Application ID added to user successfully!");
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Failed to add application ID to user: " + e.getMessage());
                });
    }

    private void updateCatDocumentWithApplication(String catId, String applicationId) {
        DocumentReference catRef = FirebaseFirestore.getInstance().collection("Cats").document(catId);

        catRef.update("pendingApplications", FieldValue.arrayUnion(applicationId))
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Application ID successfully added to pendingApplications.");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding application ID to pendingApplications.", e);
                });
    }

    private void showPopupLeft(View anchorView) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_left_swipe, null);

        final PopupWindow popupWindow = new PopupWindow(
                popupView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        popupWindow.setFocusable(false);
        popupWindow.showAsDropDown(anchorView, 0, -anchorView.getHeight());
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                popupWindow.dismiss();
            }
        }, 500);

        viewPager2.post(new Runnable() {
            @Override
            public void run() {
                moveToNextCard();
                swipeAdapter.notifyItemChanged(viewPager2.getCurrentItem());
            }
        });
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
                        showPopupRight(viewPager2);

                    }else{
                        System.out.println("Swipe Left");
                        swipeAdapter.setFlip(false);
                        viewPager2.setRotationY(0);
                        //moveToNextCard();
                        showPopupLeft(viewPager2);
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

/*
    @Override
    public void onBackPressed() {
        // Do nothing, so back navigation is disabled
    }
    */



    // Method to move to the next card in the dataset
    private void moveToNextCard() {
        int currentItem = viewPager2.getCurrentItem();
        if (currentItem < swipeAdapter.getItemCount() - 1) {
            viewPager2.setCurrentItem(currentItem + 1, true);
        } else {
            viewPager2.setUserInputEnabled(false);
            clearLastCardView();
            showNoMoreCatsPopup();
        }
    }

    // Method to clear the last card view when no more cats are available
    private void clearLastCardView() {
        // Assuming your ViewPager2's adapter can be notified to handle the "no more cats" case
        if (swipeAdapter.getItemCount() > 0) {
            swipeAdapter.notifyItemRemoved(swipeAdapter.getItemCount() - 1);
        }
    }

    // Method to show the No More Cats popup
    private void showNoMoreCatsPopup() {
        List<Object> popupData = createNonDismissablePopup(R.layout.no_more_cats, findViewById(android.R.id.content));

        // Get the PopupView, PopupWindow, and other components
        View popupView = (View) popupData.get(0);
        final PopupWindow popupWindow = (PopupWindow) popupData.get(1);
        final View rootLayout = (ViewGroup) popupData.get(2);

        // Find the Reload button and set its click listener
        AppCompatButton reloadButton = popupView.findViewById(R.id.reload_rejected_button);
        reloadButton.setOnClickListener(v -> {
            viewPager2.setCurrentItem(0);
            popupWindow.dismiss();
        });
    }


}

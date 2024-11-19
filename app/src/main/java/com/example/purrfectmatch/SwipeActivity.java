package com.example.purrfectmatch;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SwipeActivity extends AppCompatActivity implements GestureDetector.OnGestureListener{


    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<SwipeData> swipeDataList = new ArrayList<>();
    

    ViewPager2 viewPager2;
    SwipeAdapter swipeAdapter;
    ImageView profile, explore, swipe;
    private static final String TAG = "Swipe Position";
    private float x1, x2, y1, y2;
    private static int MIN_DISTANCE = 150;
    private static int MIN_VELOCITY = 10;
    private GestureDetector gestureDetector;

    private static final float MIN_SCALE = 0.85f;
    private static final float MIN_ALPHA = 0.5f;

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



        this.gestureDetector = new GestureDetector(this, this);

        loadCatData();


        profile.setOnClickListener(view -> {
            Intent i = new Intent(this, ProfileActivity.class);
            startActivity(i);
        });

        swipe.setOnClickListener(view -> {
            Intent i = new Intent(this, SwipeActivity.class);
            startActivity(i);
        });

        explore.setOnClickListener(view -> {
            Intent i = new Intent(this, ExploreActivity.class);
            startActivity(i);
        });
    }

    private void loadCatData() {
        CollectionReference catsRef = db.collection("Cats");

        catsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    SwipeData swipeData = createSwipeDataFromDocument(document);
                    swipeDataList.add(swipeData);
                }

                swipeAdapter = new SwipeAdapter(swipeDataList.toArray(new SwipeData[0]), SwipeActivity.this);
                viewPager2.setAdapter(swipeAdapter);
                viewPager2.setPageTransformer((page, position) -> {
                    float absPos = Math.abs(position);
                    page.setAlpha(1.0f - absPos);
                    page.setScaleY(1.0f - absPos * 0.15f);
                });


            } else {
                Toast.makeText(this, "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private SwipeData createSwipeDataFromDocument(QueryDocumentSnapshot document) {
        int age = document.getLong("age").intValue();
        int weight = document.getLong("weight").intValue();
        int adoptionFee = document.getLong("adoptionFee").intValue();
        
        List<String> catImages = (List<String>) document.get("catImages");
        int[] catPicSet = new int[catImages.size()];
        for (int i = 0; i < catImages.size(); i++) {
            catPicSet[i] = getResources().getIdentifier(catImages.get(i), "drawable", getPackageName());
        }

        char sex = document.getString("sex").charAt(0);
        String foodPreference = document.getString("foodPreference");
        String bio = document.getString("bio");
        String temperament = document.getString("temperament");
        String breed = document.getString("breed");
        String medicalHistory = document.getString("medicalHistory");
        String name = document.getString("name");
        String birthday = document.getString("birthday");
        String contact = document.getString("contact");

        return new SwipeData(age, weight, adoptionFee, R.drawable.check, R.drawable.check, R.drawable.check, catPicSet,
                             sex, foodPreference, bio, temperament, breed, medicalHistory, name, birthday, contact, true);
    }



    private void showPopupRight(View anchorView) {
        // Inflate the popup layout
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_right_swipe, null);

        // Create the PopupWindow
        final PopupWindow popupWindow = new PopupWindow(
                popupView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        // Set focusable to false so it auto-dismisses on touch outside
        popupWindow.setFocusable(false);

        // Show the popup near the anchor view (adjust offsets if needed)
        popupWindow.showAsDropDown(anchorView, 0, -anchorView.getHeight());

        // Auto-dismiss the popup after 2 seconds
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                popupWindow.dismiss();
            }
        }, 500);
    }

    private void showPopupLeft(View anchorView) {
        // Inflate the popup layout
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_left_swipe, null);

        // Create the PopupWindow
        final PopupWindow popupWindow = new PopupWindow(
                popupView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        // Set focusable to false so it auto-dismisses on touch outside
        popupWindow.setFocusable(false);

        // Show the popup near the anchor view (adjust offsets if needed)
        popupWindow.showAsDropDown(anchorView, 0, -anchorView.getHeight());

        // Auto-dismiss the popup after 2 seconds
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                popupWindow.dismiss();
            }
        }, 500);
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
                        swipeAdapter.setFlip(true);
                        viewPager2.setRotationY(180);
                        //moveToNextCard();
                        showPopupRight(viewPager2);
                    }else{
                        System.out.println("Swipe Left");
                        swipeAdapter.setFlip(false);
                        viewPager2.setRotationY(0);
                        //moveToNextCard();
                        showPopupLeft(viewPager2);
                    }

                    viewPager2.post(new Runnable() {
                        @Override
                        public void run() {
                            moveToNextCard();
                            swipeAdapter.notifyItemChanged(viewPager2.getCurrentItem());
                        }
                    });
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

    // Method to move to the next card in the dataset
    private void moveToNextCard() {
        int currentItem = viewPager2.getCurrentItem();
        if (currentItem < swipeAdapter.getItemCount() - 1) {

            viewPager2.setCurrentItem(currentItem + 1, true);


            if(currentItem >= swipeAdapter.getItemCount() - 2){
                viewPager2.setUserInputEnabled(false);
                Toast.makeText(this, "End of Cards!", Toast.LENGTH_SHORT).show();
            }
        } else {
            viewPager2.setUserInputEnabled(false);
            Toast.makeText(this, "End of Cards!", Toast.LENGTH_SHORT).show();
        }
    }
}

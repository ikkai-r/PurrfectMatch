package com.example.purrfectmatch;

import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

public class SwipeActivity extends AppCompatActivity implements GestureDetector.OnGestureListener{


    ViewPager2 viewPager2;
    SwipeAdapter swipeAdapter;
    ImageView profile, bookmark, explore, swipe;
    private static final String TAG = "Swipe Position";
    private float x1, x2, y1, y2;
    private static int MIN_DISTANCE = 150;
    private static int MIN_VELOCITY = 10;
    private GestureDetector gestureDetector;

    private static final float MIN_SCALE = 0.85f;
    private static final float MIN_ALPHA = 0.5f;

    ImageView catPic;
    int[] catPicSet= {R.drawable.cat0, R.drawable.cat1, R.drawable.cat2};
    int imageIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.homepage_swipe);

        viewPager2 = findViewById(R.id.viewPager2);
        viewPager2.setUserInputEnabled(false);

        bookmark = findViewById(R.id.bookmark);

        profile = findViewById(R.id.profile);

        explore = findViewById(R.id.imageView19);

        swipe = findViewById(R.id.imageView17);


        //viewPager2.requestDisallowInterceptTouchEvent(false);

        this.gestureDetector = new GestureDetector(this, this);

        SwipeData swipeData[] = new SwipeData[]{
                new SwipeData(47, 12, 1000,  R.drawable.check, R.drawable.check, R.drawable.check, catPicSet,'F', "wet food"
                , "TEST BIO", "Aloof", "Puspin", "TEST MEDICAL HISTORY", "CAR", "01/01/2001", "091712345678"),

                new SwipeData(47, 12, 1000,  R.drawable.check, R.drawable.check, R.drawable.check, catPicSet,'F', "wet food"
                        , "TEST BIO", "Aloof", "Puspin", "TEST MEDICAL HISTORY", "CAR2", "01/01/2001", "091712345678"),

                new SwipeData(47, 12, 1000,  R.drawable.check, R.drawable.check, R.drawable.check, catPicSet,'F', "wet food"
                        , "TEST BIO", "Aloof", "Puspin", "TEST MEDICAL HISTORY", "CAR3", "01/01/2001", "091712345678"),
                new SwipeData(47, 12, 1000,  R.drawable.check, R.drawable.check, R.drawable.check, catPicSet,'F', "wet food"
                        , "TEST BIO", "Aloof", "Puspin", "TEST MEDICAL HISTORY", "CAR4", "01/01/2001", "091712345678"),
                new SwipeData(47, 12, 1000,  R.drawable.check, R.drawable.check, R.drawable.check, catPicSet,'F', "wet food"
                        , "TEST BIO", "Aloof", "Puspin", "TEST MEDICAL HISTORY", "CAR5", "01/01/2001", "091712345678")
        };

        swipeAdapter = new SwipeAdapter(swipeData, SwipeActivity.this);
        viewPager2.setAdapter(swipeAdapter);

        //viewPager2.setUserInputEnabled(false);

        // Set up a touch listener on the ViewPager2

        /*
        // Set up a gesture detector for swipe handling
        gestureDetector = new GestureDetectorCompat(this, new SwipeGestureListener());

        // Detect gestures on the ViewPager2
        viewPager2.getChildAt(0).setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));
        */

        /*
        catPic = findViewById(R.id.catPic);

        catPic.setOnClickListener(view -> {
            imageIndex = (imageIndex + 1) % catPicSet.length;
            catPic.setImageResource(catPicSet[imageIndex]);
        });
        */

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

        bookmark.setOnClickListener(view -> {
            Intent i = new Intent(this, BookmarkActivity.class);
            startActivity(i);
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
                        swipeAdapter.setFlip(true);
                        viewPager2.setRotationY(180);
                        //moveToNextCard();
                    }else{
                        System.out.println("Swipe Left");
                        swipeAdapter.setFlip(false);
                        viewPager2.setRotationY(0);
                        //moveToNextCard();
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

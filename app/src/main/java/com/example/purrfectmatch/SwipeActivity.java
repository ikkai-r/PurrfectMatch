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

public class SwipeActivity extends AppCompatActivity implements GestureDetector.OnGestureListener{


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


        //viewPager2.requestDisallowInterceptTouchEvent(false);

        this.gestureDetector = new GestureDetector(this, this);

        SwipeData swipeData[] = new SwipeData[]{
                new SwipeData(30, 15, 1000,  R.drawable.check, R.drawable.check, R.drawable.check, catPicSet0,'F', "- wet food"
                , "Playful and affectionate, loves lounging on windowsills and chasing laser pointers. He’s a social butterfly and always greets guests at the door.",
                        "Aloof", "Puspin", "Diagnosed with mild arthritis, treated with joint supplements.\n" +
                        "Experienced an ear infection last year but fully recovered with antibiotics.", "CAR", "01/09/2010", "091712345678", true),

                new SwipeData(20, 11, 5500,  R.drawable.x, R.drawable.x, R.drawable.x, catPicSet2,'M', "- wet food"
                        , "Energetic and mischievous, loves to ambush other pets and zoom through the house at midnight. He's endlessly curious about new things.",
                        "Aloof", "Maine Coon", "None so far", "Chai", "12/23/2012", "091712345678", false),

                new SwipeData(47, 12, 4500,  R.drawable.check, R.drawable.x, R.drawable.check, catPicSet3,'F', "- dry food"
                        , "Sweet and friendly, is a cuddler. He loves kneading blankets and being held. His favorite activity is watching birds from the window.",
                        "Moody", "Persian Maine coon", "Neutered at 5 months.\n" +
                        "No chronic illnesses or allergies.\n" +
                        "Treated for a minor flea infestation three months ago, now on preventive medication.\n", "Solana", "10/11/2018", "091712345678", true),
                new SwipeData(34, 16, 2500,  R.drawable.check, R.drawable.check, R.drawable.x, catPicSet4,'M', "- water"
                        , "Calm and elegant, is a quiet observer. She prefers high perches and enjoys being brushed. She’s bonded closely with one person and can be shy around others.",
                        "Orange", "Puspin", "Overweight, currently on a weight management diet.", "Leo", "01/25/2020", "091712345678", true),
                new SwipeData(19, 10, 9000,  R.drawable.x, R.drawable.check, R.drawable.x, catPicSet1,'F', "- wet food"
                        , "Gentle and laid-back, enjoys long naps and belly rubs. He’s especially fond of sunbeams and naps near radiators", "Playful", "Siamese",
                        "None so far", "Coco", "08/26/2020", "091712345678", false)
        };

        swipeAdapter = new SwipeAdapter(swipeData, SwipeActivity.this);
        viewPager2.setAdapter(swipeAdapter);

        viewPager2.setPageTransformer((page, position) -> {
            float absPos = Math.abs(position);
            page.setAlpha(1.0f - absPos);
            page.setScaleY(1.0f - absPos * 0.15f);
        });


        //viewPager2.setUserInputEnabled(false);

        // Set up a touch listener on the ViewPager20

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

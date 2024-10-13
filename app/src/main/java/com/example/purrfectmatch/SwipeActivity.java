package com.example.purrfectmatch;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class SwipeActivity extends AppCompatActivity {

    ImageView catPic;
    int[] catPicSet= {R.drawable.cat0, R.drawable.cat1, R.drawable.cat0};
    int imageIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.homepage_swipe);

        catPic = findViewById(R.id.catPic);

        catPic.setOnClickListener(view -> {
            imageIndex = (imageIndex + 1) % catPicSet.length;
            catPic.setImageResource(catPicSet[imageIndex]);
        });
    }
}

package com.example.purrfectmatch;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SuccessForm extends AppCompatActivity {

    private TextView title, title_big, subtitle_1, subtitle_2;
    private Button buttonYes;
    private String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_success_form);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        title = findViewById(R.id.title);
        title_big = findViewById(R.id.title_big);
        subtitle_1 = findViewById(R.id.subtitle_1);
        subtitle_2 = findViewById(R.id.subtitle_2);
        buttonYes = findViewById(R.id.buttonYes);

        Intent intent = getIntent();
        String title_str = intent.getStringExtra("title");
        String titleBig = intent.getStringExtra("title_big");
        String subtitle1 = intent.getStringExtra("subtitle_1");
        String subtitle2 = intent.getStringExtra("subtitle_2");
        String buttonText = intent.getStringExtra("button_text");
        userType = intent.getStringExtra("user_type");


        // Set the retrieved data into the TextViews and Button
        title.setText(title_str);
        title_big.setText(titleBig);
        subtitle_1.setText(subtitle1);
        subtitle_2.setText(subtitle2);
        buttonYes.setText(buttonText);
    }

    public void goShelterView(View v) {
        Intent i = new Intent(this, SwipeActivity.class);
        this.startActivity(i);
    }

    public void handleButtonClick(View v) {
        Intent nextIntent = null;
        if ("shelter".equalsIgnoreCase(userType)) {
            nextIntent = new Intent(this, ShelterPage.class);
        } else if ("user".equalsIgnoreCase(userType)) {
            nextIntent = new Intent(this, SwipeActivity.class);
        }
        startActivity(nextIntent);
    }
}
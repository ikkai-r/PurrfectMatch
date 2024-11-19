package com.example.purrfectmatch;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

public class SignUp extends AppCompatActivity {

    private ViewPager2 viewPager;
    private Button nextButton;
    private static final int NUM_PAGES = 4; // Adjust this based on the number of fragments (sections)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        viewPager = findViewById(R.id.form);
        nextButton = findViewById(R.id.buttonNext);

        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        viewPager.setUserInputEnabled(false);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                if (position == viewPager.getAdapter().getItemCount() - 1) {
                    nextButton.setText("Register");
                } else {
                    nextButton.setText("Next");
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem() < NUM_PAGES - 1) {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                } else if(viewPager.getCurrentItem() == NUM_PAGES-1) {
                    Intent i = new Intent(SignUp.this, SuccessForm.class);
                    i.putExtra("title", "Sign up");
                    i.putExtra("title_big", "Success");
                    i.putExtra("subtitle_1", "Welcome to our Purrfect family!");
                    i.putExtra("subtitle_2", "Are you ready to meet the cat of your dreams?");
                    i.putExtra("button_text", "Yes!");
                    i.putExtra("user_type", "user");
                    SignUp.this.startActivity(i);
                    finish();
                }
            }
        });
    }

    public void login(View v) {
        Intent i = new Intent(this, Login.class);
        this.startActivity(i);
    }
}
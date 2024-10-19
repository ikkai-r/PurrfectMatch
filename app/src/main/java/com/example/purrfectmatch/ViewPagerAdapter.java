package com.example.purrfectmatch;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(FragmentActivity fa) {
        super(fa);
    }

    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new RegisterForm();
            case 1:
                return new PersonalForm();
            case 2:
                return new ContactForm();
            case 3:
                return new LifestyleForm();
            default:
                return new RegisterForm();  // Default case
        }
    }

    @Override
    public int getItemCount() {
        return 4; // Number of fragments
    }
}
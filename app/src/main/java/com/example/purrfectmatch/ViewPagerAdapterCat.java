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

public class ViewPagerAdapterCat extends FragmentStateAdapter {

    public ViewPagerAdapterCat(FragmentActivity fa) {
        super(fa);
    }

    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new CatGenForm();
            case 1:
                return new CatLifeForm();
            case 2:
                return new CatPicForm();
            case 3:
                return new CatAboutForm();
            default:
                return new CatGenForm();
        }
    }

    @Override
    public int getItemCount() {
        return 4; // Number of fragments
    }
}
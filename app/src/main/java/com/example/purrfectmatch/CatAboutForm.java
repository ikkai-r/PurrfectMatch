package com.example.purrfectmatch;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * */

public class CatAboutForm extends Fragment {

    public CatAboutForm() {
        // Required empty public constructor
    }

    private EditText about;
    private Button buttonNext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cat_about_form, container, false);

        about = view.findViewById(R.id.aboutMeEditText);
        buttonNext = view.findViewById(R.id.buttonNext5);

        buttonNext.setOnClickListener(v -> {
            // Handle the next button click event


            String aboutInput = about.getText().toString().trim();

            if (TextUtils.isEmpty(aboutInput)) {
                Toast.makeText(getActivity(), "All fields are required!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Bundle data to pass to the next step (e.g., SignUp activity or another fragment)
            Bundle previousBundle = getArguments();
            if (previousBundle == null) {
                previousBundle = new Bundle();
            }


            previousBundle.putString("about", aboutInput);


            // Pass the bundle to sign up
            AddCat addCat = (AddCat) getActivity();
            if (addCat != null) {
                addCat.onDataPassed(previousBundle);
            }
        });

        return view;
    }
}
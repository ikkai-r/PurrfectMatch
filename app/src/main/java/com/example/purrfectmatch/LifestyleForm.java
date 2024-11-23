package com.example.purrfectmatch;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

public class LifestyleForm extends Fragment {

    private Spinner spinnerHousehold, spinnerOtherPets, spinnerPreferences, spinnerPreferences2;
    private Button buttonNext;

    public LifestyleForm() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lifestyle_form, container, false);

        // Initialize Spinners
        spinnerHousehold = view.findViewById(R.id.spinnerHousehold);
        spinnerOtherPets = view.findViewById(R.id.spinnerOtherPets);
        spinnerPreferences = view.findViewById(R.id.spinnerPreferences);
        spinnerPreferences2 = view.findViewById(R.id.spinnerPreferences2);

        ArrayAdapter<String> householdAdapter = new ArrayAdapter<>(getContext(),
                R.layout.spinner_item, new String[]{"1-2 members", "3-4 members", "5+ members"});
        householdAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinnerHousehold.setAdapter(householdAdapter);

        ArrayAdapter<String> otherPetsAdapter = new ArrayAdapter<>(getContext(),
                R.layout.spinner_item, new String[]{"Yes", "No"});
        otherPetsAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinnerOtherPets.setAdapter(otherPetsAdapter);

        ArrayAdapter<String> preferencesAdapter = new ArrayAdapter<>(getContext(),
                R.layout.spinner_item, new String[]{"Independent", "Moderately Clingy", "Affectionate"});
        preferencesAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinnerPreferences.setAdapter(preferencesAdapter);

        ArrayAdapter<String> preferences2Adapter = new ArrayAdapter<>(getContext(),
                R.layout.spinner_item, new String[]{"Active / Playful", "Calm / Shy", "Curious"});
        preferences2Adapter.setDropDownViewResource(R.layout.spinner_item);
        spinnerPreferences2.setAdapter(preferences2Adapter);


        // Initialize Button
        buttonNext = view.findViewById(R.id.buttonNext);
        TextView backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pop the fragment back stack
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        buttonNext.setOnClickListener(view1 -> {
            // Collect data from spinners and checkboxes
            String householdMembers = spinnerHousehold.getSelectedItem().toString();
            String otherPets = spinnerOtherPets.getSelectedItem().toString();
            String preferences1 = spinnerPreferences.getSelectedItem().toString();
            String preferences2 = spinnerPreferences2.getSelectedItem().toString();

            // Check if all required fields are selected
            if (TextUtils.isEmpty(householdMembers) ||
                    TextUtils.isEmpty(otherPets) ||
                    TextUtils.isEmpty(preferences1) || TextUtils.isEmpty(preferences2)) {
                Toast.makeText(getActivity(), "Please fill all required fields!", Toast.LENGTH_SHORT).show();
                return;
            }

            //pass to main activity

            Bundle previousBundle = getArguments();
            if (previousBundle == null) {
                previousBundle = new Bundle();
            }

            // pass data
            previousBundle.putString("householdMembers", householdMembers);
            previousBundle.putString("otherPets", otherPets);
            previousBundle.putString("preferences1", preferences1);
            previousBundle.putString("preferences2", preferences2);

            // Pass the bundle to sign up
            SignUp signUp = (SignUp) getActivity();
            if (signUp != null) {
                signUp.onLifestyleDataPassed(previousBundle);
            }
        });

        return view;
    }


}

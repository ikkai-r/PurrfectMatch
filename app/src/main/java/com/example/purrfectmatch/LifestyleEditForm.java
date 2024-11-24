package com.example.purrfectmatch;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class LifestyleEditForm extends Fragment {

    private Spinner spinnerHousehold, spinnerOtherPets, spinnerPreferences, spinnerPreferences2;
    private Button buttonNext;
    private ArrayAdapter<String> householdAdapter, otherPetsAdapter, preferencesAdapter, preferences2Adapter;
    private FirebaseAuth mAuth;

    public LifestyleEditForm() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lifestyle_form_edit, container, false);

        mAuth = FirebaseAuth.getInstance();

        // Initialize Spinners
        spinnerHousehold = view.findViewById(R.id.spinnerHousehold);
        spinnerOtherPets = view.findViewById(R.id.spinnerOtherPets);
        spinnerPreferences = view.findViewById(R.id.spinnerPreferences);
        spinnerPreferences2 = view.findViewById(R.id.spinnerPreferences2);

        householdAdapter = new ArrayAdapter<>(getContext(),
                R.layout.spinner_item, new String[]{"1-2 members", "3-4 members", "5+ members"});
        householdAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinnerHousehold.setAdapter(householdAdapter);

        otherPetsAdapter = new ArrayAdapter<>(getContext(),
                R.layout.spinner_item, new String[]{"Yes", "No"});
        otherPetsAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinnerOtherPets.setAdapter(otherPetsAdapter);

        preferencesAdapter = new ArrayAdapter<>(getContext(),
                R.layout.spinner_item, new String[]{"Independent", "Moderately Clingy", "Affectionate"});
        preferencesAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinnerPreferences.setAdapter(preferencesAdapter);

        preferences2Adapter = new ArrayAdapter<>(getContext(),
                R.layout.spinner_item, new String[]{"Active / Playful", "Calm / Shy", "Curious"});
        preferences2Adapter.setDropDownViewResource(R.layout.spinner_item);
        spinnerPreferences2.setAdapter(preferences2Adapter);

        setUserInfo();

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

            // Pass bundle to the next form
            AboutEditForm aboutForm = new AboutEditForm();
            aboutForm.setArguments(previousBundle);

            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.formEdit, aboutForm).addToBackStack(null).commit();

        });

        return view;
    }

    private void setUserInfo() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("Users").document(user.getUid());

            // Fetch data from Firestore using the reference
            userRef.get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Extract data from the document snapshot
                            String householdMembersTxt = documentSnapshot.getString("householdMembers");
                            setSpinnerHousehold(householdMembersTxt);

                            String otherPetsTxt = documentSnapshot.getString("otherPets");
                            setSpinnerOtherPets(otherPetsTxt);

                            String preferences1Txt = documentSnapshot.getString("preferences1");
                            setSpinnerPreferences1(preferences1Txt);

                            String preferences2Txt = documentSnapshot.getString("preferences2");
                            setSpinnerPreferences2(preferences2Txt);
                        } else {
                            Log.d("fe", "user doesnt exist");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.d("fe", "failed to get info");
                    });
        } else {
            Log.d("fe", "user is not signed in");
        }
    }

    private void setSpinnerPreferences1(String preferences1Txt) {
        int spinnerPosition = preferencesAdapter.getPosition(preferences1Txt);
        spinnerPreferences.setSelection(spinnerPosition);
    }

    private void setSpinnerPreferences2(String preferences2Txt) {
        int spinnerPosition = preferences2Adapter.getPosition(preferences2Txt);
        spinnerPreferences2.setSelection(spinnerPosition);
    }

    private void setSpinnerOtherPets(String otherPetsTxt) {
        int spinnerPosition = otherPetsAdapter.getPosition(otherPetsTxt);
        spinnerOtherPets.setSelection(spinnerPosition);
    }

    private void setSpinnerHousehold(String householdMembers) {
        int spinnerPosition = householdAdapter.getPosition(householdMembers);
        spinnerHousehold.setSelection(spinnerPosition);
    }


}

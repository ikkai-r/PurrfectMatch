package com.example.purrfectmatch;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactEditForm extends Fragment {

    public ContactEditForm() {

    }

    private EditText phoneNumber, country, region, city;
    private Button buttonNext;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact_form_edit, container, false);

        phoneNumber = view.findViewById(R.id.phoneNumber);
        country = view.findViewById(R.id.country);
        region = view.findViewById(R.id.region);
        city = view.findViewById(R.id.city);
        buttonNext = view.findViewById(R.id.buttonNext);

        mAuth = FirebaseAuth.getInstance();
        setUserInfo();

        TextView backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pop the fragment back stack
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle previousBundle = getArguments();
                if (previousBundle == null) {
                    previousBundle = new Bundle();
                }

                // Validate all required fields and add to bundle
                String phoneNumberText = phoneNumber.getText().toString().trim();
                String countryText = country.getText().toString().trim();
                String regionText = region.getText().toString().trim();
                String cityText = city.getText().toString().trim();

                if (TextUtils.isEmpty(phoneNumberText) || TextUtils.isEmpty(countryText) ||
                        TextUtils.isEmpty(regionText) || TextUtils.isEmpty(cityText)) {
                    Toast.makeText(getContext(), "All fields are required", Toast.LENGTH_SHORT).show();
                    return;
                }

                previousBundle.putString("phoneNumber", phoneNumberText);
                previousBundle.putString("country", countryText);
                previousBundle.putString("region", regionText);
                previousBundle.putString("city", cityText);

                // add data to bundle
                LifestyleEditForm lifestyleForm = new LifestyleEditForm();
                lifestyleForm.setArguments(previousBundle);

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.formEdit, lifestyleForm).addToBackStack(null).commit();
            }
        });


        return view;
    }


    private void setUserInfo() {
        FirebaseUser user = mAuth.getCurrentUser();

        Bundle bundle = getArguments();


        if (user != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("Users").document(user.getUid());

            // Fetch data from Firestore using the reference
            userRef.get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Extract data from the document snapshot
                            String phonenumberTxt = documentSnapshot.getString("phoneNumber");

                            if (bundle != null) {
                                // Retrieve values from the bundle and set them to TextViews
                                String countryText = bundle.getString("country", documentSnapshot.getString("country")); // Default empty if null
                                String regionText = bundle.getString("region", documentSnapshot.getString("region"));
                                String cityText = bundle.getString("city", documentSnapshot.getString("city"));

                                Log.d("c", countryText + " " + regionText + " " + cityText);

                                country.setText(countryText);
                                region.setText(regionText);
                                city.setText(cityText);
                            }

                            // Display the data in the UI
                            phoneNumber.setText(phonenumberTxt);
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
}
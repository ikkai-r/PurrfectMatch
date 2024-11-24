package com.example.purrfectmatch;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class PersonalEditForm extends Fragment {

    public PersonalEditForm() {
    }

    private Spinner genderSpinner;
    private EditText firstname, lastname, age;
    private Button buttonNext;
    private FirebaseAuth mAuth;
    private ArrayAdapter<String> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_personal_form_edit, container, false);

        // Initialize spinner
        genderSpinner = view.findViewById(R.id.gender_spinner);

        adapter = new ArrayAdapter<>(
                getContext(),
                R.layout.spinner_item,
                new String[]{"Female", "Male", "Others", "Doesn't want to disclose"}
        );

        adapter.setDropDownViewResource(R.layout.spinner_item);
        genderSpinner.setAdapter(adapter);

        // Initialize text fields and button
        firstname = view.findViewById(R.id.firstname);
        lastname = view.findViewById(R.id.lastname);
        age = view.findViewById(R.id.age);
        buttonNext = view.findViewById(R.id.buttonNext);

        mAuth = FirebaseAuth.getInstance();

        //set known values
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
                // Get gender
                String gender = getSelectedGender();

                // Get Bundle from previous form
                Bundle previousBundle = getArguments();
                if (previousBundle == null) {
                    previousBundle = new Bundle();
                }

                // Check if all required fields are filled
                String firstnameText = firstname.getText().toString();
                String lastnameText = lastname.getText().toString();
                String ageText = age.getText().toString();

                if (TextUtils.isEmpty(firstnameText) || TextUtils.isEmpty(lastnameText)
                        || genderSpinner.getSelectedItem() == null) {
                    Toast.makeText(getActivity(), "Fill up required (*) fields!", Toast.LENGTH_SHORT).show();
                    return;
                }


                // Add data to the bundle
                previousBundle.putString("firstname", firstnameText);
                previousBundle.putString("lastname", lastnameText);
                previousBundle.putString("age", ageText);
                previousBundle.putString("gender", gender.toString());

                // Pass bundle to the next form
                ContactEditForm contactForm = new ContactEditForm();
                contactForm.setArguments(previousBundle);

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.formEdit, contactForm).addToBackStack(null).commit();

            }
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
                            String firstnameTxt = documentSnapshot.getString("firstName");
                            String lastnameTxt = documentSnapshot.getString("lastName");
                            String ageTxt = String.valueOf(documentSnapshot.getLong("age"));

                            String genderTxt = documentSnapshot.getString("gender");
                            setGenderSpinnerValue(genderTxt);

                            // Display the data in the UI
                            firstname.setText(firstnameTxt);
                            lastname.setText(lastnameTxt);
                            age.setText(ageTxt);
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

    private void setGenderSpinnerValue(String genderTxt) {
        int spinnerPosition = adapter.getPosition(genderTxt);
        genderSpinner.setSelection(spinnerPosition);
    }


    public String getSelectedGender() {
        return genderSpinner.getSelectedItem().toString();
    }
}

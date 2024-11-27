package com.example.purrfectmatch;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class CatGenEditForm extends Fragment {

    private EditText name, age, weight, breed, sex;
    private Button female, male, buttonNext;
    private RadioGroup radioGroup;
    //private String sex;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cat_gen_edit_form, container, false);

        db = FirebaseFirestore.getInstance();

        sex = view.findViewById(R.id.sexEdit);
        name = view.findViewById(R.id.name);
        age = view.findViewById(R.id.age);
        weight = view.findViewById(R.id.weightEdit);
        breed = view.findViewById(R.id.breed);
        buttonNext = view.findViewById(R.id.buttonNext6);
        //radioGroup = view.findViewById(R.id.genderRadioGroup);

        /*
        radioGroup.setOnCheckedChangeListener(((radioGroup1, i) -> {
            if (i == R.id.radioMale) {
                sex = "male";
            } else if (i == R.id.radioFemale) {
                sex = "female";
            }
        }));
        */


        // Fetch and display cat data if cat name is passed in arguments

        final Bundle previousBundle = getArguments();

        fetchCatData(previousBundle.getString("name"));


        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if all fields are not empty
                String nameInput = name.getText().toString().trim();
                String ageInput = age.getText().toString().trim();
                String weightInput = weight.getText().toString().trim();
                String breedInput = breed.getText().toString().trim();
                String sexInput = sex.getText().toString().trim();

                if (TextUtils.isEmpty(nameInput) || TextUtils.isEmpty(ageInput)
                        || TextUtils.isEmpty(weightInput) || TextUtils.isEmpty(breedInput) || TextUtils.isEmpty(sexInput)) {
                    Toast.makeText(getActivity(), "All fields are required!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Pass data to the next form
                //previousBundle.putString("name", nameInput);
                previousBundle.putString("age", ageInput);
                previousBundle.putString("weight", weightInput);
                previousBundle.putString("sex", sexInput);
                previousBundle.putString("breed", breedInput);

                CatLifeEditForm catLifeEditForm = new CatLifeEditForm();
                catLifeEditForm.setArguments(previousBundle);

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.cat_edit_form, catLifeEditForm).addToBackStack(null).commit();
            }
        });

        return view;
    }

    private void fetchCatData(String catName) {
        db.collection("Cats")
                .whereEqualTo("name",  catName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // Assuming there is only one match for the name
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);

                        // Populate fields with fetched data
                        name.setText(document.getString("name"));
                        age.setText(String.valueOf(document.getLong("age").intValue()));
                        weight.setText(String.valueOf(document.getLong("weight").intValue()));
                        breed.setText(document.getString("breed"));

                        breed.setText(document.getString("breed"));

                    } else {
                        Toast.makeText(getActivity(), "Cat not found or an error occurred.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed to fetch cat data.", Toast.LENGTH_SHORT).show());
    }
}

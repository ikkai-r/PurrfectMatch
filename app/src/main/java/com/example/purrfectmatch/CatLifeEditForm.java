package com.example.purrfectmatch;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CatLifeEditForm#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CatLifeEditForm extends Fragment {

    public CatLifeEditForm() {
        // Required empty public constructor
    }

    private FirebaseFirestore db;

    private EditText compatible, food, fee;
    private Button buttonNext;
    private RadioGroup radioGroup;
    private boolean isNeutered, chosen = false;
    private Spinner temperament, temperament2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cat_life_edit_form, container, false);

        db = FirebaseFirestore.getInstance();

        buttonNext = view.findViewById(R.id.buttonNext8);
        temperament = view.findViewById(R.id.temperament);
        compatible = view.findViewById(R.id.compatible);
        //food = view.findViewById(R.id.food);
        fee = view.findViewById(R.id.editTextCash);
        radioGroup = view.findViewById(R.id.radioGroup);
        temperament = view.findViewById(R.id.temperament);
        temperament2 = view.findViewById(R.id.food);

        radioGroup.setOnCheckedChangeListener(((radioGroup1, i) -> {
            if(i == R.id.radioNo){
                isNeutered = false;
                chosen = true;
            }else if(i == R.id.radioFemale){
                isNeutered = true;
                chosen = true;
            }
        }));

        final Bundle previousBundle = getArguments();

        fetchCatData(previousBundle.getString("name"));

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(),
                R.array.spinner_items, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(view.getContext(),
                R.array.spinner_items2, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        temperament.setAdapter(adapter2);
        temperament2.setAdapter(adapter);

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Check if all fields are not empty
                String temperamentInput = temperament.getSelectedItem().toString().trim();
                String compatibleInput = compatible.getText().toString().trim();
                String foodInput = temperament2.getSelectedItem().toString().trim();
                String feeInput = fee.getText().toString().trim();

                if (TextUtils.isEmpty(temperamentInput) || TextUtils.isEmpty(compatibleInput)
                        || TextUtils.isEmpty(foodInput) || TextUtils.isEmpty(feeInput)) {
                    Toast.makeText(getActivity(), "All fields are required!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // if all yes then pass as bundle

                previousBundle.putString("temperament", temperamentInput);
                previousBundle.putString("compatible", compatibleInput);
                previousBundle.putString("food", foodInput);
                previousBundle.putBoolean("isneutered", isNeutered);

                //Log.d("c", country + " " + city + " " + region);
                previousBundle.putString("fee", feeInput);

                CatPicEditForm catPicEditForm = new CatPicEditForm();
                catPicEditForm.setArguments(previousBundle);

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.cat_edit_form, catPicEditForm).addToBackStack(null).commit();
            }
        });

        return view;
    }

    private void fetchCatData(String catName) {
        db.collection("Cats")
                .whereEqualTo("name", catName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // Assuming there is only one match for the name
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);

                        // Populate fields with fetched data
                        //temperament.setText(document.getString("temperament"));
                        compatible.setText(document.getString("compatibleWith"));
                        //ood.setText(document.getString("foodPreference"));
                        fee.setText(String.valueOf(document.getLong("adoptionFee").intValue()));

                        //breed.setText(document.getString("breed"));

                        boolean fetchedNeutered = Boolean.TRUE.equals(document.getBoolean("isNeutered"));
                        if (fetchedNeutered) {
                            radioGroup.check(R.id.radioYes);
                            isNeutered = true;
                        } else{
                            radioGroup.check(R.id.radioNo);
                            isNeutered = false;
                        }

                    } else {
                        Toast.makeText(getActivity(), "Cat not found or an error occurred.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed to fetch cat data.", Toast.LENGTH_SHORT).show());
    }
}
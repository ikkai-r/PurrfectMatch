package com.example.purrfectmatch;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CatLifeForm#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CatLifeForm extends Fragment {

    public CatLifeForm() {
        // Required empty public constructor
    }

    private EditText temperament, compatible, food, fee;
    private Button buttonNext;
    private RadioGroup radioGroup;
    private boolean isNeutered, chosen = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cat_life_form, container, false);

        temperament = view.findViewById(R.id.temperament);
        compatible = view.findViewById(R.id.compatible);
        food = view.findViewById(R.id.foodText);
        fee = view.findViewById(R.id.editTextCash);
        buttonNext = view.findViewById(R.id.buttonNext2);
        radioGroup = view.findViewById(R.id.radioGroup);

        radioGroup.setOnCheckedChangeListener(((radioGroup1, i) -> {
            if(i == R.id.radioNo){
                isNeutered = false;
                chosen = true;
            }else if(i == R.id.radioFemale){
                isNeutered = true;
                chosen = true;
            }
        }));

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Check if all fields are not empty
                String temperamentInput = temperament.getText().toString().trim();
                String compatibleInput = compatible.getText().toString().trim();
                String foodInput = food.getText().toString().trim();
                String feeInput = fee.getText().toString().trim();

                if (TextUtils.isEmpty(temperamentInput) || TextUtils.isEmpty(compatibleInput)
                        || TextUtils.isEmpty(foodInput) || TextUtils.isEmpty(feeInput)) {
                    Toast.makeText(getActivity(), "All fields are required!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // if all yes then pass as bundle

                Bundle previousBundle = getArguments();
                if (previousBundle == null) {
                    previousBundle = new Bundle();
                }
                previousBundle.putString("temperament", temperamentInput);
                previousBundle.putString("compatible", compatibleInput);
                previousBundle.putString("food", foodInput);
                previousBundle.putBoolean("isneutered", isNeutered);

                //Log.d("c", country + " " + city + " " + region);
                previousBundle.putString("fee", feeInput);

                CatPicForm catPicForm = new CatPicForm();
                catPicForm.setArguments(previousBundle);

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.cat_form, catPicForm).addToBackStack(null).commit();
            }
        });

        return view;
    }
}
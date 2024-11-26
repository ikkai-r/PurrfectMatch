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
import android.widget.RadioGroup;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CatGenForm#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CatGenForm extends Fragment {


    public CatGenForm() {
        // Required empty public constructor
    }

    private EditText name, age, weight, breed;
    private Button female, male, buttonNext;
    private RadioGroup radioGroup;
    private String sex;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cat_gen_form, container, false);

        name = view.findViewById(R.id.name);
        age = view.findViewById(R.id.age);
        weight = view.findViewById(R.id.weight);
        breed = view.findViewById(R.id.breed);
        buttonNext = view.findViewById(R.id.buttonNext4);
        radioGroup = view.findViewById(R.id.genderRadioGroup);

        radioGroup.setOnCheckedChangeListener(((radioGroup1, i) -> {
            if(i == R.id.radioMale){
                sex = "male";
            }else if(i == R.id.radioFemale){
                sex = "female";
            }
        }));

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Check if all fields are not empty
                String nameInput = name.getText().toString().trim();
                String ageInput = age.getText().toString().trim();
                String weightInput = weight.getText().toString().trim();
                String breedInput = breed.getText().toString().trim();

                if (TextUtils.isEmpty(nameInput) || TextUtils.isEmpty(ageInput)
                        || TextUtils.isEmpty(weightInput) || TextUtils.isEmpty(breedInput) || TextUtils.isEmpty(sex)) {
                    Toast.makeText(getActivity(), "All fields are required!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // if all yes then pass as bundle

                Bundle bundle = new Bundle();
                bundle.putString("name", nameInput);
                bundle.putString("age", ageInput);
                bundle.putString("weight", weightInput);
                bundle.putString("sex", sex);

                //Log.d("c", country + " " + city + " " + region);
                bundle.putString("breed", breedInput);

                CatLifeForm catLifeForm = new CatLifeForm();
                catLifeForm.setArguments(bundle);

                // Pass bundle to the next form
                AboutForm aboutForm = new AboutForm();
                aboutForm.setArguments(bundle);

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.cat_form, catLifeForm).addToBackStack(null).commit();
            }
        });

        return view;
    }

}
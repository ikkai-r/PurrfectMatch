package com.example.purrfectmatch;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 */
public class CatAboutEditForm extends Fragment {


    public CatAboutEditForm() {
        // Required empty public constructor
    }

    private EditText about;
    private Button buttonNext;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cat_about_edit_form, container, false);

        db = FirebaseFirestore.getInstance();

        about = view.findViewById(R.id.aboutMeEditText);
        buttonNext = view.findViewById(R.id.buttonNext9);

        final Bundle previousBundle = getArguments();

        fetchCatData(previousBundle.getString("name"));

        buttonNext.setOnClickListener(v -> {
            // Handle the next button click event


            String aboutInput = about.getText().toString().trim();

            if (TextUtils.isEmpty(aboutInput)) {
                Toast.makeText(getActivity(), "All fields are required!", Toast.LENGTH_SHORT).show();
                return;
            }

            previousBundle.putString("about", aboutInput);


            // Pass the bundle to sign up
            EditCat editCat = (EditCat) getActivity();
            if (editCat != null) {
                editCat.onDataPassed(previousBundle);
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
                        about.setText(document.getString("bio"));

                    } else {
                        Toast.makeText(getActivity(), "Cat not found or an error occurred.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed to fetch cat data.", Toast.LENGTH_SHORT).show());
    }
}
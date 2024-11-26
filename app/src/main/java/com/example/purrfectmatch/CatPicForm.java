package com.example.purrfectmatch;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * */
public class CatPicForm extends Fragment {

    public CatPicForm() {
        // Required empty public constructor
    }

    private ImageView image;
    private Uri photoUri;
    private Button buttonNext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cat_pic_form, container, false);

        image = view.findViewById(R.id.largeImageView);
        buttonNext = view.findViewById(R.id.buttonNext3);

        image.setOnClickListener(v -> choosePicture());

        buttonNext.setOnClickListener(v -> {
            // Handle the next button click event

            // Bundle data to pass to the next step (e.g., SignUp activity or another fragment)
            Bundle previousBundle = getArguments();
            if (previousBundle == null) {
                previousBundle = new Bundle();
            }

            if (photoUri != null) {
                previousBundle.putString("catimg", photoUri.toString());
            }

            // Log the values for debugging (you can remove this in production)
            Log.d("AboutForm", "Profile Image URI: " + (photoUri != null ? photoUri.toString() : "No Image"));

            // Pass bundle to the next form
            CatAboutForm catAboutForm = new CatAboutForm();
            catAboutForm.setArguments(previousBundle);

            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.cat_form, catAboutForm).addToBackStack(null).commit();
        });

        return view;

    }

    private void choosePicture() {
        // Create an Intent to pick a file (image)
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");  // Only allow image files, can be changed to "*/*" for all files
        launcher.launch(intent);
    }

    // Handle the result of the image picker
    private final ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    photoUri = result.getData().getData();
                    // Show the selected image in ImageView
                    image.setImageURI(photoUri);

                }
            }
    );
}
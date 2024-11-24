package com.example.purrfectmatch;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutEditForm extends Fragment {

    public AboutEditForm() {
        // Required empty public constructor
    }

    private EditText bio;
    private Button buttonNext, buttonUpload;
    private ImageView profileImageView;
    private Uri photoUri;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about_form_edit, container, false);

        mAuth = FirebaseAuth.getInstance();

        bio = view.findViewById(R.id.bio);
        buttonUpload = view.findViewById(R.id.buttonUpload);
        buttonNext = view.findViewById(R.id.buttonNext);
        profileImageView = view.findViewById(R.id.profileImageView);  // ImageView to show selected image

        setUserInfo();

        TextView backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            // Pop the fragment back stack
            getActivity().getSupportFragmentManager().popBackStack();
        });

        buttonUpload.setOnClickListener(v -> choosePicture());

        buttonNext.setOnClickListener(v -> {
            // Handle the next button click event

            // Retrieve and validate all required fields
            String bioText = bio.getText().toString().trim();
            if (TextUtils.isEmpty(bioText)) {
                Toast.makeText(getActivity(), "Please fill in the bio", Toast.LENGTH_SHORT).show();
                return;
            }

            // Bundle data to pass to the next step (e.g., SignUp activity or another fragment)
            Bundle previousBundle = getArguments();
            if (previousBundle == null) {
                previousBundle = new Bundle();
            }

            // Add bio text and photo URI to the bundle
            previousBundle.putString("bio", bioText);
            if (photoUri != null) {
                previousBundle.putString("profileimg", photoUri.toString());
            }

            // Log the values for debugging (you can remove this in production)
            Log.d("AboutForm", "Bio: " + bioText);
            Log.d("AboutForm", "Profile Image URI: " + (photoUri != null ? photoUri.toString() : "No Image"));

            // Pass the bundle to sign up
            SignUp signUp = (SignUp) getActivity();
            if (signUp != null) {
                signUp.onDataPassed(previousBundle);
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
                            String bioTxt = documentSnapshot.getString("bio");

                            // Display the data in the UI
                            bio.setText(bioTxt);
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

    // Launch intent to select an image
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
                    profileImageView.setImageURI(photoUri);

                }
            }
    );
}

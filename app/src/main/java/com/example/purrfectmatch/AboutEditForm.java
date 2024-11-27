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

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

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
    private String cloudName = "djdeqyhes";
    private String imageUrl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about_form_edit, container, false);

        bio = view.findViewById(R.id.bio);
        buttonUpload = view.findViewById(R.id.buttonUpload);
        buttonNext = view.findViewById(R.id.buttonNext);
        profileImageView = view.findViewById(R.id.profileImageView);  // ImageView to show selected image

        setUserInfo();
        initCloudinary();

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
            if (imageUrl != null) {
                previousBundle.putString("profileimg", imageUrl);
            }

            // Log the values for debugging (you can remove this in production)
            Log.d("AboutForm", "Bio: " + bioText);
            Log.d("AboutForm", "Profile Image URI: " + (photoUri != null ? photoUri.toString() : "No Image"));

            // Pass the bundle to sign up
            EditUser editUser = (EditUser) getActivity();
            if (editUser != null) {
                editUser.onDataPassed(previousBundle);
            }
        });

        return view;
    }

    private void setUserInfo() {

        Bundle bundle = getArguments();

        if (bundle != null) {
            // Retrieve values from the bundle and set them to TextViews
            String bioText = bundle.getString("bio");
            String profileImageUrl = bundle.getString("profileimg");

            bio.setText(bioText);
            Glide.with(requireContext()).load(profileImageUrl).into(profileImageView);

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
                    uploadImage();

                }
            }
    );

    private void initCloudinary() {
        Map config = new HashMap();
        config.put("cloud_name", cloudName);
        config.put("api_key", "267458959441425");
        config.put("api_secret", "yx3jJ7kIqnno467x2_5DnNWsmAA"); //TODO: hide this
        MediaManager.init(requireContext(), config);
    }


    private void uploadImage() {
        MediaManager.get().upload(photoUri).callback(new UploadCallback() {
            @Override
            public void onStart(String requestId) {
                Log.d("Cloudinary Quickstart", "Upload start");
            }

            @Override
            public void onProgress(String requestId, long bytes, long totalBytes) {
                Log.d("Cloudinary Quickstart", "Upload progress");
            }

            @Override
            public void onSuccess(String requestId, Map resultData) {
                Log.d("Cloudinary Quickstart", "Upload success");
                imageUrl = (String) resultData.get("secure_url");
                Glide.with(requireContext()).load(imageUrl).into(profileImageView);
            }

            @Override
            public void onError(String requestId, ErrorInfo error) {
                Log.d("Cloudinary Quickstart", "Upload failed");
            }

            @Override
            public void onReschedule(String requestId, ErrorInfo error) {

            }
        }).dispatch();


    }
}

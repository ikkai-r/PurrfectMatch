package com.example.purrfectmatch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class CatPicForm extends Fragment {

    public CatPicForm() {
        // Required empty public constructor
    }

    private ImageView image;
    private Uri photoUri;
    private Button buttonNext;
    private String savedImagePath, imageUrl;
    private String cloudName = "djdeqyhes";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cat_pic_form, container, false);

        image = view.findViewById(R.id.largeImageView);
        buttonNext = view.findViewById(R.id.buttonNext3);

        image.setOnClickListener(v -> choosePicture());

        initCloudinary();

        buttonNext.setOnClickListener(v -> {
            // Handle the next button click event

            // Save the image to internal storage
            if (image.getDrawable() != null) {
                savedImagePath = saveImageToInternalStorage();
            }

            // Bundle data to pass to the next step (e.g., SignUp activity or another fragment)
            Bundle previousBundle = getArguments();
            if (previousBundle == null) {
                previousBundle = new Bundle();
            }

            if (imageUrl != null) {
                Log.d("test", "HERE!" + imageUrl);
                previousBundle.putString("catimg", imageUrl);
            }

            // Log the values for debugging (you can remove this in production)
            Log.d("AboutForm", "Saved Image Path: " + savedImagePath);

            // Pass bundle to the next form
            CatAboutForm catAboutForm = new CatAboutForm();
            catAboutForm.setArguments(previousBundle);

            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.cat_form, catAboutForm).addToBackStack(null).commit();
        });

        return view;
    }

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
                Glide.with(requireContext()).load(imageUrl).into(image);
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

    private String saveImageToInternalStorage() {
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();

        // Define the file name and path
        File directory = getActivity().getDir("images", Context.MODE_PRIVATE);
        File imageFile = new File(directory, "cat_image_" + System.currentTimeMillis() + ".png");

        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            // Compress and save the bitmap to the file
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            Toast.makeText(getContext(), "Image Saved", Toast.LENGTH_SHORT).show();
            return imageFile.getAbsolutePath(); // Return the saved image path
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Failed to Save Image", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

}

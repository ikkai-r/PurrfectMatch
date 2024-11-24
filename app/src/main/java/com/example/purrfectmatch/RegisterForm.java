package com.example.purrfectmatch;

import android.app.Activity;
import android.location.LocationListener;
import android.Manifest;

import android.app.Person;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterForm extends Fragment implements LocationListener {

    public RegisterForm() {

    }

    LocationManager locationManager;

    private EditText email, password, username, match_pass;
    private String country, city, region;
    private Button buttonNext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_form, container, false);

        // Check permissions first
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                    101);
        } else {
            getLocation();
        }


        //locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
        //locationEnabled();
        //getLocation();


        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);
        username = view.findViewById(R.id.username);
        match_pass = view.findViewById(R.id.match_pass);
        buttonNext = view.findViewById(R.id.buttonNext);

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Check if all fields are not empty
                String emailText = email.getText().toString().trim();
                String passwordText = password.getText().toString().trim();
                String usernameText = username.getText().toString().trim();
                String matchPassText = match_pass.getText().toString().trim();

                if (TextUtils.isEmpty(emailText) || TextUtils.isEmpty(passwordText)
                        || TextUtils.isEmpty(usernameText) || TextUtils.isEmpty(matchPassText)) {
                    Toast.makeText(getActivity(), "All fields are required!", Toast.LENGTH_SHORT).show();
                    return;
                }

                //require password to be 6 characters long
                if(passwordText.length() < 6) {
                    Toast.makeText(getActivity(), "Password should be 6 characters long.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // check if match_pass and password are matching
                if (!passwordText.equals(matchPassText)) {
                    Toast.makeText(getActivity(), "Passwords do not match!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // if all yes then pass as bundle

                Bundle bundle = new Bundle();
                bundle.putString("email", emailText);
                bundle.putString("password", passwordText);
                bundle.putString("username", usernameText);

                Log.d("c", country + " " + city + " " + region);
                bundle.putString("country", country);
                bundle.putString("city", city);
                bundle.putString("region", region);

                PersonalForm personalForm = new PersonalForm();
                personalForm.setArguments(bundle);

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.form, personalForm).addToBackStack(null).commit();
            }
        });


        return view;
    }

//    private void locationEnabled() {
//        LocationManager lm = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
//        boolean gps_enabled = false;
//        boolean network_enabled = false;
//        try {
//            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        try {
//            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        if (!gps_enabled && !network_enabled) {
//            new AlertDialog.Builder(this.getContext())
//                    .setTitle("Enable GPS Service")
//                    .setMessage("We need your GPS location.")
//                    .setCancelable(false)
//                    .setPositiveButton("Enable", new
//                            DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
//                                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//                                }
//                            })
//                    .setNegativeButton("Cancel", null)
//                    .show();
//        }
//    }

    void getLocation() {
        try {
            Log.d("loc", "getting location");
            locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);

            // Check if the GPS is enabled
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, this);
            } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10, this);
            } else {
                // Prompt to enable location services
                new AlertDialog.Builder(requireContext())
                        .setTitle("Enable Location Services")
                        .setMessage("Location services are turned off. Please enable to proceed.")
                        .setPositiveButton("Enable", (dialog, which) -> startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                        .setNegativeButton("Cancel", null)
                        .show();
            }

        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    void getLocationA() {
        try {
            Log.d("loc", "getting location");
            locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 5, this);
            if(locationManager!=null) {
                Log.d("did location", "location manager not null");
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("ca", "Location changed triggered");
        if (location != null) {
            try {
                Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (addresses != null && !addresses.isEmpty()) {
                    country = addresses.get(0).getCountryName();
                    city = addresses.get(0).getLocality();
                    region = addresses.get(0).getAdminArea();
                    Log.d("c", "Location found: " + country + ", " + city + ", " + region);
                }
            } catch (Exception e) {
                Log.e("Location Error", "Error getting location", e);
            }
        }
    }



    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


}

package com.example.purrfectmatch;

import android.app.Person;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterForm extends Fragment {

    public RegisterForm() {

    }

    private EditText email, password, username, match_pass;
    private Button buttonNext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_form, container, false);

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

                PersonalForm personalForm = new PersonalForm();
                personalForm.setArguments(bundle);

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.form, personalForm).commit();
            }
        });


        return view;
    }
}
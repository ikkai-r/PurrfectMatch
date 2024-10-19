package com.example.purrfectmatch;

import android.content.Intent;
import android.os.Bundle;
import android.app.Dialog;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ViewApplication extends AppCompatActivity {

    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_application);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_reject_app);

        Button rejectButton = dialog.findViewById(R.id.dialog_reject_button);
        Button cancelButton = dialog.findViewById(R.id.dialog_cancel_button);

        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform the delete action here
                Intent i = new Intent(ViewApplication.this, ShelterPage.class);
                // Close the dialog
                ViewApplication.this.startActivity(i);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Just dismiss the dialog
                dialog.dismiss();
            }
        });

    }

    public void rejectApp(View v) {
        dialog.show();
    }

    public void acceptApp(View v) {
        Intent i = new Intent(this, ScheduleShelter.class);
        this.startActivity(i);
    }
}
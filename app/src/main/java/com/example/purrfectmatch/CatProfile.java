package com.example.purrfectmatch;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CatProfile extends AppCompatActivity {

    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cat_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_delete_cat);

        Button deleteButton = dialog.findViewById(R.id.dialog_delete_button);
        Button cancelButton = dialog.findViewById(R.id.dialog_cancel_button);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform the delete action here
                Intent i = new Intent(CatProfile.this, SuccessForm.class);
                i.putExtra("title", "Profile successfully deleted:");
                i.putExtra("title_big", "Dweety");
                i.putExtra("subtitle_1", "Sad to see you go :(");
                i.putExtra("subtitle_2", "");
                i.putExtra("button_text", "Okay");
                // Close the dialog
                CatProfile.this.startActivity(i);
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

    public void editCat(View v) {
        Intent i = new Intent(this, EditCat.class);
        this.startActivity(i);
    }


    public void deleteCat(View v) {
        dialog.show();
    }
}
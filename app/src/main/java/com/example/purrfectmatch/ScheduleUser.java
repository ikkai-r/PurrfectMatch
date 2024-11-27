package com.example.purrfectmatch;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class ScheduleUser extends AppCompatActivity {

    private TextView timePicker, datePicker, endTimePicker, subheading;
    private EditText venuePicker;

    private String selectedDate, selectedTime;
    private String startDate, endDate, startTime, endTime;

    private String appId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_schedule_shelter);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        datePicker = findViewById(R.id.datePicker);
        venuePicker = findViewById(R.id.venuePicker);
        timePicker = findViewById(R.id.startTimePicker);
        endTimePicker = findViewById(R.id.endTimePicker);
        subheading = findViewById(R.id.subheading);

        endTimePicker.setVisibility(View.GONE);
        venuePicker.setFocusable(false);
        venuePicker.setClickable(false);

        Intent intent = getIntent();
        appId = intent.getStringExtra("applicationId");

        fetchApplicationDetails();

        datePicker.setOnClickListener(this::showDatePicker);
        timePicker.setOnClickListener(this::showTimePicker);
    }

    private void fetchApplicationDetails() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference appRef = db.collection("Applications").document(appId);

        appRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                startDate = documentSnapshot.getString("startDate");
                endDate = documentSnapshot.getString("endDate");
                startTime = documentSnapshot.getString("startTime");
                endTime = documentSnapshot.getString("endTime");
                String venue = documentSnapshot.getString("venue");

                Log.d("sched", "Fetched details: " + startDate + " to " + endDate + ", " + startTime + " to " + endTime);

                // Update subheading
                subheading.setText("Book an appointment between " +
                        startDate + " and " + endDate + ", " +
                        startTime + " to " + endTime);

                // Set venue text
                venuePicker.setText("Venue: " + (venue != null ? venue : "Not specified"));
            } else {
                Toast.makeText(this, "Application details not found.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(e -> {
            Log.d("sched", "Error fetching details: " + e.getMessage());
            Toast.makeText(this, "Error fetching application details.", Toast.LENGTH_SHORT).show();
            finish();
        });
    }


    public void schedSuccess(View v) {
        if (selectedDate == null || selectedTime == null) {
            Toast.makeText(this, "Please complete all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference scheduleRef = db.collection("Applications").document(appId);

        HashMap<String, Object> scheduleData = new HashMap<>();
        scheduleData.put("finalDate", selectedDate);
        scheduleData.put("finalTime", selectedTime);
        scheduleData.put("status", "scheduled");

        scheduleRef.update(scheduleData)
                .addOnSuccessListener(aVoid -> {
                    Log.d("sched", "Scheduled successfully");
                    Intent i = new Intent(ScheduleUser.this, SuccessForm.class);
                    i.putExtra("title", "Application status updated to");
                    i.putExtra("title_big", "Scheduled");
                    i.putExtra("subtitle_1", "Successfully scheduled for appointment");
                    i.putExtra("button_text", "Okay");
                    i.putExtra("user_type", "user");
                    startActivity(i);
                })
                .addOnFailureListener(e -> Log.d("sched", "Error saving schedule: " + e.getMessage()));
    }

    private void showDatePicker(View view) {
        if (startDate == null || endDate == null) {
            Toast.makeText(this, "Please wait for application details to load.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Parse the start and end dates
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select a Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds());

        MaterialDatePicker<Long> datePicker = builder.build();
        datePicker.show(getSupportFragmentManager(), "DATE_PICKER");

        datePicker.addOnPositiveButtonClickListener(selection -> {
            String selected = dateFormat.format(new Date(selection));
            try {
                Date selectedDate = dateFormat.parse(selected);
                Date startDateObj = dateFormat.parse(startDate);
                Date endDateObj = dateFormat.parse(endDate);

                if (!selectedDate.before(startDateObj) && !selectedDate.after(endDateObj)) {
                    this.selectedDate = selected;
                    this.datePicker.setText(selected);
                } else {
                    Toast.makeText(this, "Please select a date within the specified range.", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e("DatePicker", "Error parsing dates", e);
                Toast.makeText(this, "Error selecting date.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showTimePicker(View view) {
        if (startTime == null || endTime == null) {
            Toast.makeText(this, "Please wait for application details to load.", Toast.LENGTH_SHORT).show();
            return;
        }

        MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setTitleText("Select Time")
                .build();

        timePicker.addOnPositiveButtonClickListener(v -> {
            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();
            String formattedTime = formatTime(hour, minute);

            if (isTimeWithinRange(hour, minute)) {
                selectedTime = formattedTime;
                this.timePicker.setText(selectedTime);
            } else {
                Toast.makeText(this, "Please select a time within the specified range.", Toast.LENGTH_SHORT).show();
            }
        });

        timePicker.show(getSupportFragmentManager(), "TIME_PICKER");
    }

    private boolean isTimeWithinRange(int hour, int minute) {
        try {
            // Convert 24-hour format to 12-hour format for comparison
            int hour12 = hour % 12;
            hour12 = (hour12 == 0) ? 12 : hour12;
            String amPm = (hour < 12) ? "AM" : "PM";

            String selectedTimeStr = String.format(Locale.getDefault(), "%02d:%02d %s", hour12, minute, amPm);

            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            Date selectedTime = timeFormat.parse(selectedTimeStr);
            Date startTimeObj = timeFormat.parse(startTime);
            Date endTimeObj = timeFormat.parse(endTime);

            return !selectedTime.before(startTimeObj) && !selectedTime.after(endTimeObj);
        } catch (Exception e) {
            Log.e("TimePicker", "Error parsing times", e);
            return false;
        }
    }

    private String formatTime(int hour, int minute) {
        String amPm = (hour < 12) ? "AM" : "PM";
        int hour12 = (hour == 0 || hour == 12) ? 12 : hour % 12;
        return String.format(Locale.getDefault(), "%02d:%02d %s", hour12, minute, amPm);
    }
}

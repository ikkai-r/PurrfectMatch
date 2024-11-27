package com.example.purrfectmatch;

import android.app.TimePickerDialog;
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
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class ScheduleShelter extends AppCompatActivity {

    private TextView startTimePicker;
    private TextView endTimePicker;
    private TextView datePicker;
    private EditText venuePicker;

    private int startHour, startMinute;
    private int endHour, endMinute;
    private String startDate, endDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_schedule_shelter);

        // Handle edge-to-edge insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI components
        datePicker = findViewById(R.id.datePicker);
        venuePicker = findViewById(R.id.venuePicker);

        startTimePicker = findViewById(R.id.startTimePicker);
        endTimePicker = findViewById(R.id.endTimePicker);

        // Set onClick listeners for time pickers
        startTimePicker.setOnClickListener(this::showStartTimePicker);
        endTimePicker.setOnClickListener(this::showEndTimePicker);
        datePicker.setOnClickListener(this::showDateRangePicker);
    }

    public void schedSuccess(View v) {
        // Retrieve the app ID from the Intent (assuming the app ID was passed via Intent)
        Intent intent = getIntent();
        String appId = intent.getStringExtra("appId"); // Replace "app_id" with the actual key you're using
        Log.d("sched", appId);

        // Retrieve schedule details (e.g., start date, end date, start time, end time)
        String startTime = startTimePicker.getText().toString();
        String endTime = endTimePicker.getText().toString();
        String venue = venuePicker.getText().toString();

        // Initialize Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a new document in the Firestore collection for schedules
        DocumentReference scheduleRef = db.collection("Applications").document(appId);

        HashMap<String, Object> scheduleData = new HashMap<>();
        scheduleData.put("startDate", startDate); // Set the start date
        scheduleData.put("endDate", endDate); // Set the end date
        scheduleData.put("startTime", startTime); // Set the start time
        scheduleData.put("endTime", endTime); // Set the end time
        scheduleData.put("venue", venue); // Set the venue
        scheduleData.put("status", "reviewed");
        // Set the schedule data in Firestore
        scheduleRef.update(scheduleData)
                .addOnSuccessListener(aVoid -> {
            // Successfully saved the schedule to Firestore
            Log.d("sched", "Scheduled success");
            // After saving, start the ShelterPage activity
            Intent i = new Intent(ScheduleShelter.this, SuccessForm.class);
            i.putExtra("title", "Adopter Application");
            i.putExtra("title_big", "Reviewed");
            i.putExtra("subtitle_1", "Successfully scheduled for appointment");
            i.putExtra("button_text", "Okay");
            i.putExtra("user_type", "shelter");
            startActivity(i);
        }).addOnFailureListener(e -> {
            // Handle failure
            Log.d("sched", "Error saving schedule");
        });
    }


    private void showDateRangePicker(View view) {
        Calendar calendar = Calendar.getInstance();
        long today = calendar.getTimeInMillis();

        calendar.add(Calendar.MONTH, -1);
        long minDate = calendar.getTimeInMillis();
        calendar.add(Calendar.MONTH, 2);
        long maxDate = calendar.getTimeInMillis();

        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Select a Date Range")
                .setCalendarConstraints(
                        new CalendarConstraints.Builder()
                                .setStart(minDate)
                                .setEnd(maxDate)
                                .build()
                )
                .setSelection(new Pair<>(today, today));

        MaterialDatePicker<Pair<Long, Long>> dateRangePicker = builder.build();

        dateRangePicker.show(getSupportFragmentManager(), "DATE_RANGE_PICKER");

        dateRangePicker.addOnPositiveButtonClickListener(selection -> {
            if (selection != null) {
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                startDate = formatter.format(new Date(selection.first));
                endDate = formatter.format(new Date(selection.second));

                Log.d("sched", startDate + "-" + endDate);
                datePicker.setText(startDate + "-" + endDate);
            }
        });

        dateRangePicker.addOnNegativeButtonClickListener(dialog -> {
            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
        });
    }

    private void showStartTimePicker(View view) {
        // Get current time
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Create MaterialTimePicker for start time
        MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                .setHour(hour)
                .setMinute(minute)
                .setTimeFormat(TimeFormat.CLOCK_12H) // Use 12-hour format
                .setTitleText("Select Start Time")
                .build();

        // Set positive button listener
        timePicker.addOnPositiveButtonClickListener(v -> {
            startHour = timePicker.getHour();
            startMinute = timePicker.getMinute();
            String formattedTime = formatTime(startHour, startMinute);
            startTimePicker.setText(formattedTime); // Set the start time on TextView
            Log.d("sched", "Here is " + formattedTime);
        });

        // Show the time picker
        timePicker.show(getSupportFragmentManager(), "start_time_picker");
    }

    private void showEndTimePicker(View view) {
        // Get current time
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Create MaterialTimePicker for end time
        MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                .setHour(hour)
                .setMinute(minute)
                .setTimeFormat(TimeFormat.CLOCK_12H) // Use 12-hour format
                .setTitleText("Select End Time")
                .build();

        // Set positive button listener
        timePicker.addOnPositiveButtonClickListener(v -> {
            endHour = timePicker.getHour();
            endMinute = timePicker.getMinute();
            String formattedTime = formatTime(endHour, endMinute);
            endTimePicker.setText(formattedTime); // Set the end time on TextView
            Log.d("sched", "Here is end time" + formattedTime);

            // Check if end time is after start time
            if (!isEndTimeAfterStartTime()) {
                Toast.makeText(ScheduleShelter.this, "End time must be after start time", Toast.LENGTH_SHORT).show();
                endTimePicker.setText(""); // Clear end time picker
            }
        });

        // Show the time picker
        timePicker.show(getSupportFragmentManager(), "end_time_picker");
    }

    private String formatTime(int hour, int minute) {
        // Convert to 12-hour format
        String amPm = (hour < 12) ? "AM" : "PM";
        int hour12 = (hour > 12) ? hour - 12 : hour;
        if (hour12 == 0) hour12 = 12; // Convert 0 to 12 for 12-hour format
        return String.format(Locale.getDefault(), "%02d:%02d %s", hour12, minute, amPm);
    }

    private boolean isEndTimeAfterStartTime() {
        // Convert start and end times to minutes
        int startTimeInMinutes = startHour * 60 + startMinute;
        int endTimeInMinutes = endHour * 60 + endMinute;

        // Check if end time is after start time
        return endTimeInMinutes > startTimeInMinutes;
    }

}

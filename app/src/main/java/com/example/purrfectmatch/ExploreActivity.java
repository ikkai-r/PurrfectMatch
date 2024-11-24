package com.example.purrfectmatch;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ExploreActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ExploreAdapter adapter;
    private List<ExploreData> exploreList;
    private List<ExploreData> filteredList;
    private FirebaseFirestore db;

    private EditText searchBar;
    private ImageView profile, explore, swipe;
    private TextView filter;

    private int maxAge = -1, maxAdoptionFee = -1;
    private String selectedSex = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        profile = findViewById(R.id.profile);
        explore = findViewById(R.id.explore);
        swipe = findViewById(R.id.swipe);
        filter = findViewById(R.id.filter);

        exploreList = new ArrayList<>();
        filteredList = new ArrayList<>(exploreList);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        adapter = new ExploreAdapter(exploreList, ExploreActivity.this);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        fetchCats();

        searchBar = findViewById(R.id.searchBar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                searchCats(charSequence.toString());
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });

        adapter.setOnItemClickListener(cat -> {
            Intent i = new Intent(ExploreActivity.this, ClickedExploreActivity.class);
            i.putExtra("documentId", cat.getId());
            startActivity(i);
        });

        filter.setOnClickListener(view -> {
            List<Object> result = createPopup(R.layout.popup_filter, view);

            View popupView = (View) result.get(0);
            PopupWindow popupWindow = (PopupWindow) result.get(1);
            ViewGroup rootLayout = (ViewGroup) result.get(2);

            EditText ageEditText = popupView.findViewById(R.id.filterAgeEditText);
            EditText feeEditText = popupView.findViewById(R.id.filterAdoptionFeeEditText);
            RadioGroup sexRadioGroup = popupView.findViewById(R.id.filterSexRadioGroup);

            Button applyButton = popupView.findViewById(R.id.filterApplyButton);
            Button resetButton = popupView.findViewById(R.id.filterResetButton);

            // Set previously saved values
            if (maxAge != -1) {
                ageEditText.setText(String.valueOf(maxAge));
            }

            if (maxAdoptionFee != -1) {
                feeEditText.setText(String.valueOf(maxAdoptionFee));
            }

            if (selectedSex.equals("M")) {
                sexRadioGroup.check(R.id.filterSexMale);
            } else if (selectedSex.equals("F")) {
                sexRadioGroup.check(R.id.filterSexFemale);
            }

            resetButton.setOnClickListener(filterView -> {
                // Reset filters to default
                maxAdoptionFee = -1;
                maxAge = -1;
                selectedSex = "All";

                filter.setText("Filter");
                adapter.updateList(exploreList);
                popupWindow.dismiss();
            });

            applyButton.setOnClickListener(filterView -> {
                // Capture filter values
                filter.setText("Filter\n(Filtered)");
                if (!ageEditText.getText().toString().isEmpty()) {
                    maxAge = Integer.parseInt(ageEditText.getText().toString());
                }

                if (!feeEditText.getText().toString().isEmpty()) {
                    maxAdoptionFee = Integer.parseInt(feeEditText.getText().toString());
                }

                int selectedSexId = sexRadioGroup.getCheckedRadioButtonId();
                if (selectedSexId == R.id.filterSexMale) {
                    selectedSex = "M";
                } else if (selectedSexId == R.id.filterSexFemale) {
                    selectedSex = "F";
                } else {
                    selectedSex = "All";
                }

                applyFilter();
                popupWindow.dismiss();
            });


        });

        profile.setOnClickListener(view -> {
            Intent i = new Intent(this, ProfileActivity.class);
            startActivity(i);
            finish();
        });

        swipe.setOnClickListener(view -> {
            Intent i = new Intent(this, SwipeActivity.class);
            startActivity(i);
            finish();
        });
    }

    private void fetchCats() {
        db.collection("Cats")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Toast.makeText(ExploreActivity.this, "Error fetching data: "
                                    + error.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        exploreList.clear();
                        for (DocumentSnapshot snapshot : value.getDocuments()) {
                            try {
                                ExploreData cat = snapshot.toObject(ExploreData.class);
                                if (cat != null) {
                                    cat.setId(snapshot.getId());
                                    exploreList.add(cat);
                                }
                            } catch (Exception e) {
                                Toast.makeText(ExploreActivity.this, "Error parsing data: "
                                        + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        filteredList = new ArrayList<>(exploreList);
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private List<Object> createPopup(int layoutRes, View view) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(layoutRes, null);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;

        int width = (int) (screenWidth * 0.8);
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;

        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.showAtLocation(view, Gravity.CENTER, 30, 0);

        View overlay = new View(this);
        overlay.setBackgroundColor(Color.parseColor("#111111"));
        overlay.setAlpha(0.6f);

        ViewGroup rootLayout = findViewById(android.R.id.content);
        rootLayout.addView(overlay);

        popupWindow.showAtLocation(view, Gravity.CENTER, 30, 0);

        popupWindow.setOnDismissListener(() -> rootLayout.removeView(overlay));

        List<Object> result = new ArrayList<>();
        result.add(popupView);       // Index 0: popupView
        result.add(popupWindow);     // Index 1: popupWindow
        result.add(rootLayout);      // Index 2: rootLayout
        result.add(overlay);         // Index 3: Overlay

        return result;
    }

    private void applyFilter() {
        List<ExploreData> filteredList = new ArrayList<>();

        // Apply search filter if there is any query
        String searchQuery = searchBar.getText().toString().toLowerCase();

        for (ExploreData cat : exploreList) {
            boolean isValid = true;

            if (cat.getAge() > maxAge && maxAge != -1) {
                isValid = false;
            }

            if (cat.getAdoptionFee() > maxAdoptionFee && maxAdoptionFee != -1) {
                isValid = false;
            }

            if (!selectedSex.equals("All") && !cat.getSex().equalsIgnoreCase(selectedSex)) {
                isValid = false;
            }

            // Apply search filter (if any)
            if (!cat.getName().toLowerCase().contains(searchQuery)) {
                isValid = false;
            }

            if (isValid) {
                filteredList.add(cat);
            }
        }

        this.filteredList = filteredList;
        adapter.updateList(filteredList);
    }


    private void searchCats(String query) {
        filteredList = new ArrayList<>();

        if (query.isEmpty()) {
            applyFilter();
        } else {
            for (ExploreData cat : exploreList) {
                boolean isValid = true;

                if (cat.getAge() > maxAge && maxAge != -1) {
                    isValid = false;
                }

                if (cat.getAdoptionFee() > maxAdoptionFee && maxAdoptionFee != -1) {
                    isValid = false;
                }

                if (!selectedSex.equals("All") && !cat.getSex().equalsIgnoreCase(selectedSex)) {
                    isValid = false;
                }

                if (!cat.getName().toLowerCase().contains(query.toLowerCase())) {
                    isValid = false;
                }

                if (isValid) {
                    filteredList.add(cat);
                }
            }
        }

        adapter.updateList(filteredList);
    }


    // TODO: Redirect to "swipe view" when a card is tapped
    // TODO: Filter based on applied filters
}
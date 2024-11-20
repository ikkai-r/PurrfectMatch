package com.example.purrfectmatch;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;  

public class ExploreActivity extends AppCompatActivity {

    private ExploreAdapter adapter;
    private List<ExploreData> exploreList;
    private List<ExploreData> filteredList;
    private EditText searchBar;
    ImageView profile, explore, swipe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        profile = findViewById(R.id.profile);
        explore = findViewById(R.id.explore);
        swipe = findViewById(R.id.swipe);

        exploreList = new ArrayList<>();

        //TODO: Get data from firebase instead.
        //TODO: Change age to int
        exploreList.add(new ExploreData(R.drawable.cat0, "Dweety", "47 months old", "Female", "Puspin", true));
        exploreList.add(new ExploreData(R.drawable.cat1, "Fluffy", "32 months old", "Male", "Puspin", false));
        exploreList.add(new ExploreData(R.drawable.cat1, "Milo", "32 months old", "Female", "Puspin", false));
        exploreList.add(new ExploreData(R.drawable.cat1, "Mikmik", "12 months old", "Male", "Puspin", false));
        exploreList.add(new ExploreData(R.drawable.cat1, "Mia", "14 months old", "Female", "Puspin", false));
        exploreList.add(new ExploreData(R.drawable.cat1, "Mia", "14 months old", "Male", "Puspin", false));
        exploreList.add(new ExploreData(R.drawable.cat1, "Mia", "14 months old", "Female", "Puspin", false));
        exploreList.add(new ExploreData(R.drawable.cat1, "Mia", "14 months old", "Male", "Puspin", false));

        filteredList = new ArrayList<>(exploreList); // Initially, the filtered list is the same as exploreList

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new ExploreAdapter(exploreList, ExploreActivity.this);
        recyclerView.setAdapter(adapter);

        searchBar = findViewById(R.id.searchBar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                filterCats(charSequence.toString());
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });

        profile.setOnClickListener(view -> {
            Intent i = new Intent(this, ProfileActivity.class);
            startActivity(i);
        });

        swipe.setOnClickListener(view -> {
            Intent i = new Intent(this, SwipeActivity.class);
                startActivity(i);
        });

        explore.setOnClickListener(view -> {
            Intent i = new Intent(this, ExploreActivity.class);
            startActivity(i);
        });
    }

    private void filterCats(String query) {
        List<ExploreData> filteredList = new ArrayList<>();
        for (ExploreData cat : exploreList) {
            if (cat.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(cat);
            }
        }
        this.filteredList = filteredList;
        adapter.updateList(filteredList);
    }

    // TODO: Redirect to "swipe view" when a card is tapped
    // TODO: Filter based on applied filters
}

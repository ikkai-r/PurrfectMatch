package com.example.purrfectmatch;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

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

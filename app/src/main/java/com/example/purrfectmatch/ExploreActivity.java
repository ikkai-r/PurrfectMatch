package com.example.purrfectmatch;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ExploreActivity extends AppCompatActivity {

    private ExploreAdapter adapter;
    private List<ExploreData> exploreList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.explore);

        exploreList = new ArrayList<>();
        exploreList.add(new ExploreData(R.drawable.cat0, "Dweety", "47 months old", "Female", "Puspin", true));
        exploreList.add(new ExploreData(R.drawable.cat1, "Fluffy", "32 months old", "Male", "Puspin", false));
        exploreList.add(new ExploreData(R.drawable.cat1, "Milo", "32 months old", "Female", "Puspin", false));
        exploreList.add(new ExploreData(R.drawable.cat1, "Mikmik", "12 months old", "Male", "Puspin", false));
        exploreList.add(new ExploreData(R.drawable.cat1, "Mia", "14 months old", "Female", "Puspin", false));
        exploreList.add(new ExploreData(R.drawable.cat1, "Mia", "14 months old", "Male", "Puspin", false));
        exploreList.add(new ExploreData(R.drawable.cat1, "Mia", "14 months old", "Female", "Puspin", false));
        exploreList.add(new ExploreData(R.drawable.cat1, "Mia", "14 months old", "Male", "Puspin", false));

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new ExploreAdapter(exploreList, ExploreActivity.this);
        recyclerView.setAdapter(adapter);
    }

    // TODO: Redirect to "swipe view" when a card is tapped
}

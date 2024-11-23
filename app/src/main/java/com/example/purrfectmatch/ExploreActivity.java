package com.example.purrfectmatch;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        profile = findViewById(R.id.profile);
        explore = findViewById(R.id.explore);
        swipe = findViewById(R.id.swipe);

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
                filterCats(charSequence.toString());
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });

        adapter.setOnItemClickListener(new ExploreAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ExploreData cat) {
                Intent i = new Intent(ExploreActivity.this, ClickedExploreActivity.class);
//                i.putExtra("documentId", cat.getId());
                startActivity(i);
            }
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
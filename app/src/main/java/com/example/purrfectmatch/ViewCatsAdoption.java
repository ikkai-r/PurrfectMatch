package com.example.purrfectmatch;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ViewCatsAdoption extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ExploreAdapter adapter;
    private List<ExploreData> exploreList;
    private List<ExploreData> filteredList;
    private FirebaseFirestore db;

    private EditText searchBar;
    private LinearLayout navBar;
    private TextView filter, title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_explore);

        navBar = findViewById(R.id.linearLayout2);
        navBar.setVisibility(View.GONE); // Remove navbar
        filter = findViewById(R.id.filter);
        filter.setVisibility(View.GONE); // Remove filter
        title = findViewById(R.id.exploreText);
        title.setText("Cats for Adoption");

        exploreList = new ArrayList<>();
        filteredList = new ArrayList<>(exploreList);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        adapter = new ExploreAdapter(exploreList, ViewCatsAdoption.this);
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
            Intent i = new Intent(ViewCatsAdoption.this, CatProfile.class);
            i.putExtra("documentId", cat.getId());
            startActivity(i);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void fetchCats() {
        db.collection("Cats")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Toast.makeText(ViewCatsAdoption.this, "Error fetching data: "
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
                                Toast.makeText(ViewCatsAdoption.this, "Error parsing data: "
                                        + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        filteredList = new ArrayList<>(exploreList);
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private void searchCats(String query) {
        filteredList = new ArrayList<>();

        for (ExploreData cat : exploreList) {
            if (cat.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(cat);
            }
        }
        adapter.updateList(filteredList);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

//    public void viewCatProfile(View v) {
//        Intent i = new Intent(this, CatProfile.class);
//        this.startActivity(i);
//    }
}
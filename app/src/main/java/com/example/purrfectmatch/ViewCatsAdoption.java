package com.example.purrfectmatch;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
            i.putExtra("catId", cat.getId());
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
                .whereEqualTo("isAdopted", false)
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
                                ExploreData cat = createExploreDataFromDocument(snapshot);
                                if (cat != null) {
                                    exploreList.add(cat);
                                }
                            } catch (Exception e) {
                                Toast.makeText(ViewCatsAdoption.this, "Error parsing data: "
                                        + e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.d("error" ,"Here!" + e.getMessage());
                            }
                        }
                        filteredList = new ArrayList<>(exploreList);
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private ExploreData createExploreDataFromDocument(DocumentSnapshot document) {
        try {
            String catId = document.getId();
            int age = document.contains("age") && document.get("age") != null
                    ? document.getLong("age").intValue()
                    : 0;

            String catImageStr = document.getString("catImage");
            int catImage = R.drawable.app_icon; // Default picture
            if (catImageStr != null && !catImageStr.isEmpty()) {
                catImage = getResources().getIdentifier(catImageStr, "drawable", getPackageName());
            }

            String sex = document.getString("sex");
            String breed = document.getString("breed");
            String name = document.getString("name");
            boolean isNeutered = document.contains("isNeutered") && Boolean.TRUE.equals(document.getBoolean("isNeutered"));
            int adoptionFee = document.contains("adoptionFee") && document.get("adoptionFee") != null
                    ? document.getLong("adoptionFee").intValue()
                    : 0;

            return new ExploreData(catId, catImageStr, name, age, sex, breed, isNeutered, adoptionFee);
        } catch (Exception e) {
            Log.e("CreateExploreData", "Error creating ExploreData from document", e);
            return null;
        }
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
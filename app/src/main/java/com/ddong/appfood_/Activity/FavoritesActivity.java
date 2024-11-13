package com.ddong.appfood_.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.ddong.appfood_.Adapter.FavoriteAdapter;
import com.ddong.appfood_.Helper.TinyDB;
import com.ddong.appfood_.R;
import com.ddong.appfood_.databinding.ActivityFavoritesBinding;

import java.util.ArrayList;

public class FavoritesActivity extends AppCompatActivity {
    ActivityFavoritesBinding binding;
    RecyclerView recyclerView;
    FavoriteAdapter adapter;
    TinyDB tinyDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityFavoritesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        recyclerView = findViewById(R.id.recyclerViewFavorites);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        tinyDB = new TinyDB(this);

        ArrayList<String> favoriteList = tinyDB.getListString("favorite_products");
        adapter = new FavoriteAdapter(favoriteList);
        recyclerView.setAdapter(adapter);
    
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
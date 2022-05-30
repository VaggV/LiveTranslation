package com.vaggv.livetranslation.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.vaggv.livetranslation.R;

public class StatsMenu extends AppCompatActivity {
    Button showMapBtn, pplrLanguagesBtn, pplrTranslationsBtn, pplrWordsBtn, userLeaderboardBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats_menu);

        showMapBtn = findViewById(R.id.showMapBtn);

        showMapBtn.setOnClickListener(v -> {
            startActivity(new Intent(StatsMenu.this, MapsActivity.class));
        });

        pplrLanguagesBtn = findViewById(R.id.pplrLanguagesBtn);
        pplrTranslationsBtn = findViewById(R.id.pplrTranslationsBtn);
        pplrWordsBtn = findViewById(R.id.pplrWordsBtn);
        userLeaderboardBtn = findViewById(R.id.userLeaderboardBtn);

        pplrLanguagesBtn.setOnClickListener(v -> {
            startActivity(new Intent(StatsMenu.this, PopularLanguagesActivity.class));
        });

        pplrTranslationsBtn.setOnClickListener(v -> {
            startActivity(new Intent(StatsMenu.this, PopularTextActivity.class));
        });

        pplrWordsBtn.setOnClickListener(v -> {
            startActivity(new Intent(StatsMenu.this, PopularWordsActivity.class));
        });

        userLeaderboardBtn.setOnClickListener(v -> {
            startActivity(new Intent(StatsMenu.this, UserLeaderboardActivity.class));
        });
    }
}
package com.vaggv.livetranslation.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.vaggv.livetranslation.R;

public class StatsMenu extends AppCompatActivity {
    Button showMapBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats_menu);

        showMapBtn = findViewById(R.id.showMapBtn);

        showMapBtn.setOnClickListener(v -> {
            startActivity(new Intent(StatsMenu.this, MapsActivity.class));
        });
    }
}
package com.vaggv.livetranslation.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.vaggv.livetranslation.R;

public class MenuActivity extends AppCompatActivity {

    Button openCameraBtn, openStatsBtn, openSettingsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        openCameraBtn = findViewById(R.id.openCameraBtn);
        openStatsBtn = findViewById(R.id.openStatsBtn);
        openSettingsBtn = findViewById(R.id.openSettingsBtn);

        openCameraBtn.setOnClickListener(v -> {
            startActivity(new Intent(MenuActivity.this, MainActivity.class));
        });

        openStatsBtn.setOnClickListener(v -> {
            startActivity(new Intent(MenuActivity.this, StatsMenu.class));
        });

        openSettingsBtn.setOnClickListener(v -> {
            startActivity(new Intent(MenuActivity.this, SettingsActivity.class));
        });
    }
}
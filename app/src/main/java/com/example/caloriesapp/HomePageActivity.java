package com.example.caloriesapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class HomePageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topbar);

        findViewById(R.id.icon_bell).setOnClickListener(v -> {
            // Create an intent to start EditPhysicalProfileActivity
            Intent intent = new Intent(HomePageActivity.this, SettingActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.avatar).setOnClickListener(v -> {
            // Create an intent to start EditPhysicalProfileActivity
            Intent intent = new Intent(HomePageActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.lightning).setOnClickListener(v -> {
            // Create an intent to start EditPhysicalProfileActivity
            Intent intent = new Intent(HomePageActivity.this, TdeeActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.fire).setOnClickListener(v -> {
            // Create an intent to start EditPhysicalProfileActivity
            Intent intent = new Intent(HomePageActivity.this, AddActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.title_exercise).setOnClickListener(v -> {
            // Create an intent to start EditPhysicalProfileActivity
            Intent intent = new Intent(HomePageActivity.this, ListActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.icon_calendar).setOnClickListener(v -> {
            // Create an intent to start EditPhysicalProfileActivity
            Intent intent = new Intent(HomePageActivity.this, MonitorActivity.class);
            startActivity(intent);
        });

    }
}
package com.example.caloriesapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        findViewById(R.id.toolbar).setOnClickListener(v -> {
            // Create an intent to start EditPhysicalProfileActivity
            Intent intent = new Intent(ProfileActivity.this, HomePageActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.logout).setOnClickListener(v -> {
            // Create an intent to start EditPhysicalProfileActivity
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.physical_profile).setOnClickListener(v -> {
            // Create an intent to start EditPhysicalProfileActivity
            Intent intent = new Intent(ProfileActivity.this, PhysicalProfileActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.language).setOnClickListener(v -> {
            // Create an intent to start EditPhysicalProfileActivity
            Intent intent = new Intent(ProfileActivity.this, DialogLanguageActivity.class);
            startActivity(intent);
        });

    }
}
package com.example.caloriesapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        email = getIntent().getStringExtra("email");

        findViewById(R.id.toolbar).setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, HomePageActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.logout).setOnClickListener(v -> {

            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.physical_profile).setOnClickListener(v -> {

            Intent intent = new Intent(ProfileActivity.this, PhysicalProfileActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
        });

        findViewById(R.id.language).setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, DialogLanguageActivity.class);
            startActivity(intent);
        });

    }
}
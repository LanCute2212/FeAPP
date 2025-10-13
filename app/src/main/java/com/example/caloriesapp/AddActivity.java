package com.example.caloriesapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class AddActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addactivity);

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            // Create an intent to start EditPhysicalProfileActivity
            Intent intent = new Intent(AddActivity.this, HomePageActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.btnConfirm).setOnClickListener(v -> {
            // Create an intent to start EditPhysicalProfileActivity
            Intent intent = new Intent(AddActivity.this, HomePageActivity.class);
            startActivity(intent);
        });
    }
}
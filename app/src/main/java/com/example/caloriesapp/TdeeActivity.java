package com.example.caloriesapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class TdeeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tdeeactivitylevel);

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            // Create an intent to start EditPhysicalProfileActivity
            Intent intent = new Intent(TdeeActivity.this, HomePageActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.calculate_tdee_button).setOnClickListener(v -> {
            // Create an intent to start EditPhysicalProfileActivity
            Intent intent = new Intent(TdeeActivity.this, EditPhysicalProfile.class);
            startActivity(intent);
        });
    }
}
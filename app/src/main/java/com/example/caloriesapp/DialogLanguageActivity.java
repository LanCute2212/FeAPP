package com.example.caloriesapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class DialogLanguageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialoglanguage);

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            // Create an intent to start EditPhysicalProfileActivity
            Intent intent = new Intent(DialogLanguageActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }
}
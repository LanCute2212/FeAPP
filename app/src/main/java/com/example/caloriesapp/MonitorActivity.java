package com.example.caloriesapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MonitorActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring);

        findViewById(R.id.toolbar).setOnClickListener(v -> {
            Intent intent = new Intent(MonitorActivity.this, HomePageActivity.class);
            startActivity(intent);
        });
    }
}
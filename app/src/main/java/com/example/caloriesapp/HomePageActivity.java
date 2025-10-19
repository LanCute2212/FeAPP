package com.example.caloriesapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class HomePageActivity extends AppCompatActivity {
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topbar);

        email = getIntent().getStringExtra("email");


        findViewById(R.id.icon_bell).setOnClickListener(v -> {
            Intent intent = new Intent(HomePageActivity.this, SettingActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.avatar).setOnClickListener(v -> {
            Intent intent = new Intent(HomePageActivity.this, ProfileActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
        });

        findViewById(R.id.lightning).setOnClickListener(v -> {
            Intent intent = new Intent(HomePageActivity.this, TdeeActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.fire).setOnClickListener(v -> {
            Intent intent = new Intent(HomePageActivity.this, AddActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.title_exercise).setOnClickListener(v -> {
            Intent intent = new Intent(HomePageActivity.this, ListActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.icon_calendar).setOnClickListener(v -> {
            Intent intent = new Intent(HomePageActivity.this, MonitorActivity.class);
            startActivity(intent);
        });

    }
}
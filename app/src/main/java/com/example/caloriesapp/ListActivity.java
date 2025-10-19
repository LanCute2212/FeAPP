package com.example.caloriesapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class ListActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listactivity);

        findViewById(R.id.cancel).setOnClickListener(v -> {
            Intent intent = new Intent(ListActivity.this, HomePageActivity.class);
            startActivity(intent);
        });
    }
}
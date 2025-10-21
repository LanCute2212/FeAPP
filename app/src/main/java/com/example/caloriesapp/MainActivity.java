package com.example.caloriesapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        navigateToHomePage();
    }

    private void navigateToHomePage() {
        Intent intent = new Intent(MainActivity.this, HomePageActivity.class);
        startActivity(intent);
        finish();
    }
}

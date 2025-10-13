package com.example.caloriesapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class PhysicalProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physicalprofile);

        // Find the edit button by its ID (assuming it's defined in activity_physical_profile.xml as edit_physical_button)
        findViewById(R.id.btnRegister).setOnClickListener(v -> {
            // Create an intent to start EditPhysicalProfileActivity
            Intent intent = new Intent(PhysicalProfileActivity.this, EditPhysicalProfile.class);
            startActivity(intent);
        });
    }
}
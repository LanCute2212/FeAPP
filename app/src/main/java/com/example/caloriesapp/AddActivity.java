package com.example.caloriesapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddActivity extends AppCompatActivity {
    private EditText etActivityName, etEnergy, etDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addactivity);

        etActivityName = findViewById(R.id.et_activity_name);
        etEnergy = findViewById(R.id.et_energy);
        etDuration = findViewById(R.id.et_duration);

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            finish();
        });

        findViewById(R.id.btnConfirm).setOnClickListener(v -> {
            if (validateInput()) {
                createNewActivity();
            }
        });
    }

    private boolean validateInput() {
        String activityName = etActivityName.getText().toString().trim();
        String energyText = etEnergy.getText().toString().trim();
        String durationText = etDuration.getText().toString().trim();

        if (activityName.isEmpty()) {
            etActivityName.setError("Please enter activity name");
            return false;
        }

        if (energyText.isEmpty()) {
            etEnergy.setError("Please enter energy value");
            return false;
        }

        if (durationText.isEmpty()) {
            etDuration.setError("Please enter duration");
            return false;
        }

        try {
            int energy = Integer.parseInt(energyText);
            int duration = Integer.parseInt(durationText);
            
            if (energy <= 0) {
                etEnergy.setError("Energy must be greater than 0");
                return false;
            }
            
            if (duration <= 0) {
                etDuration.setError("Duration must be greater than 0");
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void createNewActivity() {
        String activityName = etActivityName.getText().toString().trim();
        int energy = Integer.parseInt(etEnergy.getText().toString().trim());
        int duration = Integer.parseInt(etDuration.getText().toString().trim());

        Intent resultIntent = new Intent();
        resultIntent.putExtra("activity_name", activityName);
        resultIntent.putExtra("energy", energy);
        resultIntent.putExtra("duration", duration);
        
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
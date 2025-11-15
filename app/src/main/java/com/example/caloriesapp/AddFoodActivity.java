package com.example.caloriesapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;

public class AddFoodActivity extends AppCompatActivity {
  private static final String EXTRA_MEAL_TYPE = "meal_type";
  private static final double CARBS_CALORIES_PER_GRAM = 4.0;
  private static final double PROTEIN_CALORIES_PER_GRAM = 4.0;
  private static final double FAT_CALORIES_PER_GRAM = 9.0;
  
  private AutoCompleteTextView mealTypeDropdown;
  private TextInputEditText foodNameInput;
  private TextInputEditText energyInput;
  private TextInputEditText weightInput;
  private TextInputEditText carbsInput;
  private TextInputEditText proteinInput;
  private TextInputEditText fatInput;
  private TextView otherNutrients;
  private String currentMealType;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_food);

    currentMealType = getIntent().getStringExtra(EXTRA_MEAL_TYPE);
    if (currentMealType == null) {
      currentMealType = "Breakfast";
    }

    initializeViews();
    setupMealTypeDropdown();
    setupClickListeners();
  }

  private void initializeViews() {
    mealTypeDropdown = findViewById(R.id.meal_type);
    foodNameInput = findViewById(R.id.food_name_input);
    energyInput = findViewById(R.id.energy_input);
    weightInput = findViewById(R.id.weight_input);
    carbsInput = findViewById(R.id.carbs_input);
    proteinInput = findViewById(R.id.protein_input);
    fatInput = findViewById(R.id.fat_input);
    otherNutrients = findViewById(R.id.other_nutrients);
    
    setupTextWatchers();
  }
  
  private void setupTextWatchers() {
    TextWatcher macroWatcher = new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }
      
      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        calculateEnergyFromMacros();
      }
      
      @Override
      public void afterTextChanged(Editable s) {
      }
    };
    
    carbsInput.addTextChangedListener(macroWatcher);
    proteinInput.addTextChangedListener(macroWatcher);
    fatInput.addTextChangedListener(macroWatcher);
  }
  
  private void calculateEnergyFromMacros() {
    try {
      double carbs = getDoubleValue(carbsInput.getText().toString());
      double protein = getDoubleValue(proteinInput.getText().toString());
      double fat = getDoubleValue(fatInput.getText().toString());
      
      double totalCalories = (carbs * CARBS_CALORIES_PER_GRAM) +
                             (protein * PROTEIN_CALORIES_PER_GRAM) +
                             (fat * FAT_CALORIES_PER_GRAM);
      
      energyInput.setText(String.format("%.1f", totalCalories));
    } catch (Exception e) {
      energyInput.setText("0.0");
    }
  }
  
  private double getDoubleValue(String text) {
    if (text == null || text.trim().isEmpty()) {
      return 0.0;
    }
    try {
      return Double.parseDouble(text.trim());
    } catch (NumberFormatException e) {
      return 0.0;
    }
  }

  private void setupMealTypeDropdown() {
    String[] mealTypes = {"Sáng", "Trưa", "Tối"};
    String[] mealTypeValues = {"Breakfast", "Lunch", "Dinner"};

    ArrayAdapter<String> adapter = new ArrayAdapter<>(
        this,
        R.layout.dropdown_item_meal_type,
        mealTypes
    );

    mealTypeDropdown.setAdapter(adapter);
    mealTypeDropdown.setText(getMealTypeDisplayName(currentMealType), false);

    try {
      mealTypeDropdown.setDropDownBackgroundDrawable(
          getResources().getDrawable(R.drawable.dropdown_background, getTheme()));
    } catch (Exception e) {
      mealTypeDropdown.setDropDownBackgroundResource(android.R.drawable.dialog_holo_light_frame);
    }

    mealTypeDropdown.setOnItemClickListener((parent, view, position, id) -> {
      String selectedDisplayName = mealTypes[position];
      String selectedValue = mealTypeValues[position];
      currentMealType = selectedValue;
      mealTypeDropdown.setText(selectedDisplayName, false);
    });
  }

  private void setupClickListeners() {
    findViewById(R.id.back_button).setOnClickListener(v -> finish());

    findViewById(R.id.other_nutrients).setOnClickListener(v -> {
      Toast.makeText(this, "Other nutrients input will be implemented", Toast.LENGTH_SHORT).show();
    });

    findViewById(R.id.save_button).setOnClickListener(v -> {
      saveFoodItem();
    });
  }

  private void saveFoodItem() {
    String foodName = foodNameInput.getText().toString().trim();
    
    if (foodName.isEmpty()) {
      Toast.makeText(this, "Vui lòng nhập tên thực phẩm", Toast.LENGTH_SHORT).show();
      foodNameInput.requestFocus();
      return;
    }
    
    double energy = getDoubleValue(energyInput.getText().toString());
    double weight = getDoubleValue(weightInput.getText().toString());
    double carbs = getDoubleValue(carbsInput.getText().toString());
    double protein = getDoubleValue(proteinInput.getText().toString());
    double fat = getDoubleValue(fatInput.getText().toString());
    
    if (weight <= 0) {
      Toast.makeText(this, "Vui lòng nhập khối lượng", Toast.LENGTH_SHORT).show();
      weightInput.requestFocus();
      return;
    }
    
    Toast.makeText(this, "Đã lưu thực phẩm: " + foodName, Toast.LENGTH_SHORT).show();
    
    Intent resultIntent = new Intent();
    resultIntent.putExtra("food_name", foodName);
    resultIntent.putExtra("energy", energy);
    resultIntent.putExtra("weight", weight);
    resultIntent.putExtra("carbs", carbs);
    resultIntent.putExtra("protein", protein);
    resultIntent.putExtra("fat", fat);
    resultIntent.putExtra("meal_type", currentMealType);
    
    setResult(RESULT_OK, resultIntent);
    finish();
  }

  private String getMealTypeDisplayName(String mealType) {
    switch (mealType.toLowerCase()) {
      case "breakfast":
        return "Sáng";
      case "lunch":
        return "Trưa";
      case "dinner":
        return "Tối";
      default:
        return "Sáng";
    }
  }
}


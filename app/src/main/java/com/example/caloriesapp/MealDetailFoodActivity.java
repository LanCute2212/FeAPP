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
import com.example.caloriesapp.model.FoodItem;
import com.example.caloriesapp.model.MealDetail;
import com.example.caloriesapp.util.MealDataManager;
import com.example.caloriesapp.view.SegmentedCircularProgressView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class MealDetailFoodActivity extends AppCompatActivity {
  public static final String EXTRA_FOOD_ITEM = "food_item";
  public static final String EXTRA_MEAL_TYPE = "meal_type";
  
  private FoodItem foodItem;
  private String currentMealType;
  private AutoCompleteTextView mealTypeDropdown;
  private TextInputEditText servingCountInput;
  private AutoCompleteTextView servingUnitDropdown;
  private SegmentedCircularProgressView calorieProgress;
  private TextView calorieText;
  private TextView carbsValue;
  private TextView carbsPercent;
  private TextView proteinValue;
  private TextView proteinPercent;
  private TextView fatValue;
  private TextView fatPercent;
  private MaterialButton addToMealButton;
  
  private int servingCount = 1;
  private double baseCalories = 0;
  private double baseCarbs = 0;
  private double baseProtein = 0;
  private double baseFat = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_meal_detail_food);

    foodItem = (FoodItem) getIntent().getSerializableExtra(EXTRA_FOOD_ITEM);
    currentMealType = getIntent().getStringExtra(EXTRA_MEAL_TYPE);
    if (currentMealType == null) {
      currentMealType = "Breakfast";
    }

    if (foodItem == null) {
      Toast.makeText(this, "Food item not found", Toast.LENGTH_SHORT).show();
      finish();
      return;
    }

    initializeViews();
    setupMealTypeDropdown();
    setupServingInputs();
    updateNutritionDisplay();
  }

  private void initializeViews() {
    mealTypeDropdown = findViewById(R.id.meal_type);
    servingCountInput = findViewById(R.id.serving_count);
    servingUnitDropdown = findViewById(R.id.serving_unit);
    calorieProgress = findViewById(R.id.calorie_progress);
    calorieText = findViewById(R.id.calorie_text);
    carbsValue = findViewById(R.id.carbs_value);
    carbsPercent = findViewById(R.id.carbs_percent);
    proteinValue = findViewById(R.id.protein_value);
    proteinPercent = findViewById(R.id.protein_percent);
    fatValue = findViewById(R.id.fat_value);
    fatPercent = findViewById(R.id.fat_percent);
    addToMealButton = findViewById(R.id.add_to_meal_button);

    TextView foodName = findViewById(R.id.food_name);
    foodName.setText(foodItem.getName());

    baseCalories = foodItem.getCalories();
    try {
      baseCarbs = Double.parseDouble(foodItem.getCarbs().replace("g", "").trim());
      baseProtein = Double.parseDouble(foodItem.getProtein().replace("g", "").trim());
      baseFat = Double.parseDouble(foodItem.getFat().replace("g", "").trim());
    } catch (Exception e) {
      baseCarbs = 0;
      baseProtein = 0;
      baseFat = 0;
    }

    findViewById(R.id.back_button).setOnClickListener(v -> finish());
    findViewById(R.id.favorite_button).setOnClickListener(v -> {
      Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show();
    });
    findViewById(R.id.more_button).setOnClickListener(v -> {
      Toast.makeText(this, "More options", Toast.LENGTH_SHORT).show();
    });
    findViewById(R.id.full_nutrition_link).setOnClickListener(v -> {
      Toast.makeText(this, "Full nutrition info", Toast.LENGTH_SHORT).show();
    });
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
      updateAddButtonText();
    });

    updateAddButtonText();
  }

  private void setupServingInputs() {
    String[] servingUnits = {"Khẩu phần (50g)", "100g", "200g", "1 cốc", "1 chén"};
    
    ArrayAdapter<String> adapter = new ArrayAdapter<>(
        this,
        R.layout.dropdown_item_meal_type,
        servingUnits
    );

    servingUnitDropdown.setAdapter(adapter);
    servingUnitDropdown.setText(servingUnits[0], false);

    try {
      servingUnitDropdown.setDropDownBackgroundDrawable(
          getResources().getDrawable(R.drawable.dropdown_background, getTheme()));
    } catch (Exception e) {
      servingUnitDropdown.setDropDownBackgroundResource(android.R.drawable.dialog_holo_light_frame);
    }

    servingCountInput.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        try {
          servingCount = Integer.parseInt(s.toString());
          if (servingCount <= 0) {
            servingCount = 1;
          }
        } catch (Exception e) {
          servingCount = 1;
        }
        updateNutritionDisplay();
      }

      @Override
      public void afterTextChanged(Editable s) {
      }
    });

    addToMealButton.setOnClickListener(v -> addFoodToMeal());
  }

  private void updateNutritionDisplay() {
    double totalCalories = baseCalories * servingCount;
    double totalCarbs = baseCarbs * servingCount;
    double totalProtein = baseProtein * servingCount;
    double totalFat = baseFat * servingCount;

    double totalMacros = totalCarbs + totalProtein + totalFat;
    
    calorieText.setText(String.format("%.0f Kcal", totalCalories));
    
    int carbsPercentValue = totalMacros > 0 ? (int)((totalCarbs / totalMacros) * 100) : 0;
    int proteinPercentValue = totalMacros > 0 ? (int)((totalProtein / totalMacros) * 100) : 0;
    int fatPercentValue = totalMacros > 0 ? (int)((totalFat / totalMacros) * 100) : 0;

    carbsPercent.setText(carbsPercentValue + "%");
    proteinPercent.setText(proteinPercentValue + "%");
    fatPercent.setText(fatPercentValue + "%");

    if (calorieProgress != null) {
      calorieProgress.setMacros((float)carbsPercentValue, (float)proteinPercentValue, (float)fatPercentValue);
    }

    if (carbsValue != null) {
      carbsValue.setText("Carbs: " + String.format("%.1fg", totalCarbs));
    }
    if (proteinValue != null) {
      proteinValue.setText("Chất đạm: " + String.format("%.1fg", totalProtein));
    }
    if (fatValue != null) {
      fatValue.setText("Chất béo: " + String.format("%.1fg", totalFat));
    }
  }

  private void updateAddButtonText() {
    String mealTypeText = getMealTypeDisplayName(currentMealType);
    addToMealButton.setText("Thêm vào bữa " + mealTypeText.toLowerCase());
  }

  private void addFoodToMeal() {
    double totalCalories = baseCalories * servingCount;
    double totalCarbs = baseCarbs * servingCount;
    double totalProtein = baseProtein * servingCount;
    double totalFat = baseFat * servingCount;

    FoodItem adjustedFood = new FoodItem(
        foodItem.getName(),
        foodItem.getServingSize(),
        (int)totalCalories,
        foodItem.getIconResource(),
        String.format("%.1f", totalProtein),
        String.format("%.1f", totalCarbs),
        String.format("%.1f", totalFat)
    );

    String currentDate = MealDataManager.getInstance().getCurrentDate();
    MealDetail mealDetail = new MealDetail(currentMealType, adjustedFood, currentDate);
    MealDataManager.getInstance().addMealDetail(mealDetail);

    Intent resultIntent = new Intent();
    resultIntent.putExtra("meal_type", currentMealType);
    resultIntent.putExtra("calories", totalCalories);
    resultIntent.putExtra("carbs", totalCarbs);
    resultIntent.putExtra("protein", totalProtein);
    resultIntent.putExtra("fat", totalFat);
    
    setResult(RESULT_OK, resultIntent);
    Toast.makeText(this, "Đã thêm vào bữa " + getMealTypeDisplayName(currentMealType).toLowerCase(), Toast.LENGTH_SHORT).show();
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


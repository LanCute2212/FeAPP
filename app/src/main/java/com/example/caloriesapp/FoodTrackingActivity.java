package com.example.caloriesapp;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caloriesapp.adapter.FoodAdapter;
import com.example.caloriesapp.model.FoodItem;
import com.example.caloriesapp.model.MealDetail;
import com.example.caloriesapp.util.MealDataManager;

import java.util.ArrayList;
import java.util.List;

public class FoodTrackingActivity extends AppCompatActivity {

  private static final int ADD_FOOD_REQUEST_CODE = 2001;
  public static final String EXTRA_MEAL_TYPE = "meal_type";

  private FoodAdapter foodAdapter;
  private List<FoodItem> allFoodList;
  private List<FoodItem> recentFoodList;
  private List<FoodItem> favoritesFoodList;
  private List<FoodItem> myFoodsList;
  private List<FoodItem> displayedFoodList;
  private TextView totalCaloriesText;
  private TextView totalCarbsText;
  private TextView totalProteinText;
  private TextView totalFatText;
  private AutoCompleteTextView mealTypeDropdown;
  private String currentMealType;
  private int currentTabIndex = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_food_tracking);

    // Get meal type from intent
    currentMealType = getIntent().getStringExtra(EXTRA_MEAL_TYPE);
    if (currentMealType == null) {
      currentMealType = "Breakfast"; // Default value
    }

    initializeViews();
    setupFoodList();
    setupClickListeners();
    updateNutritionSummary();
  }

  private void initializeViews() {
    totalCaloriesText = findViewById(R.id.total_calories);
    totalCarbsText = findViewById(R.id.total_carbs);
    totalProteinText = findViewById(R.id.total_protein);
    totalFatText = findViewById(R.id.total_fat);
    mealTypeDropdown = findViewById(R.id.meal_type);

    setupMealTypeDropdown();
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
      updateNutritionSummary();
    });
  }

  private void setupFoodList() {
    RecyclerView recyclerView = findViewById(R.id.food_recycler_view);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));

    allFoodList = createSampleFoodList();
    recentFoodList = new ArrayList<>();
    favoritesFoodList = new ArrayList<>();
    myFoodsList = new ArrayList<>();
    
    displayedFoodList = new ArrayList<>();
    foodAdapter = new FoodAdapter(displayedFoodList);
    recyclerView.setAdapter(foodAdapter);

    foodAdapter.setOnFoodClickListener(new FoodAdapter.OnFoodClickListener() {
      @Override
      public void onFoodClick(FoodItem food, int position) {
        Intent intent = new Intent(FoodTrackingActivity.this, MealDetailFoodActivity.class);
        intent.putExtra(MealDetailFoodActivity.EXTRA_FOOD_ITEM, food);
        intent.putExtra(EXTRA_MEAL_TYPE, currentMealType);
        startActivityForResult(intent, 2002);
      }

      @Override
      public void onAddFoodClick(FoodItem food, int position) {
        Intent intent = new Intent(FoodTrackingActivity.this, MealDetailFoodActivity.class);
        intent.putExtra(MealDetailFoodActivity.EXTRA_FOOD_ITEM, food);
        intent.putExtra(EXTRA_MEAL_TYPE, currentMealType);
        startActivityForResult(intent, 2002);
      }
    });
    
    updateDisplayedFoodList();
  }
  
  private void updateDisplayedFoodList() {
    displayedFoodList.clear();
    switch (currentTabIndex) {
      case 0:
        displayedFoodList.addAll(recentFoodList);
        break;
      case 1:
        displayedFoodList.addAll(favoritesFoodList);
        break;
      case 2:
        displayedFoodList.addAll(myFoodsList);
        break;
      default:
        displayedFoodList.addAll(allFoodList);
        break;
    }
    foodAdapter.notifyDataSetChanged();
  }

  private void setupClickListeners() {
    findViewById(R.id.back_button).setOnClickListener(v -> finish());

    findViewById(R.id.profile_button).setOnClickListener(v -> {
      Intent intent = new Intent(FoodTrackingActivity.this, ProfileActivity.class);
      startActivity(intent);
    });

    findViewById(R.id.search_container).setOnClickListener(v -> {
      // Handle search functionality
    });

    findViewById(R.id.scanner_button).setOnClickListener(v -> {
      // Handle barcode scanner functionality
    });

    findViewById(R.id.microphone_button).setOnClickListener(v -> {
      // Handle voice search functionality
    });

    // Action buttons click listeners - using the LinearLayout children
    LinearLayout actionButtons = findViewById(R.id.action_buttons);
    actionButtons.getChildAt(0).setOnClickListener(v -> {
      // Handle food category selection
    });

    actionButtons.getChildAt(1).setOnClickListener(v -> {
      Intent intent = new Intent(FoodTrackingActivity.this, AddFoodActivity.class);
      intent.putExtra(EXTRA_MEAL_TYPE, currentMealType);
      startActivityForResult(intent, ADD_FOOD_REQUEST_CODE);
    });

    findViewById(R.id.recent_tab).setOnClickListener(v -> selectTab(0));
    findViewById(R.id.favorites_tab).setOnClickListener(v -> selectTab(1));
    findViewById(R.id.my_foods_tab).setOnClickListener(v -> selectTab(2));

    findViewById(R.id.all_filter).setOnClickListener(v -> selectFilter(0));
    findViewById(R.id.high_protein_filter).setOnClickListener(v -> selectFilter(1));
    findViewById(R.id.low_carbs_filter).setOnClickListener(v -> selectFilter(2));
    findViewById(R.id.low_fat_filter).setOnClickListener(v -> selectFilter(3));
  }

  private void selectTab(int tabIndex) {
    TextView recentTab = findViewById(R.id.recent_tab);
    TextView favoritesTab = findViewById(R.id.favorites_tab);
    TextView myFoodsTab = findViewById(R.id.my_foods_tab);

    // Reset all tabs
    recentTab.setTextColor(getResources().getColor(R.color.gray));
    recentTab.setTypeface(null, Typeface.NORMAL);
    favoritesTab.setTextColor(getResources().getColor(R.color.gray));
    favoritesTab.setTypeface(null, Typeface.NORMAL);
    myFoodsTab.setTextColor(getResources().getColor(R.color.gray));
    myFoodsTab.setTypeface(null, Typeface.NORMAL);

    // Highlight selected tab
    currentTabIndex = tabIndex;
    switch (tabIndex) {
      case 0:
        recentTab.setTextColor(getResources().getColor(R.color.green));
        recentTab.setTypeface(null, Typeface.BOLD);
        break;
      case 1:
        favoritesTab.setTextColor(getResources().getColor(R.color.green));
        favoritesTab.setTypeface(null, Typeface.BOLD);
        break;
      case 2:
        myFoodsTab.setTextColor(getResources().getColor(R.color.green));
        myFoodsTab.setTypeface(null, Typeface.BOLD);
        break;
    }
    
    updateDisplayedFoodList();
  }

  private void selectFilter(int filterIndex) {
    TextView allFilter = findViewById(R.id.all_filter);
    TextView highProteinFilter = findViewById(R.id.high_protein_filter);
    TextView lowCarbsFilter = findViewById(R.id.low_carbs_filter);
    TextView lowFatFilter = findViewById(R.id.low_fat_filter);

    // Reset all filters
    allFilter.setTextColor(getResources().getColor(R.color.gray));
    highProteinFilter.setTextColor(getResources().getColor(R.color.gray));
    lowCarbsFilter.setTextColor(getResources().getColor(R.color.gray));
    lowFatFilter.setTextColor(getResources().getColor(R.color.gray));

    // Highlight selected filter
    switch (filterIndex) {
      case 0:
        allFilter.setTextColor(getResources().getColor(R.color.green));
        break;
      case 1:
        highProteinFilter.setTextColor(getResources().getColor(R.color.green));
        break;
      case 2:
        lowCarbsFilter.setTextColor(getResources().getColor(R.color.green));
        break;
      case 3:
        lowFatFilter.setTextColor(getResources().getColor(R.color.green));
        break;
    }
  }

  private void addFoodToMeal(FoodItem food) {
    // Create MealDetail from FoodItem
    String currentDate = MealDataManager.getInstance().getCurrentDate();
    MealDetail mealDetail = new MealDetail(currentMealType, food, currentDate);

    // Save to data manager
    MealDataManager.getInstance().addMealDetail(mealDetail);

    // Update nutrition summary
    updateNutritionSummary();

    // Show success message
    Toast.makeText(this, "Added " + food.getName() + " to " + currentMealType, Toast.LENGTH_SHORT)
        .show();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    
    if (requestCode == ADD_FOOD_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
      String foodName = data.getStringExtra("food_name");
      double energy = data.getDoubleExtra("energy", 0);
      double weight = data.getDoubleExtra("weight", 0);
      double carbs = data.getDoubleExtra("carbs", 0);
      double protein = data.getDoubleExtra("protein", 0);
      double fat = data.getDoubleExtra("fat", 0);
      
      FoodItem newFood = new FoodItem(
          foodName,
          String.format("%.0fg", weight),
          (int)energy,
          R.drawable.ic_meal,
          String.format("%.0f", protein),
          String.format("%.0f", carbs),
          String.format("%.0f", fat)
      );
      
      myFoodsList.add(newFood);
      
      selectTab(2);
      
      Toast.makeText(this, "Đã thêm " + foodName + " vào Của tôi", Toast.LENGTH_SHORT).show();
    } else if (requestCode == 2002 && resultCode == RESULT_OK && data != null) {
      String mealType = data.getStringExtra("meal_type");
      if (mealType != null) {
        currentMealType = mealType;
        mealTypeDropdown.setText(getMealTypeDisplayName(mealType), false);
      }
      updateNutritionSummary();
    }
  }

  private void updateNutritionSummary() {
    int totalCalories = 0;
    int totalCarbs = 0;
    int totalProtein = 0;
    int totalFat = 0;

    for (FoodItem food : displayedFoodList) {
      totalCalories += food.getCalories();
      // Add logic to calculate macros based on serving size
    }

    totalCaloriesText.setText(totalCalories + " kcal");
    totalCarbsText.setText(totalCarbs + " g");
    totalProteinText.setText(totalProtein + " g");
    totalFatText.setText(totalFat + " g");
  }

  private List<FoodItem> createSampleFoodList() {
    List<FoodItem> foods = new ArrayList<>();

    foods.add(new FoodItem(
        "Cơm cháy, không dầu",
        "100g",
        389,
        R.drawable.ic_meal,
        "8g",
        "75g",
        "2g"
    ));

    return foods;
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

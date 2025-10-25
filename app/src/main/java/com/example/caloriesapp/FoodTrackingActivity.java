package com.example.caloriesapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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
    private List<FoodItem> foodList;
    private TextView totalCaloriesText;
    private TextView totalCarbsText;
    private TextView totalProteinText;
    private TextView totalFatText;
    private TextView mealTypeText;
    private String currentMealType;

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
        mealTypeText = findViewById(R.id.meal_type);
        
        // Set the meal type text
        mealTypeText.setText(getMealTypeDisplayName(currentMealType));
    }

    private void setupFoodList() {
        RecyclerView recyclerView = findViewById(R.id.food_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        foodList = createSampleFoodList();
        foodAdapter = new FoodAdapter(foodList);
        recyclerView.setAdapter(foodAdapter);

        foodAdapter.setOnFoodClickListener(new FoodAdapter.OnFoodClickListener() {
            @Override
            public void onFoodClick(FoodItem food, int position) {
                // Handle food item click - could show details or edit
            }

            @Override
            public void onAddFoodClick(FoodItem food, int position) {
                addFoodToMeal(food);
            }
        });
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
            // Handle create new food item
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
        favoritesTab.setTextColor(getResources().getColor(R.color.gray));
        myFoodsTab.setTextColor(getResources().getColor(R.color.gray));

        // Highlight selected tab
        switch (tabIndex) {
            case 0:
                recentTab.setTextColor(getResources().getColor(R.color.green));
                break;
            case 1:
                favoritesTab.setTextColor(getResources().getColor(R.color.green));
                break;
            case 2:
                myFoodsTab.setTextColor(getResources().getColor(R.color.green));
                break;
        }
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
        Toast.makeText(this, "Added " + food.getName() + " to " + currentMealType, Toast.LENGTH_SHORT).show();
    }

    private void updateNutritionSummary() {
        int totalCalories = 0;
        int totalCarbs = 0;
        int totalProtein = 0;
        int totalFat = 0;

        for (FoodItem food : foodList) {
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

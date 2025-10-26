package com.example.caloriesapp;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caloriesapp.adapter.MealDetailAdapter;
import com.example.caloriesapp.model.MealDetail;
import com.example.caloriesapp.util.MealDataManager;

import java.util.List;

public class MealDetailsActivity extends AppCompatActivity {

    private MealDetailAdapter breakfastAdapter;
    private MealDetailAdapter lunchAdapter;
    private MealDetailAdapter dinnerAdapter;
    private List<MealDetail> breakfastList;
    private List<MealDetail> lunchList;
    private List<MealDetail> dinnerList;
    private RecyclerView breakfastRecyclerView;
    private RecyclerView lunchRecyclerView;
    private RecyclerView dinnerRecyclerView;
    private TextView breakfastCount;
    private TextView lunchCount;
    private TextView dinnerCount;
    private TextView breakfastDropdown;
    private TextView lunchDropdown;
    private TextView dinnerDropdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_details);

        initializeViews();
        setupMealDetails();
        setupClickListeners();
    }

    private void initializeViews() {
        // Initialize RecyclerViews
        breakfastRecyclerView = findViewById(R.id.breakfast_recycler_view);
        lunchRecyclerView = findViewById(R.id.lunch_recycler_view);
        dinnerRecyclerView = findViewById(R.id.dinner_recycler_view);
        
        // Initialize count TextViews
        breakfastCount = findViewById(R.id.breakfast_count);
        lunchCount = findViewById(R.id.lunch_count);
        dinnerCount = findViewById(R.id.dinner_count);
        
        // Initialize dropdown TextViews
        breakfastDropdown = findViewById(R.id.breakfast_dropdown);
        lunchDropdown = findViewById(R.id.lunch_dropdown);
        dinnerDropdown = findViewById(R.id.dinner_dropdown);
    }

    private void setupMealDetails() {
        String currentDate = MealDataManager.getInstance().getCurrentDate();
        
        // Initialize meal lists
        breakfastList = MealDataManager.getInstance().getMealDetailsForMealType("Breakfast", currentDate);
        lunchList = MealDataManager.getInstance().getMealDetailsForMealType("Lunch", currentDate);
        dinnerList = MealDataManager.getInstance().getMealDetailsForMealType("Dinner", currentDate);
        
        // Setup adapters
        breakfastAdapter = new MealDetailAdapter(breakfastList);
        lunchAdapter = new MealDetailAdapter(lunchList);
        dinnerAdapter = new MealDetailAdapter(dinnerList);
        
        // Setup RecyclerViews
        breakfastRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        breakfastRecyclerView.setAdapter(breakfastAdapter);
        
        lunchRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        lunchRecyclerView.setAdapter(lunchAdapter);
        
        dinnerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        dinnerRecyclerView.setAdapter(dinnerAdapter);
        
        // Setup click listeners
        setupMealDetailClickListeners();
        setupDropdownClickListeners();
        
        // Update counts
        updateMealCounts();
    }

    private void setupClickListeners() {
        findViewById(R.id.back_button).setOnClickListener(v -> finish());
    }

    private void setupMealDetailClickListeners() {
        MealDetailAdapter.OnMealDetailClickListener clickListener = new MealDetailAdapter.OnMealDetailClickListener() {
            @Override
            public void onMealDetailClick(MealDetail mealDetail, int position) {
                // Handle meal detail click - could show details or edit
                Toast.makeText(MealDetailsActivity.this, "Clicked: " + mealDetail.getFoodName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMealDetailDelete(MealDetail mealDetail, int position) {
                MealDataManager.getInstance().removeMealDetail(mealDetail);
                refreshMealDetails();
                Toast.makeText(MealDetailsActivity.this, "Removed: " + mealDetail.getFoodName(), Toast.LENGTH_SHORT).show();
            }
        };
        
        breakfastAdapter.setOnMealDetailClickListener(clickListener);
        lunchAdapter.setOnMealDetailClickListener(clickListener);
        dinnerAdapter.setOnMealDetailClickListener(clickListener);
    }

    private void setupDropdownClickListeners() {
        // Breakfast dropdown
        findViewById(R.id.breakfast_section).setOnClickListener(v -> {
            toggleMealSection("breakfast");
        });

        // Lunch dropdown
        findViewById(R.id.lunch_section).setOnClickListener(v -> {
            toggleMealSection("lunch");
        });

        // Dinner dropdown
        findViewById(R.id.dinner_section).setOnClickListener(v -> {
            toggleMealSection("dinner");
        });
    }

    private void toggleMealSection(String mealType) {
        RecyclerView recyclerView;
        TextView dropdown;
        
        switch (mealType) {
            case "breakfast":
                recyclerView = breakfastRecyclerView;
                dropdown = breakfastDropdown;
                break;
            case "lunch":
                recyclerView = lunchRecyclerView;
                dropdown = lunchDropdown;
                break;
            case "dinner":
                recyclerView = dinnerRecyclerView;
                dropdown = dinnerDropdown;
                break;
            default:
                return;
        }

        if (recyclerView.getVisibility() == View.VISIBLE) {
            recyclerView.setVisibility(View.GONE);
            dropdown.setText("▼");
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            dropdown.setText("▲");
        }
    }

    private void updateMealCounts() {
        breakfastCount.setText(breakfastList.size() + " items");
        lunchCount.setText(lunchList.size() + " items");
        dinnerCount.setText(dinnerList.size() + " items");
    }

    private void refreshMealDetails() {
        String currentDate = MealDataManager.getInstance().getCurrentDate();
        
        breakfastList.clear();
        breakfastList.addAll(MealDataManager.getInstance().getMealDetailsForMealType("Breakfast", currentDate));
        breakfastAdapter.notifyDataSetChanged();
        
        lunchList.clear();
        lunchList.addAll(MealDataManager.getInstance().getMealDetailsForMealType("Lunch", currentDate));
        lunchAdapter.notifyDataSetChanged();
        
        dinnerList.clear();
        dinnerList.addAll(MealDataManager.getInstance().getMealDetailsForMealType("Dinner", currentDate));
        dinnerAdapter.notifyDataSetChanged();
        
        // Update counts
        updateMealCounts();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshMealDetails();
    }
}


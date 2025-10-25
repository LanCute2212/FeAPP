package com.example.caloriesapp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MealDetail {
    
    private String mealType; // "Breakfast", "Lunch", "Dinner"
    private String foodName;
    private String servingSize;
    private int calories;
    private int iconResource;
    private String protein;
    private String carbs;
    private String fat;
    private String date; // Format: "yyyy-MM-dd"
    private long timestamp; // For sorting and uniqueness

    public MealDetail(String mealType, FoodItem foodItem, String date) {
        this.mealType = mealType;
        this.foodName = foodItem.getName();
        this.servingSize = foodItem.getServingSize();
        this.calories = foodItem.getCalories();
        this.iconResource = foodItem.getIconResource();
        this.protein = foodItem.getProtein();
        this.carbs = foodItem.getCarbs();
        this.fat = foodItem.getFat();
        this.date = date;
        this.timestamp = System.currentTimeMillis();
    }

    public String getNutritionSummary() {
        return servingSize + ", " + calories + " kcal";
    }

    public String getMacroSummary() {
        return protein + "g protein • " + carbs + "g carbs • " + fat + "g fat";
    }
}


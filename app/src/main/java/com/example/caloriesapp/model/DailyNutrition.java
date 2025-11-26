package com.example.caloriesapp.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "daily_nutrition")
public class DailyNutrition {
    @PrimaryKey
    @NonNull
    private String date;
    
    private double calories;
    private double carbs;
    private double fat;
    private double protein;
    private double consumed;
    
    public DailyNutrition() {
    }
    
    public DailyNutrition(@NonNull String date, double calories, double carbs, double fat, double protein, double consumed) {
        this.date = date;
        this.calories = calories;
        this.carbs = carbs;
        this.fat = fat;
        this.protein = protein;
        this.consumed = consumed;
    }
    
    @NonNull
    public String getDate() {
        return date;
    }
    
    public void setDate(@NonNull String date) {
        this.date = date;
    }
    
    public double getCalories() {
        return calories;
    }
    
    public void setCalories(double calories) {
        this.calories = calories;
    }
    
    public double getCarbs() {
        return carbs;
    }
    
    public void setCarbs(double carbs) {
        this.carbs = carbs;
    }
    
    public double getFat() {
        return fat;
    }
    
    public void setFat(double fat) {
        this.fat = fat;
    }
    
    public double getProtein() {
        return protein;
    }
    
    public void setProtein(double protein) {
        this.protein = protein;
    }
    
    public double getConsumed() {
        return consumed;
    }
    
    public void setConsumed(double consumed) {
        this.consumed = consumed;
    }
}


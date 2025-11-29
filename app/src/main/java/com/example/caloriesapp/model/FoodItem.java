package com.example.caloriesapp.model;

import java.io.Serializable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FoodItem implements Serializable {

    private String name;
    private String servingSize;
    private int calories;
    private int iconResource;
    private String protein;
    private String carbs;
    private String fat;
    private String imageUrl;

    public FoodItem(String name, String servingSize, int calories, int iconResource, String protein, String carbs, String fat) {
        this.name = name;
        this.servingSize = servingSize;
        this.calories = calories;
        this.iconResource = iconResource;
        this.protein = protein;
        this.carbs = carbs;
        this.fat = fat;
        this.imageUrl = null;
    }

    public FoodItem(String name, String servingSize, int calories, int iconResource, String protein, String carbs, String fat, String imageUrl) {
        this.name = name;
        this.servingSize = servingSize;
        this.calories = calories;
        this.iconResource = iconResource;
        this.protein = protein;
        this.carbs = carbs;
        this.fat = fat;
        this.imageUrl = imageUrl;
    }

    public String getNutritionSummary() {
        return servingSize + ", " + calories + " kcal";
    }

    public String getMacroSummary() {
        return protein + "g protein • " + carbs + "g carbs • " + fat + "g fat";
    }
}



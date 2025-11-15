package com.example.caloriesapp.model;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FoodItem implements Serializable {

    private String name;
    private String servingSize;
    private int calories;
    private int iconResource;
    private String protein;
    private String carbs;
    private String fat;

    public String getNutritionSummary() {
        return servingSize + ", " + calories + " kcal";
    }

    public String getMacroSummary() {
        return protein + "g protein • " + carbs + "g carbs • " + fat + "g fat";
    }
}


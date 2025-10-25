package com.example.caloriesapp.util;

import com.example.caloriesapp.model.MealDetail;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MealDataManager {
    private static MealDataManager instance;
    private List<MealDetail> mealDetails;

    private MealDataManager() {
        mealDetails = new ArrayList<>();
    }

    public static MealDataManager getInstance() {
        if (instance == null) {
            instance = new MealDataManager();
        }
        return instance;
    }

    public void addMealDetail(MealDetail mealDetail) {
        mealDetails.add(mealDetail);
    }

    public void removeMealDetail(MealDetail mealDetail) {
        mealDetails.remove(mealDetail);
    }

    public List<MealDetail> getMealDetailsForDate(String date) {
        List<MealDetail> result = new ArrayList<>();
        for (MealDetail mealDetail : mealDetails) {
            if (mealDetail.getDate().equals(date)) {
                result.add(mealDetail);
            }
        }
        return result;
    }

    public List<MealDetail> getMealDetailsForMealType(String mealType, String date) {
        List<MealDetail> result = new ArrayList<>();
        for (MealDetail mealDetail : mealDetails) {
            if (mealDetail.getMealType().equals(mealType) && mealDetail.getDate().equals(date)) {
                result.add(mealDetail);
            }
        }
        return result;
    }

    public int getTotalCaloriesForMealType(String mealType, String date) {
        int total = 0;
        for (MealDetail mealDetail : mealDetails) {
            if (mealDetail.getMealType().equals(mealType) && mealDetail.getDate().equals(date)) {
                total += mealDetail.getCalories();
            }
        }
        return total;
    }

    public String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // Month is 0-based
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return String.format("%04d-%02d-%02d", year, month, day);
    }
}


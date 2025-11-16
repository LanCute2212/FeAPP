package com.example.caloriesapp.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.caloriesapp.model.DailyNutrition;

import java.util.List;

@Dao
public interface DailyNutritionDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DailyNutrition dailyNutrition);
    
    @Update
    void update(DailyNutrition dailyNutrition);
    
    @Query("SELECT * FROM daily_nutrition WHERE date = :date")
    DailyNutrition getByDate(String date);
    
    @Query("SELECT * FROM daily_nutrition ORDER BY date DESC")
    List<DailyNutrition> getAll();
    
    @Query("SELECT * FROM daily_nutrition WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    List<DailyNutrition> getByDateRange(String startDate, String endDate);
    
    @Query("DELETE FROM daily_nutrition WHERE date = :date")
    void deleteByDate(String date);
    
    @Query("DELETE FROM daily_nutrition")
    void deleteAll();
}










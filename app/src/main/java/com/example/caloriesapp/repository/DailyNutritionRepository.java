package com.example.caloriesapp.repository;

import android.os.AsyncTask;

import com.example.caloriesapp.dao.DailyNutritionDao;
import com.example.caloriesapp.database.AppDatabase;
import com.example.caloriesapp.model.DailyNutrition;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DailyNutritionRepository {
    private DailyNutritionDao dailyNutritionDao;
    private ExecutorService executorService;
    
    public DailyNutritionRepository(AppDatabase database) {
        this.dailyNutritionDao = database.dailyNutritionDao();
        this.executorService = Executors.newSingleThreadExecutor();
    }
    
    public void insert(DailyNutrition dailyNutrition) {
        executorService.execute(() -> {
            dailyNutritionDao.insert(dailyNutrition);
        });
    }
    
    public void update(DailyNutrition dailyNutrition) {
        executorService.execute(() -> {
            dailyNutritionDao.update(dailyNutrition);
        });
    }
    
    public void getByDate(String date, OnDataLoadedListener<DailyNutrition> listener) {
        executorService.execute(() -> {
            DailyNutrition nutrition = dailyNutritionDao.getByDate(date);
            if (listener != null) {
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                    listener.onDataLoaded(nutrition);
                });
            }
        });
    }
    
    public void getAll(OnDataLoadedListener<List<DailyNutrition>> listener) {
        executorService.execute(() -> {
            List<DailyNutrition> all = dailyNutritionDao.getAll();
            if (listener != null) {
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                    listener.onDataLoaded(all);
                });
            }
        });
    }
    
    public void getByDateRange(String startDate, String endDate, OnDataLoadedListener<List<DailyNutrition>> listener) {
        executorService.execute(() -> {
            List<DailyNutrition> range = dailyNutritionDao.getByDateRange(startDate, endDate);
            if (listener != null) {
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                    listener.onDataLoaded(range);
                });
            }
        });
    }
    
    public void deleteByDate(String date) {
        executorService.execute(() -> {
            dailyNutritionDao.deleteByDate(date);
        });
    }
    
    public interface OnDataLoadedListener<T> {
        void onDataLoaded(T data);
    }
}





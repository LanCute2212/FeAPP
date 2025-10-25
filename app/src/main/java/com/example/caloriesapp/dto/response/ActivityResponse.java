package com.example.caloriesapp.dto.response;

public class ActivityResponse {
    private int id;
    private String name;
    private Double caloriesBurnedPer30Minutes;

    public ActivityResponse() {}

    public ActivityResponse(int id, String name, Double caloriesBurned) {
        this.id = id;
        this.name = name;
        this.caloriesBurnedPer30Minutes = caloriesBurned;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getCaloriesBurned() {
        return caloriesBurnedPer30Minutes;
    }

    public void setCaloriesBurned(Double caloriesBurned) {
        this.caloriesBurnedPer30Minutes = caloriesBurned;
    }
}
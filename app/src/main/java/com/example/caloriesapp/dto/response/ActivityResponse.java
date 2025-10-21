package com.example.caloriesapp.dto.response;

public class ActivityResponse {
    private int id;
    private String name;
    private Integer caloriesBurned;

    public ActivityResponse() {}

    public ActivityResponse(int id, String name, Integer caloriesBurned) {
        this.id = id;
        this.name = name;
        this.caloriesBurned = caloriesBurned;
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

    public Integer getCaloriesBurned() {
        return caloriesBurned;
    }

    public void setCaloriesBurned(Integer caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }
}
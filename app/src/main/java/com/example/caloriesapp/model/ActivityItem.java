package com.example.caloriesapp.model;

public class ActivityItem {
    private String name;
    private String duration;
    private int calories;
    private int iconResource;
    private String intensity;
    private String distance;
    private String date;

    public ActivityItem(String name, String duration, int calories, int iconResource, 
                       String intensity, String distance, String date) {
        this.name = name;
        this.duration = duration;
        this.calories = calories;
        this.iconResource = iconResource;
        this.intensity = intensity;
        this.distance = distance;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public String getDuration() {
        return duration;
    }

    public int getCalories() {
        return calories;
    }

    public int getIconResource() {
        return iconResource;
    }

    public String getIntensity() {
        return intensity;
    }

    public String getDistance() {
        return distance;
    }

    public String getDate() {
        return date;
    }

    public String getSummary() {
        return duration + " • " + calories + " kcal";
    }
}

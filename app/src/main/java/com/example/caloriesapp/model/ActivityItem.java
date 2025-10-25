package com.example.caloriesapp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ActivityItem {

  private String name;
  private String duration;
  private Double calories;
  private int iconResource;
  private String intensity;
  private String distance;
  private String date;

  public ActivityItem(String name, String s, Double caloriesBurned, int icLightning) {
    this.name = name;
    this.duration = s;
    this.calories = caloriesBurned;
    this.iconResource = icLightning;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDuration() {
    return duration;
  }

  public void setDuration(String duration) {
    this.duration = duration;
  }

  public Double getCalories() {
    return calories;
  }

  public void setCalories(Double calories) {
    this.calories = calories;
  }

  public int getIconResource() {
    return iconResource;
  }

  public void setIconResource(int iconResource) {
    this.iconResource = iconResource;
  }

  public String getIntensity() {
    return intensity;
  }

  public void setIntensity(String intensity) {
    this.intensity = intensity;
  }

  public String getDistance() {
    return distance;
  }

  public void setDistance(String distance) {
    this.distance = distance;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public String getSummary() {
    return duration + " • " + calories + " kcal";
  }
}

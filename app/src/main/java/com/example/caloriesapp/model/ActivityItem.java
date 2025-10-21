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
  private int calories;
  private int iconResource;
  private String intensity;
  private String distance;
  private String date;

  public String getSummary() {
    return duration + " • " + calories + " kcal";
  }
}

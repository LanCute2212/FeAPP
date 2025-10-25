package com.example.caloriesapp.dto.response;

import lombok.Data;

@Data
public class ActivityDto {
    private int id;
    private String name;
    private int caloriesBurnedPer30Minutes;
}
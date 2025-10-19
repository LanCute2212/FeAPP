package com.example.caloriesapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePhysicalProfileForm {
    private Double age;
    private String gender;
    private double weight;
    private double height;
    private Double activityLevel;
    private Double goal;
}

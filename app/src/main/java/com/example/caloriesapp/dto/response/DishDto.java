package com.example.caloriesapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DishDto {
    private Integer id;

    private String name;

    private Double carb;

    private Double fat;

    private Double protein;

    private Double fiber;

    private String imageUrl;

    private Double calories;

    private String des;

    private Unit unit;

    private String barcode;

    private String servingSize;

    public static enum Unit {
        GRAM,
        ML
    }
}

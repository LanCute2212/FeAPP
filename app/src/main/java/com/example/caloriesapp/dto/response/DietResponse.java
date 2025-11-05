package com.example.caloriesapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DietResponse {
  private String name;
  private int carbPercent;

  private int fatPercent;

  private int proteinPercent;

}

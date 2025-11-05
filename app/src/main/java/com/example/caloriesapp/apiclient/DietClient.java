package com.example.caloriesapp.apiclient;

import com.example.caloriesapp.dto.response.BaseResponse;
import com.example.caloriesapp.dto.response.DietResponse;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface DietClient {
  @GET("api/diets")
  Call<BaseResponse<java.util.List<DietResponse>>> getDiets();

}

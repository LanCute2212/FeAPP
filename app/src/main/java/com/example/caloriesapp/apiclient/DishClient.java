package com.example.caloriesapp.apiclient;

import com.example.caloriesapp.dto.response.BaseResponse;
import com.example.caloriesapp.dto.response.DishDto;

import retrofit2.http.GET;
import retrofit2.http.Path;

public interface DishClient {
    @GET("/api/dishes/barcode/{code}")
    retrofit2.Call<BaseResponse<DishDto>> getDishByBarcode(@Path("code") String code);
}

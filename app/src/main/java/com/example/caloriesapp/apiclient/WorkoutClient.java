package com.example.caloriesapp.apiclient;

import com.example.caloriesapp.dto.response.BaseResponse;
import com.example.caloriesapp.dto.response.WorkoutResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WorkoutClient {
    @GET("/api/workout")
    Call<BaseResponse<List<WorkoutResponse>>> getWorkouts();

    @GET("/api/workout/{trainingId}")
    Call<BaseResponse<List<WorkoutResponse>>> getWorkouts(@Query("trainingId") Long trainingId);
}

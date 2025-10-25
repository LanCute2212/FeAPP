package com.example.caloriesapp.apiclient;

import com.example.caloriesapp.dto.request.LogActivityRequest;
import com.example.caloriesapp.dto.response.ActivityResponse;
import com.example.caloriesapp.dto.response.BaseResponse;
import com.example.caloriesapp.dto.response.ActivityLogResponse;

import java.util.List;

import kotlin.ParameterName;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ActivityClient {
    @GET("api/activities")
    Call<BaseResponse<List<ActivityResponse>>> getActivityList(@Query("id") Integer userId);

    @POST("api/activity-logs")
    Call<BaseResponse<ActivityLogResponse>> createActivityLog(@Body LogActivityRequest request);
}

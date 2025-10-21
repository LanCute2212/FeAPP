package com.example.caloriesapp.apiclient;

import com.example.caloriesapp.dto.response.ActivityResponse;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ActivityClient {
    @GET("api/activity")
    Call<List<ActivityResponse>> getActivityList();
}

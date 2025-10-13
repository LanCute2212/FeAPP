package com.example.caloriesapp.apiclient;

import com.example.caloriesapp.dto.request.UserRegisterForm;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserClient {
    @POST("api/register")
    Call<String> registerUser(@Body UserRegisterForm userRegisterForm);
}

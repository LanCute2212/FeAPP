package com.example.caloriesapp.apiclient;

import com.example.caloriesapp.dto.request.PhysicalEditProfileForm;
import com.example.caloriesapp.dto.request.UserLoginForm;
import com.example.caloriesapp.dto.request.UserRegisterForm;
import com.example.caloriesapp.dto.response.LoginResponse;
import com.example.caloriesapp.dto.response.PhysicalProfileForm;
import com.example.caloriesapp.dto.response.UpdatePhysicalProfileForm;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface UserClient {
    @POST("api/register")
    Call<Map<String, Object>> registerUser(@Body UserRegisterForm userRegisterForm);
    @POST("api/login")
    Call<LoginResponse> login(@Body UserLoginForm userLoginForm);

    @PUT("api/user")
    Call<Void> update(@Query("email") String email, @Body PhysicalEditProfileForm form);

    @GET("api/user/getInfo")
    Call<PhysicalProfileForm> getInfo(@Query("email") String email);

}

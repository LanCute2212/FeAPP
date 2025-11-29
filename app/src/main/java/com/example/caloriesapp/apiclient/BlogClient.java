package com.example.caloriesapp.apiclient;

import com.example.caloriesapp.dto.response.BaseResponse;
import com.example.caloriesapp.dto.response.CategoryResponse;
import com.example.caloriesapp.dto.response.PostSummaryResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BlogClient {
    @GET("api/blog/posts")
    Call<BaseResponse<List<PostSummaryResponse>>> getAllPosts();

    @GET("api/blog/categories")
    Call<BaseResponse<List<CategoryResponse>>> getAllCategories();

    @GET("api/blog/posts/{id}")
    Call<BaseResponse<PostSummaryResponse>> getPostDetail(@Path("id") Long postId);
}

package com.example.caloriesapp.repository;

import com.example.caloriesapp.apiclient.ApiClient;
import com.example.caloriesapp.apiclient.BlogClient;
import com.example.caloriesapp.dto.response.BaseResponse;
import com.example.caloriesapp.dto.response.CategoryResponse;
import com.example.caloriesapp.dto.response.PostSummaryResponse;

import java.util.List;

import retrofit2.Call;

public class BlogRepository {
    private final BlogClient blogClient;

    public BlogRepository() {
        this.blogClient = ApiClient.getClient().create(BlogClient.class);
    }

    public Call<BaseResponse<List<PostSummaryResponse>>> getAllPosts() {
        return blogClient.getAllPosts();
    }

    public Call<BaseResponse<List<CategoryResponse>>> getAllCategories() {
        return blogClient.getAllCategories();
    }

    public Call<BaseResponse<PostSummaryResponse>> getPostDetail(Long postId) {
        return blogClient.getPostDetail(postId);
    }
}

package com.example.caloriesapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.caloriesapp.dto.response.BaseResponse;
import com.example.caloriesapp.dto.response.PostSummaryResponse;
import com.example.caloriesapp.repository.BlogRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BlogDetailActivity extends AppCompatActivity {
    public static final String EXTRA_POST_ID = "extra_post_id";

    private ImageView ivBanner;
    private TextView tvTitle;
    private TextView tvMeta;
    private TextView tvContent;
    private ProgressBar progressBar;
    private BlogRepository blogRepository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_detail);
        blogRepository = new BlogRepository();
        initializeToolbar();
        initializeViews();
        long postId = getIntent().getLongExtra(EXTRA_POST_ID, -1);
        if (postId <= 0) {
            Toast.makeText(this, "Invalid article", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        loadPostDetail(postId);
    }

    private void initializeToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbarDetail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void initializeViews() {
        ivBanner = findViewById(R.id.ivBanner);
        tvTitle = findViewById(R.id.tvDetailTitle);
        tvMeta = findViewById(R.id.tvDetailMeta);
        tvContent = findViewById(R.id.tvDetailContent);
        progressBar = findViewById(R.id.progressDetail);
    }

    private void loadPostDetail(long postId) {
        showLoading(true);
        blogRepository.getPostDetail(postId).enqueue(new Callback<BaseResponse<PostSummaryResponse>>() {
            @Override
            public void onResponse(Call<BaseResponse<PostSummaryResponse>> call, Response<BaseResponse<PostSummaryResponse>> response) {
                showLoading(false);
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(BlogDetailActivity.this, "Failed to load article", Toast.LENGTH_SHORT).show();
                    return;
                }
                BaseResponse<PostSummaryResponse> baseResponse = response.body();
                if (baseResponse.isError() || baseResponse.getData() == null) {
                    Toast.makeText(BlogDetailActivity.this, baseResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                bindPost(baseResponse.getData());
            }

            @Override
            public void onFailure(Call<BaseResponse<PostSummaryResponse>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(BlogDetailActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bindPost(PostSummaryResponse post) {
        tvTitle.setText(post.getTitle());
        String author = post.getAuthorName() != null ? post.getAuthorName() : "";
        String category = post.getCategoryName() != null ? post.getCategoryName() : "";
        String meta = author;
        if (!author.isEmpty() && !category.isEmpty()) {
            meta = author + " • " + category;
        } else if (author.isEmpty()) {
            meta = category;
        }
        tvMeta.setText(meta);
        tvContent.setText(post.getContent());
        if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(post.getImageUrl())
                    .placeholder(R.drawable.bg_blog_card)
                    .error(R.drawable.bg_blog_card)
                    .centerCrop()
                    .into(ivBanner);
        } else {
            ivBanner.setImageResource(R.drawable.bg_blog_card);
        }
    }

    private void showLoading(boolean isLoading) {
        if (progressBar == null) {
            return;
        }
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }
}



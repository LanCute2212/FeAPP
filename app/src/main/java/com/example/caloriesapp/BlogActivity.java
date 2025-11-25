package com.example.caloriesapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.caloriesapp.adapter.BlogAdapter;
import com.example.caloriesapp.model.BlogArticle;
import com.example.caloriesapp.repository.BlogRepository;

import java.util.ArrayList;
import java.util.List;

public class BlogActivity extends AppCompatActivity {
    private RecyclerView rvBlogArticles;
    private BlogAdapter blogAdapter;
    private List<BlogArticle> articleList;
    private ImageView ivFeaturedImage;
    private TextView tvFeaturedTitle;
    private ImageView ivSearch;
    private BlogRepository blogRepository;

    private android.widget.LinearLayout llCategoryContainer;
    private String selectedCategory = "all";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog);

        blogRepository = new BlogRepository();
        initializeViews();
        setupFeaturedArticle();
        setupCategoryChips();
        setupRecyclerView();
        setupBottomNavigation();
        loadArticles();
        loadCategories();
    }

    private void initializeViews() {
        rvBlogArticles = findViewById(R.id.rvBlogArticles);
        ivFeaturedImage = findViewById(R.id.ivFeaturedImage);
        tvFeaturedTitle = findViewById(R.id.tvFeaturedTitle);
        ivSearch = findViewById(R.id.ivSearch);

        llCategoryContainer = findViewById(R.id.llCategoryContainer);

        ivSearch.setOnClickListener(v -> {
            Toast.makeText(this, "Search functionality coming soon", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupFeaturedArticle() {
        tvFeaturedTitle.setText("5 Thực đơn Eat Clean cho người bận rộn");
    }

    private void setupCategoryChips() {
        addCategoryChip("All", "all");
        selectCategory("all");
    }

    private void addCategoryChip(String name, String categoryId) {
        TextView chip = new TextView(this);
        chip.setText(name);
        chip.setTextSize(14);

        android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
        int marginEnd = (int) (8 * getResources().getDisplayMetrics().density);
        params.setMargins(0, 0, marginEnd, 0);
        chip.setLayoutParams(params);

        int paddingStartEnd = (int) (20 * getResources().getDisplayMetrics().density);
        int paddingTopBottom = (int) (8 * getResources().getDisplayMetrics().density);
        chip.setPadding(paddingStartEnd, paddingTopBottom, paddingStartEnd, paddingTopBottom);

        chip.setOnClickListener(v -> selectCategory(categoryId));

        chip.setTag(categoryId);

        llCategoryContainer.addView(chip);
    }

    private void selectCategory(String category) {
        selectedCategory = category;

        for (int i = 0; i < llCategoryContainer.getChildCount(); i++) {
            View child = llCategoryContainer.getChildAt(i);
            if (child instanceof TextView) {
                TextView tv = (TextView) child;
                String tag = (String) tv.getTag();

                if (category.equals(tag)) {
                    tv.setTextColor(getResources().getColor(R.color.white));
                    tv.setBackground(null);
                } else {
                    tv.setBackgroundResource(R.drawable.bg_category_chip_unselected);
                    tv.setTextColor(getResources().getColor(R.color.blogTextPrimary));
                }
            }
        }

    }

    private void setupRecyclerView() {
        rvBlogArticles.setLayoutManager(new LinearLayoutManager(this));
        articleList = new ArrayList<>();
        blogAdapter = new BlogAdapter(articleList);
        rvBlogArticles.setAdapter(blogAdapter);

        blogAdapter.setOnArticleClickListener((article, position) -> {
            Toast.makeText(this, "Clicked: " + article.getTitle(), Toast.LENGTH_SHORT).show();
            // TODO: Open article detail activity
        });
    }

    private void loadArticles() {
        blogRepository.getAllPosts().enqueue(
                new retrofit2.Callback<com.example.caloriesapp.dto.response.BaseResponse<List<com.example.caloriesapp.dto.response.PostSummaryResponse>>>() {
                    @Override
                    public void onResponse(
                            retrofit2.Call<com.example.caloriesapp.dto.response.BaseResponse<List<com.example.caloriesapp.dto.response.PostSummaryResponse>>> call,
                            retrofit2.Response<com.example.caloriesapp.dto.response.BaseResponse<List<com.example.caloriesapp.dto.response.PostSummaryResponse>>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            com.example.caloriesapp.dto.response.BaseResponse<List<com.example.caloriesapp.dto.response.PostSummaryResponse>> baseResponse = response
                                    .body();

                            if (!baseResponse.isError() && baseResponse.getData() != null) {
                                articleList.clear();

                                for (com.example.caloriesapp.dto.response.PostSummaryResponse post : baseResponse
                                        .getData()) {
                                    String timeAgo = formatTimeAgo(post.getCreatedAt());
                                    BlogArticle article = new BlogArticle(
                                            post.getId(),
                                            post.getTitle(),
                                            post.getCategoryName() != null ? post.getCategoryName() : "General",
                                            timeAgo,
                                            post.getImageUrl() != null ? post.getImageUrl() : "",
                                            false);
                                    articleList.add(article);
                                }

                                blogAdapter.notifyDataSetChanged();

                                if (!articleList.isEmpty()) {
                                    BlogArticle featuredArticle = articleList.get(0);
                                    tvFeaturedTitle.setText(featuredArticle.getTitle());
                                    if (featuredArticle.getImageUrl() != null && !featuredArticle.getImageUrl().isEmpty()) {
                                        Glide.with(BlogActivity.this)
                                                .load(featuredArticle.getImageUrl())
                                                .placeholder(R.drawable.bg_blog_card)
                                                .error(R.drawable.bg_blog_card)
                                                .centerCrop()
                                                .into(ivFeaturedImage);
                                    }
                                }
                            } else {
                                Toast.makeText(BlogActivity.this, "Error: " + baseResponse.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                                loadSampleData();
                            }
                        } else {
                            Toast.makeText(BlogActivity.this, "Failed to load articles", Toast.LENGTH_SHORT).show();
                            loadSampleData();
                        }
                    }

                    @Override
                    public void onFailure(
                            retrofit2.Call<com.example.caloriesapp.dto.response.BaseResponse<List<com.example.caloriesapp.dto.response.PostSummaryResponse>>> call,
                            Throwable t) {
                        Toast.makeText(BlogActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT)
                                .show();
                        loadSampleData();
                    }
                });
    }

    private void loadSampleData() {
        articleList.clear();
        articleList.add(new BlogArticle(1, "10 Bài tập Cardio đốt mỡ tại nhà không cần dụng cụ",
                "HLV Tuấn Anh", "2 giờ trước", 0, false));
        articleList.add(new BlogArticle(2, "Tại sao uống đủ nước lại giúp giảm cân?",
                "Dinh dưỡng", "1 ngày trước", 0, false));
        articleList.add(new BlogArticle(3, "Lịch tập Gym cho người mới bắt đầu",
                "PT Hùng", "3 ngày trước", 0, false));
        articleList.add(new BlogArticle(4, "Cách tính Macro chuẩn xác",
                "Nutritionist", "4 ngày trước", 0, false));

        blogAdapter.notifyDataSetChanged();
    }

    private String formatTimeAgo(java.time.LocalDateTime createdAt) {
        if (createdAt == null) {
            return "Unknown";
        }

        try {
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            java.time.Duration duration = java.time.Duration.between(createdAt, now);

            long days = duration.toDays();
            long hours = duration.toHours();
            long minutes = duration.toMinutes();

            if (days > 0) {
                return days + " ngày trước";
            } else if (hours > 0) {
                return hours + " giờ trước";
            } else if (minutes > 0) {
                return minutes + " phút trước";
            } else {
                return "Vừa xong";
            }
        } catch (Exception e) {
            return "Unknown";
        }
    }

    private void setupBottomNavigation() {
        int greenColor = 0xFF4CAF50;
        int grayColor = 0xFF9E9E9E;

        ImageView exploreIcon = findViewById(R.id.bottom_explore_icon);
        TextView exploreLabel = findViewById(R.id.bottom_explore_label);

        if (exploreIcon != null) {
            exploreIcon.setColorFilter(greenColor);
        }
        if (exploreLabel != null) {
            exploreLabel.setTextColor(greenColor);
        }

        ImageView journalIcon = findViewById(R.id.bottom_left_icon);
        TextView journalLabel = findViewById(R.id.bottom_journal_label);
        ImageView workoutIcon = findViewById(R.id.bottom_workout_icon);
        TextView workoutLabel = findViewById(R.id.bottom_workout_label);
        ImageView helpIcon = findViewById(R.id.bottom_right_icon);
        TextView helpLabel = findViewById(R.id.bottom_help_label);

        if (journalIcon != null) {
            journalIcon.setColorFilter(grayColor);
        }
        if (journalLabel != null) {
            journalLabel.setTextColor(grayColor);
        }
        if (workoutIcon != null) {
            workoutIcon.setColorFilter(grayColor);
        }
        if (workoutLabel != null) {
            workoutLabel.setTextColor(grayColor);
        }
        if (helpIcon != null) {
            helpIcon.setColorFilter(grayColor);
        }
        if (helpLabel != null) {
            helpLabel.setTextColor(grayColor);
        }

        findViewById(R.id.bottom_left_icon).setOnClickListener(v -> {
            Intent intent = new Intent(this, HomePageActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.bottom_journal_label).setOnClickListener(v -> {
            Intent intent = new Intent(this, HomePageActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.bottom_workout_icon).setOnClickListener(v -> {
            Intent intent = new Intent(this, WorkoutListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.bottom_workout_label).setOnClickListener(v -> {
            Intent intent = new Intent(this, WorkoutListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.bottom_explore_icon).setOnClickListener(v -> {
        });

        findViewById(R.id.bottom_explore_label).setOnClickListener(v -> {
            // Already on this screen
        });
    }

    private void loadCategories() {
        blogRepository.getAllCategories().enqueue(
                new retrofit2.Callback<com.example.caloriesapp.dto.response.BaseResponse<List<com.example.caloriesapp.dto.response.CategoryResponse>>>() {
                    @Override
                    public void onResponse(
                            retrofit2.Call<com.example.caloriesapp.dto.response.BaseResponse<List<com.example.caloriesapp.dto.response.CategoryResponse>>> call,
                            retrofit2.Response<com.example.caloriesapp.dto.response.BaseResponse<List<com.example.caloriesapp.dto.response.CategoryResponse>>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            com.example.caloriesapp.dto.response.BaseResponse<List<com.example.caloriesapp.dto.response.CategoryResponse>> baseResponse = response
                                    .body();

                            if (!baseResponse.isError() && baseResponse.getData() != null) {
                                List<com.example.caloriesapp.dto.response.CategoryResponse> categories = baseResponse
                                        .getData();

                                llCategoryContainer.removeAllViews();

                                addCategoryChip("All", "all");

                                for (com.example.caloriesapp.dto.response.CategoryResponse cat : categories) {
                                    addCategoryChip(cat.getName(), String.valueOf(cat.getId()));
                                }

                                selectCategory(selectedCategory);
                            }
                        }
                    }

                    @Override
                    public void onFailure(
                            retrofit2.Call<com.example.caloriesapp.dto.response.BaseResponse<List<com.example.caloriesapp.dto.response.CategoryResponse>>> call,
                            Throwable t) {
                    }
                });
    }
}

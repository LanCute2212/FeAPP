package com.example.caloriesapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caloriesapp.adapter.WorkoutAdapter;
import com.example.caloriesapp.apiclient.ApiClient;
import com.example.caloriesapp.apiclient.WorkoutClient;
import com.example.caloriesapp.dto.response.BaseResponse;
import com.example.caloriesapp.dto.response.WorkoutResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkoutListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private WorkoutAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private List<WorkoutResponse> workoutList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_list);

        recyclerView = findViewById(R.id.recycler_view_workouts);
        progressBar = findViewById(R.id.progress_bar);
        tvEmpty = findViewById(R.id.tv_empty);

        workoutList = new ArrayList<>();
        adapter = new WorkoutAdapter(workoutList, workout -> {
            // Handle workout click - can navigate to detail screen later
            Toast.makeText(this, "Clicked: " + workout.getName(), Toast.LENGTH_SHORT).show();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        setupBottomNavigation();
        loadWorkouts();
    }

    private void setupBottomNavigation() {
        int greenColor = 0xFF4CAF50; // Green color
        int grayColor = 0xFF9E9E9E; // Gray color for inactive tabs

        // Highlight Exercise tab (current screen)
        ImageView workoutIcon = findViewById(R.id.bottom_workout_icon);
        TextView workoutLabel = findViewById(R.id.bottom_workout_label);

        if (workoutIcon != null) {
            workoutIcon.setColorFilter(greenColor);
        }
        if (workoutLabel != null) {
            workoutLabel.setTextColor(greenColor);
        }

        // Set other inactive tabs to gray
        ImageView journalIcon = findViewById(R.id.bottom_left_icon);
        TextView journalLabel = findViewById(R.id.bottom_journal_label);
        if (journalIcon != null) {
            journalIcon.setColorFilter(greenColor); // Diary icon stays green (default)
        }
        if (journalLabel != null) {
            journalLabel.setTextColor(grayColor); // Diary label gray when not active
        }
        ImageView exploreIcon = findViewById(R.id.bottom_explore_icon);
        TextView exploreLabel = findViewById(R.id.bottom_explore_label);
        ImageView helpIcon = findViewById(R.id.bottom_right_icon);
        TextView helpLabel = findViewById(R.id.bottom_help_label);

        if (exploreIcon != null) {
            exploreIcon.setColorFilter(grayColor);
        }
        if (exploreLabel != null) {
            exploreLabel.setTextColor(grayColor);
        }
        if (helpIcon != null) {
            helpIcon.setColorFilter(grayColor);
        }
        if (helpLabel != null) {
            helpLabel.setTextColor(grayColor);
        }

        // Set click listeners
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

        // Exercise tab - already here, do nothing
        findViewById(R.id.bottom_workout_icon).setOnClickListener(v -> {
            // Already on this screen
        });

        findViewById(R.id.bottom_workout_label).setOnClickListener(v -> {
            // Already on this screen
        });

        // Blog/Explore tab - navigate to BlogActivity
        findViewById(R.id.bottom_explore_icon).setOnClickListener(v -> {
            Intent intent = new Intent(this, BlogActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.bottom_explore_label).setOnClickListener(v -> {
            Intent intent = new Intent(this, BlogActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void loadWorkouts() {
        progressBar.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);

        WorkoutClient workoutClient = ApiClient.getClient().create(WorkoutClient.class);
        Call<BaseResponse<List<WorkoutResponse>>> call = workoutClient.getWorkouts();

        call.enqueue(new Callback<BaseResponse<List<WorkoutResponse>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<WorkoutResponse>>> call,
                    Response<BaseResponse<List<WorkoutResponse>>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse<List<WorkoutResponse>> baseResponse = response.body();
                    if (!baseResponse.isError() && baseResponse.getData() != null) {
                        workoutList.clear();
                        workoutList.addAll(baseResponse.getData());
                        adapter.updateWorkouts(workoutList);

                        if (workoutList.isEmpty()) {
                            tvEmpty.setVisibility(View.VISIBLE);
                        } else {
                            tvEmpty.setVisibility(View.GONE);
                        }
                    } else {
                        showError("Không thể tải danh sách bài tập");
                        tvEmpty.setVisibility(View.VISIBLE);
                    }
                } else {
                    showError("Lỗi kết nối server");
                    tvEmpty.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<WorkoutResponse>>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                showError("Lỗi kết nối: " + t.getMessage());
                tvEmpty.setVisibility(View.VISIBLE);
            }
        });
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

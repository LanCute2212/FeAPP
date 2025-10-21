package com.example.caloriesapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.caloriesapp.adapter.ActivityAdapter;
import com.example.caloriesapp.apiclient.ActivityClient;
import com.example.caloriesapp.dto.response.ActivityResponse;
import com.example.caloriesapp.model.ActivityItem;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListActivity extends AppCompatActivity {
    private ActivityAdapter activityAdapter;
    private static final String BASE_URL = "http://localhost:8081/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listactivity);

        findViewById(R.id.cancel).setOnClickListener(v -> {
            Intent intent = new Intent(ListActivity.this, HomePageActivity.class);
            startActivity(intent);
        });

        setupActivitiesList();
    }

    private void setupActivitiesList() {
        RecyclerView recyclerView = findViewById(R.id.activities_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        activityAdapter = new ActivityAdapter(new ArrayList<>());
        recyclerView.setAdapter(activityAdapter);

        // Set click listener for activity items
        activityAdapter.setOnActivityClickListener((activity, position) -> {
            Log.d("ClickDebug", "Activity clicked: " + activity.getName() + " at position " + position);
            showDurationDialog(activity, position);
        });

        fetchActivitiesFromAPI();
    }

    private void fetchActivitiesFromAPI() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ActivityClient client = retrofit.create(ActivityClient.class);
        Call<List<ActivityResponse>> call = client.getActivityList();

        call.enqueue(new Callback<List<ActivityResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<ActivityResponse>> call, 
                                 @NonNull Response<List<ActivityResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ActivityResponse> apiResponses = response.body();
                    List<ActivityItem> activityItems = mapToActivityItems(apiResponses);
                    
                    activityAdapter = new ActivityAdapter(activityItems);
                    RecyclerView recyclerView = findViewById(R.id.activities_recycler_view);
                    recyclerView.setAdapter(activityAdapter);
                    
                    activityAdapter.setOnActivityClickListener((activity, position) -> {
                        showDurationDialog(activity, position);
                    });
                    
                    Log.d("API", "Successfully loaded " + activityItems.size() + " activities");
                } else {
                    Log.e("API", "Failed to fetch activities: " + response.code());
                    loadFallbackData();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ActivityResponse>> call, @NonNull Throwable t) {
                Log.e("API", "Network error: " + t.getMessage());
                loadFallbackData();
            }
        });
    }

    private List<ActivityItem> mapToActivityItems(List<ActivityResponse> apiResponses) {
        List<ActivityItem> activityItems = new ArrayList<>();
        
        for (ActivityResponse response : apiResponses) {
            ActivityItem item = new ActivityItem(
                response.getName(),
                "30 minutes",
                500,
                R.drawable.ic_lightning,
                "Moderate",
                null,
                "07-10-2025"
            );
            activityItems.add(item);
        }
        
        return activityItems;
    }

    private void loadFallbackData() {
        List<ActivityItem> fallbackActivities = createFallbackActivities();
        activityAdapter = new ActivityAdapter(fallbackActivities);
        RecyclerView recyclerView = findViewById(R.id.activities_recycler_view);
        recyclerView.setAdapter(activityAdapter);
        
        activityAdapter.setOnActivityClickListener((activity, position) -> {
            showDurationDialog(activity, position);
        });
    }

    private List<ActivityItem> createFallbackActivities() {
        List<ActivityItem> activities = new ArrayList<>();
        
        activities.add(new ActivityItem(
            "Tập Thái Cực Quyền",
            "30 minutes",
            500,
            R.drawable.ic_lightning,
            "Moderate",
            null,
            "07-10-2025"
        ));
        
        activities.add(new ActivityItem(
            "Cưỡi ngựa",
            "30 minutes",
            500,
            R.drawable.ic_fire,
            "Moderate",
            null,
            "07-10-2025"
        ));
        
        activities.add(new ActivityItem(
            "Nhảy dây nhanh",
            "30 minutes",
            500,
            R.drawable.ic_badminton,
            "Moderate",
            null,
            "07-10-2025"
        ));
        
        return activities;
    }

    private void showDurationDialog(ActivityItem activity, int position) {
        Log.d("DialogDebug", "showDurationDialog called for: " + activity.getName());
        
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_activity_duration, null);
        
        ImageView activityIcon = dialogView.findViewById(R.id.dialog_activity_icon);
        TextView activityName = dialogView.findViewById(R.id.dialog_activity_name);
        EditText durationInput = dialogView.findViewById(R.id.et_duration);
        Button cancelBtn = dialogView.findViewById(R.id.btn_cancel);
        Button confirmBtn = dialogView.findViewById(R.id.btn_confirm);
        
        activityIcon.setImageResource(activity.getIconResource());
        activityName.setText(activity.getName());
        
        String currentDuration = activity.getDuration().replaceAll("\\D+", ""); // Extract numbers only
        durationInput.setHint("Current: " + activity.getDuration());
        
        // Create dialog
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();
        
        cancelBtn.setOnClickListener(v -> dialog.dismiss());
        
        confirmBtn.setOnClickListener(v -> {
            String durationText = durationInput.getText().toString().trim();
            if (durationText.isEmpty()) {
                durationInput.setError("Please enter duration");
                return;
            }
            
            try {
                int durationMinutes = Integer.parseInt(durationText);
                if (durationMinutes <= 0) {
                    durationInput.setError("Duration must be greater than 0");
                    return;
                }
                
                updateActivityDuration(activity, durationMinutes, position);
                dialog.dismiss();
                
            } catch (NumberFormatException e) {
                durationInput.setError("Please enter a valid number");
            }
        });
        
        Log.d("DialogDebug", "About to show dialog");
        dialog.show();
        Log.d("DialogDebug", "Dialog shown successfully");
    }
    
    private void updateActivityDuration(ActivityItem activity, int durationMinutes, int position) {
        int caloriesPerMinute = 500 / 30;
        int newCalories = caloriesPerMinute * durationMinutes;
        
        ActivityItem updatedActivity = new ActivityItem(
            activity.getName(),
            durationMinutes + " minutes",
            newCalories,
            activity.getIconResource(),
            activity.getIntensity(),
            activity.getDistance(),
            activity.getDate()
        );
        
        activityAdapter.updateActivity(position, updatedActivity);
        
        Intent resultIntent = new Intent();
        resultIntent.putExtra("activity_name", updatedActivity.getName());
        resultIntent.putExtra("duration", updatedActivity.getDuration());
        resultIntent.putExtra("calories", updatedActivity.getCalories());
        resultIntent.putExtra("icon_resource", updatedActivity.getIconResource());
        resultIntent.putExtra("intensity", updatedActivity.getIntensity());
        resultIntent.putExtra("distance", updatedActivity.getDistance());
        resultIntent.putExtra("date", updatedActivity.getDate());
        
        setResult(RESULT_OK, resultIntent);
        finish();
        
        Log.d("ActivityUpdate", "Updated " + activity.getName() + " to " + durationMinutes + " minutes, " + newCalories + " calories");
    }
}
package com.example.caloriesapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caloriesapp.adapter.ActivityAdapter;
import com.example.caloriesapp.model.ActivityItem;

import java.util.ArrayList;
import java.util.List;

public class HomePageActivity extends AppCompatActivity {
    private static final int ADD_ACTIVITY_REQUEST_CODE = 1001;
    private static final int LIST_ACTIVITY_REQUEST_CODE = 1002;
    private String email;
    private ActivityAdapter activityAdapter;
    private List<ActivityItem> activityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topbar);

        email = getIntent().getStringExtra("email");

        findViewById(R.id.icon_bell).setOnClickListener(v -> {
            Intent intent = new Intent(HomePageActivity.this, SettingActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.avatar).setOnClickListener(v -> {
            Intent intent = new Intent(HomePageActivity.this, ProfileActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
        });

        findViewById(R.id.lightning).setOnClickListener(v -> {
            Intent intent = new Intent(HomePageActivity.this, TdeeActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.icon_calendar).setOnClickListener(v -> {
            Intent intent = new Intent(HomePageActivity.this, MonitorActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.add_activity).setOnClickListener(v -> {
            Intent intent = new Intent(HomePageActivity.this, ListActivity.class);
            startActivityForResult(intent, LIST_ACTIVITY_REQUEST_CODE);
        });

        setupActivitiesList();
    }

    // Truyền danh sách vào adapter
    private void setupActivitiesList() {
        RecyclerView recyclerView = findViewById(R.id.activities_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        activityList = createActivityList();
        activityAdapter = new ActivityAdapter(activityList);
        recyclerView.setAdapter(activityAdapter);

        setupSwipeToDelete(recyclerView);
    }

    private void setupSwipeToDelete(RecyclerView recyclerView) {
        ItemTouchHelper.SimpleCallback swipeToDeleteCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                activityAdapter.removeActivity(position);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    // Tạo danh sách hoạt động
    private List<ActivityItem> createActivityList() {
        List<ActivityItem> activities = new ArrayList<>();

        activities.add(new ActivityItem("Badminton", "30 minutes", 150, R.drawable.ic_badminton, "Moderate", null, "07-10-2025"));

        activities.add(new ActivityItem("Running", "45 minutes", 300, R.drawable.ic_fire, "High", "5.2 km", "07-10-2025"));

        activities.add(new ActivityItem("Cycling", "60 minutes", 400, R.drawable.ic_lightning, "Moderate", "15.5 km", "07-10-2025"));

        return activities;
    }

    private void addNewActivity() {
        Intent intent = new Intent(HomePageActivity.this, AddActivity.class);
        startActivityForResult(intent, ADD_ACTIVITY_REQUEST_CODE);
    }
}
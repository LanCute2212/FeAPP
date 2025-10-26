package com.example.caloriesapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caloriesapp.adapter.ActivityAdapter;
import com.example.caloriesapp.model.ActivityItem;

import java.util.ArrayList;
import java.util.Calendar;
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

    setupCollapsibleActivityHeader();
    
    // Setup add activity button
    View addActivityButton = findViewById(R.id.add_activity);
    if (addActivityButton != null) {
      addActivityButton.setOnClickListener(v -> {
        Intent intent = new Intent(HomePageActivity.this, ListActivity.class);
        startActivityForResult(intent, LIST_ACTIVITY_REQUEST_CODE);
      });
    }

    findViewById(R.id.btn_xem_chi_tiet).setOnClickListener(v -> {
      Intent intent = new Intent(HomePageActivity.this, MealDetailsActivity.class);
      startActivity(intent);
    });

    // Add click listeners for meal add buttons
    findViewById(R.id.breakfast_add_button).setOnClickListener(v -> {
      Intent intent = new Intent(HomePageActivity.this, FoodTrackingActivity.class);
      intent.putExtra(FoodTrackingActivity.EXTRA_MEAL_TYPE, "Breakfast");
      startActivity(intent);
    });

    findViewById(R.id.lunch_add_button).setOnClickListener(v -> {
      Intent intent = new Intent(HomePageActivity.this, FoodTrackingActivity.class);
      intent.putExtra(FoodTrackingActivity.EXTRA_MEAL_TYPE, "Lunch");
      startActivity(intent);
    });

    findViewById(R.id.dinner_add_button).setOnClickListener(v -> {
      Intent intent = new Intent(HomePageActivity.this, FoodTrackingActivity.class);
      intent.putExtra(FoodTrackingActivity.EXTRA_MEAL_TYPE, "Dinner");
      startActivity(intent);
    });

    setupActivitiesList();
    populateWeekDates();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == LIST_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
      String activityName = data.getStringExtra("activity_name");
      String duration = data.getStringExtra("duration");
      Double calories = data.getDoubleExtra("calories", 0);
      int iconResource = data.getIntExtra("icon_resource", R.drawable.ic_lightning);
      String intensity = data.getStringExtra("intensity");
      String distance = data.getStringExtra("distance");
      String date = data.getStringExtra("date");

      // check xem người dùng này đã có hoạt động này ở home page chưa
      int existingIndex = findActivityByName(activityName);

      if (existingIndex != -1) {
        ActivityItem existingActivity = activityList.get(existingIndex);
        updateExistingActivity(existingActivity, duration, calories);
        activityAdapter.notifyItemChanged(existingIndex);
        Toast.makeText(this, "Updated " + activityName + " with additional " + duration,
            Toast.LENGTH_SHORT).show();
      } else {
        ActivityItem newActivity = new ActivityItem(
            activityName,
            duration,
            calories,
            iconResource,
            intensity,
            distance,
            date
        );

        activityAdapter.addActivity(newActivity);
        Toast.makeText(this, "Added " + activityName + " to your activities", Toast.LENGTH_SHORT)
            .show();
      }
      
      // Update summary stats after adding/updating activities
      updateSummaryStats();
    }
  }

  private int findActivityByName(String activityName) {
    for (int i = 0; i < activityList.size(); i++) {
      if (activityList.get(i).getName().equals(activityName)) {
        return i;
      }
    }
    return -1;
  }

  private void updateExistingActivity(ActivityItem existingActivity, String newDuration,
      Double newCalories) {
    // lấy lượng thời gian đã có của hoạt động đó ở home page
    String existingDurationStr = existingActivity.getDuration().replaceAll("\\D+", "");
    int existingMinutes = Integer.parseInt(
        existingDurationStr.isEmpty() ? "0" : existingDurationStr);

    String newDurationStr = newDuration.replaceAll("\\D+", "");
    int newMinutes = Integer.parseInt(newDurationStr.isEmpty() ? "0" : newDurationStr);

    // tính lại tổng thời gian và lượng kcal
    int totalMinutes = existingMinutes + newMinutes;
    Double totalCalories = existingActivity.getCalories() + newCalories;

    existingActivity.setDuration(totalMinutes + " minutes");
    existingActivity.setCalories(totalCalories);
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
    ItemTouchHelper.SimpleCallback swipeToDeleteCallback = new ItemTouchHelper.SimpleCallback(0,
        ItemTouchHelper.RIGHT) {
      @Override
      public boolean onMove(@NonNull RecyclerView recyclerView,
          @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
      }

      @Override
      public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        activityAdapter.removeActivity(position);
        // Update summary stats after removing activity
        updateSummaryStats();
      }
    };

    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeToDeleteCallback);
    itemTouchHelper.attachToRecyclerView(recyclerView);
  }

  // Tạo danh sách hoạt động
  private List<ActivityItem> createActivityList() {
    List<ActivityItem> activities = new ArrayList<>();

    return activities;
  }

  private void addNewActivity() {
    Intent intent = new Intent(HomePageActivity.this, AddActivity.class);
    startActivityForResult(intent, ADD_ACTIVITY_REQUEST_CODE);
  }

  private void populateWeekDates() {
    Calendar calendar = Calendar.getInstance();
    Calendar today = Calendar.getInstance();
    
    int currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

    int daysToSubtract = (currentDayOfWeek == Calendar.SUNDAY) ? 6 : currentDayOfWeek - Calendar.MONDAY;
    
    calendar.add(Calendar.DAY_OF_MONTH, -daysToSubtract);
    
    TextView[] dayTextViews = {
        findViewById(R.id.day_1),
        findViewById(R.id.day_2),
        findViewById(R.id.day_3),
        findViewById(R.id.day_4),
        findViewById(R.id.day_5),
        findViewById(R.id.day_6),
        findViewById(R.id.day_7)
    };
    
    for (int i = 0; i < 7; i++) {
      int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
      dayTextViews[i].setText(String.valueOf(dayOfMonth));
      
      boolean isToday = calendar.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH) &&
                       calendar.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                       calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR);
      
      if (isToday) {
        dayTextViews[i].setBackgroundResource(R.drawable.day_background);
      } else {
        dayTextViews[i].setBackground(null);
      }
      
      calendar.add(Calendar.DAY_OF_MONTH, 1);
    }
    
    updateHeaderText();
  }

  private void updateHeaderText() {
    Calendar calendar = Calendar.getInstance();
    
    String[] monthNames = {
        "JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE",
        "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"
    };
    
    String dayOfWeek = getDayOfWeekName(calendar.get(Calendar.DAY_OF_WEEK));
    String month = monthNames[calendar.get(Calendar.MONTH)];
    int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
    
    TextView headerText = findViewById(R.id.header_text);
    headerText.setText(dayOfWeek + ", " + dayOfMonth + " " + month + "\nHello, let's get started!");
  }

  private String getDayOfWeekName(int dayOfWeek) {
    switch (dayOfWeek) {
      case Calendar.SUNDAY: return "SUNDAY";
      case Calendar.MONDAY: return "MONDAY";
      case Calendar.TUESDAY: return "TUESDAY";
      case Calendar.WEDNESDAY: return "WEDNESDAY";
      case Calendar.THURSDAY: return "THURSDAY";
      case Calendar.FRIDAY: return "FRIDAY";
      case Calendar.SATURDAY: return "SATURDAY";
      default: return "TODAY";
    }
  }
  
  private void setupCollapsibleActivityHeader() {
    View headerContainer = findViewById(R.id.activity_header_container);
    View summaryStatsLayout = findViewById(R.id.summary_stats_layout);
    View activitiesContainer = findViewById(R.id.activities_container);
    TextView expandCollapseArrow = findViewById(R.id.expand_collapse_arrow);
    
    final boolean[] isExpanded = {false};
    
    if (headerContainer != null) {
      headerContainer.setOnClickListener(v -> {
        isExpanded[0] = !isExpanded[0];
        
        if (isExpanded[0]) {
          // Expand: show activities container and summary
          if (activitiesContainer != null) {
            activitiesContainer.setVisibility(View.VISIBLE);
          }
          if (expandCollapseArrow != null) {
            expandCollapseArrow.setText("▲");
          }
          if (summaryStatsLayout != null) {
            summaryStatsLayout.setVisibility(View.VISIBLE);
          }
        } else {
          // Collapse: hide activities container and summary
          if (activitiesContainer != null) {
            activitiesContainer.setVisibility(View.GONE);
          }
          if (expandCollapseArrow != null) {
            expandCollapseArrow.setText("▼");
          }
          if (summaryStatsLayout != null) {
            summaryStatsLayout.setVisibility(View.GONE);
          }
        }
      });
    }
    
    updateSummaryStats();
  }
  
  private void updateSummaryStats() {
    TextView totalActivitiesCount = findViewById(R.id.total_activities_count);
    TextView totalCalories = findViewById(R.id.total_calories);
    TextView totalDuration = findViewById(R.id.total_duration);
    TextView activitySummaryText = findViewById(R.id.activity_summary_text);
    
    if (totalActivitiesCount == null || activityList == null) {
      return;
    }
    
    int activityCount = activityList.size();
    double totalCal = 0;
    int totalMinutes = 0;
    
    for (ActivityItem activity : activityList) {
      totalCal += activity.getCalories();
      String duration = activity.getDuration();
      String durationNum = duration.replaceAll("\\D+", "");
      if (!durationNum.isEmpty()) {
        totalMinutes += Integer.parseInt(durationNum);
      }
    }
    
    totalActivitiesCount.setText(String.valueOf(activityCount));
    totalCalories.setText(String.format("%.0f", totalCal));
    totalDuration.setText(totalMinutes + " min");
    if (activitySummaryText != null) {
      activitySummaryText.setText(activityCount + " activities");
    }
  }
}
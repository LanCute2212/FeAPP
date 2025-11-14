package com.example.caloriesapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Typeface;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ViewFlipper;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import com.example.caloriesapp.adapter.ActivityAdapter;
import com.example.caloriesapp.apiclient.ApiClient;
import com.example.caloriesapp.apiclient.UserClient;
import com.example.caloriesapp.apiclient.DietClient;
import com.example.caloriesapp.dto.response.BaseResponse;
import com.example.caloriesapp.dto.response.PhysicalProfileForm;
import com.example.caloriesapp.dto.response.DietResponse;
import com.example.caloriesapp.model.ActivityItem;
import com.example.caloriesapp.session.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomePageActivity extends AppCompatActivity {
  private static final int ADD_ACTIVITY_REQUEST_CODE = 1001;
  private static final int LIST_ACTIVITY_REQUEST_CODE = 1002;
  private static final String PREFS_NAME = "PhysicalProfile";
  private static final String KEY_TARGET_WEIGHT = "target_weight";
  private static final String KEY_ADJUSTMENT_LEVEL = "adjustment_level";
  private static final int DAYS_IN_WEEK = 7;
  private static final int MAX_PROGRESS_PERCENT = 100;
  private static final int MIN_PROGRESS_PERCENT = 0;
  private static final boolean USE_HARD_CODED_WEEK_DATA = true;
  private static final double HARD_CODED_WEEKLY_TARGET = 14000.0;
  private static final double HARD_CODED_WEEKLY_CONSUMED = 5000.0;
  
  private String email;
  private ActivityAdapter activityAdapter;
  private List<ActivityItem> activityList;
  private SessionManager sessionManager;
  private TextView tvIntake, tvRemaining, tvConsumed, tvDaNap;
  private TextView tvCarbsProgress, tvProteinProgress, tvFatProgress, tvFiberProgress;
  private ProgressBar progressCarbs, progressProtein, progressFat, progressFiber;
  private ImageView appleImg;
  private CircularProgressIndicator weekCalorieProgress;
  private TextView weekCaloriesConsumed, weekCaloriesTarget;
  
  private double tdee = 0;
  private double targetWeight = -1;
  private int selectedAdjustmentLevel = 500;
  private boolean isWeightLoss = true;
  private int selectedCarbPercent = 50;
  private int selectedProteinPercent = 23;
  private int selectedFatPercent = 27;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_topbar);

    email = getIntent().getStringExtra("email");
    sessionManager = new SessionManager(this);
    if (email == null) {
      email = sessionManager.getEmail();
    }
    
    SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
    targetWeight = prefs.getFloat(KEY_TARGET_WEIGHT, -1);
    selectedAdjustmentLevel = prefs.getInt(KEY_ADJUSTMENT_LEVEL, 500);
    
    tvIntake = findViewById(R.id.tv_intake);
    tvRemaining = findViewById(R.id.tv_remaining);
    tvConsumed = findViewById(R.id.tv_consumed);
    tvDaNap = findViewById(R.id.da_nap);
    appleImg = findViewById(R.id.apple_img);
    
    progressCarbs = findViewById(R.id.progress_carbs);
    progressProtein = findViewById(R.id.progress_protein);
    progressFat = findViewById(R.id.progress_fat);
    progressFiber = findViewById(R.id.progress_fiber);
    
    tvCarbsProgress = findViewById(R.id.tv_carbs_progress);
    tvProteinProgress = findViewById(R.id.tv_protein_progress);
    tvFatProgress = findViewById(R.id.tv_fat_progress);
    tvFiberProgress = findViewById(R.id.tv_fiber_progress);
    
    weekCalorieProgress = findViewById(R.id.week_calorie_progress);
    weekCaloriesConsumed = findViewById(R.id.week_calories_consumed);
    weekCaloriesTarget = findViewById(R.id.week_calories_target);

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
    
    View dietModeContainer = findViewById(R.id.diet_mode_container);
    if (dietModeContainer != null) {
      dietModeContainer.setOnClickListener(v -> showDietModeBottomSheet());
    }
    
    TextView tvDietMode = findViewById(R.id.tv_diet_mode);
    if (tvDietMode != null) {
      tvDietMode.setPaintFlags(tvDietMode.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }
    
    loadUserPhysicalProfile();
  
  TextView btnDay = findViewById(R.id.btn_theo_ngay);
  TextView btnWeek = findViewById(R.id.btn_theo_tuan);
  ViewFlipper flipper = findViewById(R.id.nutrition_flipper);
  if (flipper != null && btnDay != null && btnWeek != null) {
    flipper.setInAnimation(this, android.R.anim.slide_in_left);
    flipper.setOutAnimation(this, android.R.anim.slide_out_right);

    View.OnClickListener dayListener = v -> {
      if (flipper.getDisplayedChild() != 0) {
        flipper.setDisplayedChild(0);
      }
      styleToggleSelected(btnDay, true);
      styleToggleSelected(btnWeek, false);
    };

    View.OnClickListener weekListener = v -> {
      if (flipper.getDisplayedChild() != 1) {
        flipper.setDisplayedChild(1);
      }
      styleToggleSelected(btnDay, false);
      styleToggleSelected(btnWeek, true);
    };

    btnDay.setOnClickListener(dayListener);
    btnWeek.setOnClickListener(weekListener);
    dayListener.onClick(btnDay);
  }
  
  if (USE_HARD_CODED_WEEK_DATA) {
    updateWeeklyCaloriesCircle(0, 0);
  }
  }
  
  private double roundToTwoDecimals(double value) {
    return Math.round(value * 100.0) / 100.0;
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
    
    updateSummaryStats();
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
          if (activitiesContainer != null) {
            activitiesContainer.setVisibility(View.VISIBLE);
          }
          if (expandCollapseArrow != null) {
            expandCollapseArrow.setText("▲");
          }
          if (summaryStatsLayout != null) {
            summaryStatsLayout.setVisibility(View.VISIBLE);
          }
          updateSummaryStats();
        } else {
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
  }
  
  private void updateSummaryStats() {
    TextView totalActivitiesCount = findViewById(R.id.total_activities_count);
    TextView totalCalories = findViewById(R.id.total_calories);
    TextView totalDuration = findViewById(R.id.total_duration);
    TextView activitySummaryText = findViewById(R.id.activity_summary_text);
    TextView tvEmptyActivity = findViewById(R.id.tv_empty_activity);
    RecyclerView recyclerView = findViewById(R.id.activities_recycler_view);
    
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
    
    if (tvEmptyActivity != null && recyclerView != null) {
      if (activityCount == 0) {
        tvEmptyActivity.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
      } else {
        tvEmptyActivity.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
      }
    }
    
    updateCalorieStats();
  }
  
  private void loadUserPhysicalProfile() {
    if (email == null || email.isEmpty()) {
      return;
    }
    
    UserClient userClient = ApiClient.getClient().create(UserClient.class);
    Call<BaseResponse<PhysicalProfileForm>> call = userClient.getInfo(email);
    
    call.enqueue(new Callback<BaseResponse<PhysicalProfileForm>>() {
      @Override
      public void onResponse(Call<BaseResponse<PhysicalProfileForm>> call, Response<BaseResponse<PhysicalProfileForm>> response) {
        if (response.isSuccessful() && response.body() != null && !response.body().isError()) {
          PhysicalProfileForm user = response.body().getData();
          tdee = user.getTdee();
          
          if (targetWeight != -1 && user.getWeight() > 0) {
            isWeightLoss = targetWeight < user.getWeight();
          }
          
          updateCalorieStats();
        }
      }
      
      @Override
      public void onFailure(Call<BaseResponse<PhysicalProfileForm>> call, Throwable t) {
      }
    });
  }
  
  private void updateCalorieStats() {
    if (tdee == 0) {
      return;
    }
    
    double intake;
    if (targetWeight == -1) {
      intake = tdee;
    } else {
      if (isWeightLoss) {
        intake = tdee - selectedAdjustmentLevel;
      } else {
        intake = tdee + selectedAdjustmentLevel;
      }
    }
    
    double consumedCalories = 1200;
    
    double burnedCalories = 0;
    if (activityList != null) {
      for (ActivityItem activity : activityList) {
        burnedCalories += activity.getCalories();
      }
    }
    
    double remaining = intake - consumedCalories;
    
    if (tvIntake != null) {
      tvIntake.setText(String.valueOf((int)intake));
    }
    if (tvRemaining != null) {
      tvRemaining.setText(String.valueOf((int)remaining));
    }
    if (tvConsumed != null) {
      tvConsumed.setText(String.valueOf((int)burnedCalories));
    }
    
    if (tvDaNap != null) {
      tvDaNap.setText("Intake\n" + (int)intake);
    }
    
    if (appleImg != null && intake > 0) {
      double completionPercent = consumedCalories / intake;
      completionPercent = Math.max(0.0, Math.min(1.0, completionPercent));
      appleImg.setAlpha((float)completionPercent);
    } else if (appleImg != null) {
      appleImg.setAlpha(0.2f);
    }
    
    updateNutritionProgress();
    
    updateWeeklyCaloriesCircle(intake, consumedCalories);
  }
  
  private void updateWeeklyCaloriesCircle(double dailyIntake, double dailyConsumed) {
    if (USE_HARD_CODED_WEEK_DATA) {
      updateWeeklyCaloriesTextViews(HARD_CODED_WEEKLY_CONSUMED, HARD_CODED_WEEKLY_TARGET);
      updateWeeklyCaloriesProgress(HARD_CODED_WEEKLY_CONSUMED, HARD_CODED_WEEKLY_TARGET);
      return;
    }
    if (tdee == 0) {
      return;
    }
    double weeklyTarget = dailyIntake * DAYS_IN_WEEK;
    double weeklyConsumed = dailyConsumed * DAYS_IN_WEEK;
    updateWeeklyCaloriesTextViews(weeklyConsumed, weeklyTarget);
    updateWeeklyCaloriesProgress(weeklyConsumed, weeklyTarget);
  }
  private void updateWeeklyCaloriesTextViews(double weeklyConsumed, double weeklyTarget) {
    if (weekCaloriesConsumed != null) {
      weekCaloriesConsumed.setText(String.valueOf((int)weeklyConsumed));
    }
    if (weekCaloriesTarget != null) {
      weekCaloriesTarget.setText(String.valueOf((int)weeklyTarget));
    }
  }
  private void updateWeeklyCaloriesProgress(double weeklyConsumed, double weeklyTarget) {
    if (weekCalorieProgress == null) {
      return;
    }
    if (weeklyTarget > 0) {
      int progressPercent = calculateProgressPercent(weeklyConsumed, weeklyTarget);
      weekCalorieProgress.setProgress(progressPercent);
    } else {
      weekCalorieProgress.setProgress(MIN_PROGRESS_PERCENT);
    }
  }
  private int calculateProgressPercent(double consumed, double target) {
    int progressPercent = (int)((consumed / target) * MAX_PROGRESS_PERCENT);
    return Math.min(MAX_PROGRESS_PERCENT, Math.max(MIN_PROGRESS_PERCENT, progressPercent));
  }
  
  private void updateNutritionProgress() {
    double carbsConsumed = 0;
    double proteinConsumed = 0;
    double fatConsumed = 0;
    double fiberConsumed = 0;
    
    double carbsTarget = roundToTwoDecimals(((tdee * selectedCarbPercent) / 100.0) / 4.0);
    double proteinTarget = roundToTwoDecimals(((tdee * selectedProteinPercent) / 100.0) / 4.0);
    double fatTarget = roundToTwoDecimals(((tdee * selectedFatPercent) / 100.0) / 9.0);
    double fiberTarget = 49;
    
    if (tvCarbsProgress != null) {
      tvCarbsProgress.setText(String.format("%.0f/%.0fg", carbsConsumed, carbsTarget));
    }
    if (tvProteinProgress != null) {
      tvProteinProgress.setText(String.format("%.0f/%.0fg", proteinConsumed, proteinTarget));
    }
    if (tvFatProgress != null) {
      tvFatProgress.setText(String.format("%.0f/%.0fg", fatConsumed, fatTarget));
    }
    if (tvFiberProgress != null) {
      tvFiberProgress.setText(String.format("%.0f/%.0fg", fiberConsumed, fiberTarget));
    }
    
    if (progressCarbs != null && carbsTarget > 0) {
      int carbsPercent = (int)((carbsConsumed / carbsTarget) * 100);
      progressCarbs.setProgress(Math.min(100, Math.max(0, carbsPercent)));
    }
    
    if (progressProtein != null && proteinTarget > 0) {
      int proteinPercent = (int)((proteinConsumed / proteinTarget) * 100);
      progressProtein.setProgress(Math.min(100, Math.max(0, proteinPercent)));
    }
    
    if (progressFat != null && fatTarget > 0) {
      int fatPercent = (int)((fatConsumed / fatTarget) * 100);
      progressFat.setProgress(Math.min(100, Math.max(0, fatPercent)));
    }
    
    if (progressFiber != null && fiberTarget > 0) {
      int fiberPercent = (int)((fiberConsumed / fiberTarget) * 100);
      progressFiber.setProgress(Math.min(100, Math.max(0, fiberPercent)));
    }
  }
  
  private void showDietModeBottomSheet() {
    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
    View bottomSheetView = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_diet_mode, null);
    bottomSheetDialog.setContentView(bottomSheetView);
    
    ImageView btnClose = bottomSheetView.findViewById(R.id.btn_close);
    btnClose.setOnClickListener(v -> bottomSheetDialog.dismiss());
    
    NumberPicker npCarbs = bottomSheetView.findViewById(R.id.np_carbs);
    NumberPicker npProtein = bottomSheetView.findViewById(R.id.np_protein);
    NumberPicker npFat = bottomSheetView.findViewById(R.id.np_fat);
    
    npCarbs.setMinValue(0);
    npCarbs.setMaxValue(100);
    npCarbs.setValue(50);
    
    npProtein.setMinValue(0);
    npProtein.setMaxValue(100);
    npProtein.setValue(23);
    
    npFat.setMinValue(0);
    npFat.setMaxValue(100);
    npFat.setValue(27);
    
    styleNumberPicker(npCarbs, 0xFF4CAF50, 22f);
    styleNumberPicker(npProtein, 0xFF4CAF50, 22f);
    styleNumberPicker(npFat, 0xFF4CAF50, 22f);
    
    TextView tvTotalPercentage = bottomSheetView.findViewById(R.id.tv_total_percentage);
    CardView cardTotalPercentage = bottomSheetView.findViewById(R.id.card_total_percentage);
    updateTotalPercentage(tvTotalPercentage, cardTotalPercentage, npCarbs.getValue(), npProtein.getValue(), npFat.getValue());
    
    final boolean[] isUpdatingFromDiet = {false};
    final DietModeAdapter[] adapterRef = new DietModeAdapter[1];
    NumberPicker.OnValueChangeListener valueChangeListener = (picker, oldVal, newVal) -> {
      updateTotalPercentage(tvTotalPercentage, cardTotalPercentage, npCarbs.getValue(), npProtein.getValue(), npFat.getValue());
      if (!isUpdatingFromDiet[0] && adapterRef[0] != null) {
        adapterRef[0].selectCustom();
      }
    };
    
    npCarbs.setOnValueChangedListener(valueChangeListener);
    npProtein.setOnValueChangedListener(valueChangeListener);
    npFat.setOnValueChangedListener(valueChangeListener);
    
    RecyclerView rvDietModes = bottomSheetView.findViewById(R.id.rv_diet_modes);
    GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
    gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
      @Override
      public int getSpanSize(int position) {
        return 1;
      }
    });
    rvDietModes.setLayoutManager(gridLayoutManager);
    int spacing = (int) (8 * getResources().getDisplayMetrics().density);
    rvDietModes.addItemDecoration(new GridSpacingItemDecoration(3, spacing, true));
    DietModeAdapter dietModeAdapter = new DietModeAdapter(new java.util.ArrayList<>(), -1, selected -> {
      if (selected != null) {
        isUpdatingFromDiet[0] = true;
        npCarbs.setValue(selected.getCarbPercent());
        npProtein.setValue(selected.getProteinPercent());
        npFat.setValue(selected.getFatPercent());
        updateTotalPercentage(tvTotalPercentage, cardTotalPercentage, npCarbs.getValue(), npProtein.getValue(), npFat.getValue());
        isUpdatingFromDiet[0] = false;
      }
    });
    adapterRef[0] = dietModeAdapter;
    rvDietModes.setAdapter(dietModeAdapter);

    DietClient dietClient = ApiClient.getClient().create(DietClient.class);
    dietClient.getDiets().enqueue(new Callback<BaseResponse<java.util.List<DietResponse>>>() {
      @Override
      public void onResponse(Call<BaseResponse<java.util.List<DietResponse>>> call, Response<BaseResponse<java.util.List<DietResponse>>> response) {
        if (response.isSuccessful() && response.body() != null && !response.body().isError()) {
          java.util.List<DietResponse> diets = response.body().getData();
          if (diets == null) {
            diets = new java.util.ArrayList<>();
          }
          DietResponse custom = new DietResponse("Tùy chỉnh", npCarbs.getValue(), npFat.getValue(), npProtein.getValue());
          java.util.List<DietResponse> withCustom = new java.util.ArrayList<>();
          withCustom.add(custom);
          withCustom.addAll(diets);
          dietModeAdapter.setItems(withCustom);
        }
      }

      @Override
      public void onFailure(Call<BaseResponse<java.util.List<DietResponse>>> call, Throwable t) {
      }
    });
    
    Button btnSave = bottomSheetView.findViewById(R.id.btn_save_diet_mode);
    btnSave.setOnClickListener(v -> {
      int selectedIndex = dietModeAdapter.getSelectedPosition();
      String nameToSave = "Tùy chỉnh";
      if (selectedIndex >= 0) {
        DietResponse selectedDiet = dietModeAdapter.getItem(selectedIndex);
        if (selectedDiet != null &&
            selectedDiet.getCarbPercent() == npCarbs.getValue() &&
            selectedDiet.getProteinPercent() == npProtein.getValue() &&
            selectedDiet.getFatPercent() == npFat.getValue()) {
          nameToSave = selectedDiet.getName();
        }
      }
      TextView tvDietMode = findViewById(R.id.tv_diet_mode);
      if (tvDietMode != null) {
        tvDietMode.setText(nameToSave);
      }
      selectedCarbPercent = npCarbs.getValue();
      selectedProteinPercent = npProtein.getValue();
      selectedFatPercent = npFat.getValue();
      updateNutritionProgress();
      Toast.makeText(this, "Đã lưu chế độ ăn: " + nameToSave, Toast.LENGTH_SHORT).show();
      bottomSheetDialog.dismiss();
    });
    
    bottomSheetDialog.show();
  }
  
  private void updateTotalPercentage(TextView tvTotal, CardView cardView, int carbs, int protein, int fat) {
    int total = carbs + protein + fat;
    tvTotal.setText(total + "%");
    if (total == 100) {
      tvTotal.setTextColor(0xFF4CAF50);
      if (cardView != null) {
        cardView.setCardBackgroundColor(0xFFE8F5E9);
      }
    } else {
      tvTotal.setTextColor(0xFFF44336);
      if (cardView != null) {
        cardView.setCardBackgroundColor(0xFFFFEBEE);
      }
    }
  }
  
  private void styleToggleSelected(TextView textView, boolean selected) {
    if (textView == null) return;
    textView.setTypeface(null, selected ? Typeface.BOLD : Typeface.NORMAL);
    textView.setTextColor(selected ? Color.parseColor("#111111") : Color.parseColor("#9E9E9E"));
    if (selected) {
      textView.setBackgroundResource(R.drawable.btn_outline_green);
    } else {
      textView.setBackground(null);
    }
    int hPad = (int)(12 * getResources().getDisplayMetrics().density);
    int vPad = (int)(6 * getResources().getDisplayMetrics().density);
    textView.setPadding(hPad, vPad, hPad, vPad);
  }

  private void styleNumberPicker(NumberPicker numberPicker, int centerColor, float textSizeSp) {
    try {
      @SuppressLint("SoonBlockedPrivateApi") java.lang.reflect.Field selectorWheelPaintField = NumberPicker.class.getDeclaredField("mSelectorWheelPaint");
      selectorWheelPaintField.setAccessible(true);
      Paint selectorWheelPaint = (Paint) selectorWheelPaintField.get(numberPicker);
      selectorWheelPaint.setColor(centerColor);
      selectorWheelPaint.setTextSize(textSizeSp * getResources().getDisplayMetrics().scaledDensity);
      selectorWheelPaint.setFakeBoldText(false);

      java.lang.reflect.Field inputTextField = NumberPicker.class.getDeclaredField("mInputText");
      inputTextField.setAccessible(true);
      TextView inputText = (TextView) inputTextField.get(numberPicker);
      inputText.setTextColor(centerColor);
      inputText.setTextSize(textSizeSp);
      inputText.setTypeface(Typeface.DEFAULT_BOLD);

      numberPicker.invalidate();
    } catch (Exception ignored) {
    }

    int childCount = numberPicker.getChildCount();
    for (int i = 0; i < childCount; i++) {
      View child = numberPicker.getChildAt(i);
      if (child instanceof TextView) {
        ((TextView) child).setTextColor(centerColor);
        ((TextView) child).setTextSize(textSizeSp);
      }
    }
  }
  
  @Override
  protected void onResume() {
    super.onResume();
    
    SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
    targetWeight = prefs.getFloat(KEY_TARGET_WEIGHT, -1);
    selectedAdjustmentLevel = prefs.getInt(KEY_ADJUSTMENT_LEVEL, 500);
    
    if (USE_HARD_CODED_WEEK_DATA) {
      updateWeeklyCaloriesCircle(0, 0);
    }
    
    updateCalorieStats();
  }
  
  private static class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
    private int spanCount;
    private int spacing;
    private boolean includeEdge;

    GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
      this.spanCount = spanCount;
      this.spacing = spacing;
      this.includeEdge = includeEdge;
    }

    @Override
    public void getItemOffsets(@NonNull android.graphics.Rect outRect, @NonNull View view,
        @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
      int position = parent.getChildAdapterPosition(view);
      int column = position % spanCount;

      if (includeEdge) {
        outRect.left = spacing - column * spacing / spanCount;
        outRect.right = (column + 1) * spacing / spanCount;

        if (position < spanCount) {
          outRect.top = spacing;
        }
        outRect.bottom = spacing;
      } else {
        outRect.left = column * spacing / spanCount;
        outRect.right = spacing - (column + 1) * spacing / spanCount;
        if (position >= spanCount) {
          outRect.top = spacing;
        }
      }
    }
  }
  
  private static class DietModeAdapter extends RecyclerView.Adapter<DietModeAdapter.DietModeViewHolder> {
    interface OnDietSelectedListener {
      void onDietSelected(DietResponse selectedDiet);
    }
    private java.util.List<DietResponse> dietModes;
    private int selectedPosition;
    private OnDietSelectedListener onDietSelectedListener;
    
    DietModeAdapter(java.util.List<DietResponse> dietModes, int selectedPosition, OnDietSelectedListener listener) {
      this.dietModes = dietModes;
      this.selectedPosition = selectedPosition;
      this.onDietSelectedListener = listener;
    }
    
    @NonNull
    @Override
    public DietModeViewHolder onCreateViewHolder(@NonNull android.view.ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_diet_mode_button, parent, false);
      return new DietModeViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull DietModeViewHolder holder, int position) {
      holder.button.setText(dietModes.get(position).getName());
      boolean isSelected = position == selectedPosition;
      
      if (isSelected) {
        holder.button.setBackgroundResource(R.drawable.btn_diet_mode_selected);
        holder.button.setTextColor(android.graphics.Color.parseColor("#4CAF50"));
        holder.button.setElevation(4f);
      } else {
        holder.button.setBackgroundResource(R.drawable.btn_diet_mode);
        holder.button.setTextColor(android.graphics.Color.parseColor("#424242"));
        holder.button.setElevation(0f);
      }
      
      holder.button.setOnClickListener(v -> {
        int oldPosition = selectedPosition;
        selectedPosition = position;
        notifyItemChanged(oldPosition);
        notifyItemChanged(selectedPosition);
        if (onDietSelectedListener != null) {
          onDietSelectedListener.onDietSelected(dietModes.get(position));
        }
      });
    }
    
    @Override
    public int getItemCount() {
      return dietModes.size();
    }
    
    int getSelectedPosition() {
      return selectedPosition;
    }
    
    DietResponse getItem(int position) {
      if (position < 0 || position >= dietModes.size()) {
        return null;
      }
      return dietModes.get(position);
    }
    
    void setItems(java.util.List<DietResponse> newItems) {
      dietModes.clear();
      dietModes.addAll(newItems);
      notifyDataSetChanged();
    }
    
    void selectCustom() {
      if (dietModes.isEmpty()) {
        return;
      }
      int old = selectedPosition;
      selectedPosition = 0;
      if (old != -1) {
        notifyItemChanged(old);
      }
      notifyItemChanged(selectedPosition);
    }
    
    static class DietModeViewHolder extends RecyclerView.ViewHolder {
      Button button;
      
      DietModeViewHolder(@NonNull View itemView) {
        super(itemView);
        button = itemView.findViewById(R.id.btn_diet_mode);
      }
    }
  }
}
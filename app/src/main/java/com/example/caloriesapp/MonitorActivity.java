package com.example.caloriesapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.widget.GridLayout;

import com.example.caloriesapp.apiclient.ApiClient;
import com.example.caloriesapp.apiclient.UserClient;
import com.example.caloriesapp.dto.response.BaseResponse;
import com.example.caloriesapp.dto.response.PhysicalProfileForm;
import com.example.caloriesapp.model.MealDetail;
import com.example.caloriesapp.model.DailyNutrition;
import com.example.caloriesapp.session.SessionManager;
import com.example.caloriesapp.util.MealDataManager;
import com.example.caloriesapp.database.AppDatabase;
import com.example.caloriesapp.repository.DailyNutritionRepository;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MonitorActivity extends AppCompatActivity {
    private Calendar currentMonth;
    private TextView tvMonthTitle;
    private TextView tvStatisticsTitle;
    private GridLayout calendarGrid;
    private TextView tvDaysRecommended;
    private TextView tvDaysBelowBMR;
    private TextView tvDaysOverIntake;
    private TextView tvTotalCaloriesNeeded;
    private TextView tvTotalCaloriesConsumed;
    private TextView tvCaloriesToIncrease;
    private TextView tvCaloriesIncreased;

    private int selectedDay = -1;
    private SimpleDateFormat monthYearFormat;
    private SimpleDateFormat monthYearFormatForStats;
    private double bmr = 0;
    private SessionManager sessionManager;
    private Map<Integer, Boolean> daysBelowBMR = new HashMap<>(); // Day number -> is below BMR
    private DailyNutritionRepository nutritionRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring);

        currentMonth = Calendar.getInstance();
        // Escape literal text with single quotes in SimpleDateFormat
        monthYearFormat = new SimpleDateFormat("'Tháng' M yyyy", new Locale("vi", "VN"));
        monthYearFormatForStats = new SimpleDateFormat("'tháng' M/yyyy", new Locale("vi", "VN"));

        sessionManager = new SessionManager(this);
        
        // Initialize Room database
        AppDatabase database = AppDatabase.getDatabase(this);
        nutritionRepository = new DailyNutritionRepository(database);
        
        initializeViews();
        setupClickListeners();
        loadUserPhysicalProfile();
    }

    private void initializeViews() {
        tvMonthTitle = findViewById(R.id.tv_month_title);
        tvStatisticsTitle = findViewById(R.id.tv_statistics_title);
        calendarGrid = findViewById(R.id.calendar_grid);
        tvDaysRecommended = findViewById(R.id.tv_days_recommended);
        tvDaysBelowBMR = findViewById(R.id.tv_days_below_bmr);
        tvDaysOverIntake = findViewById(R.id.tv_days_over_intake);
        tvTotalCaloriesNeeded = findViewById(R.id.tv_total_calories_needed);
        tvTotalCaloriesConsumed = findViewById(R.id.tv_total_calories_consumed);
        tvCaloriesToIncrease = findViewById(R.id.tv_calories_to_increase);
        tvCaloriesIncreased = findViewById(R.id.tv_calories_increased);
    }

    private void setupClickListeners() {
        // Back button
        findViewById(R.id.btn_back).setOnClickListener(v -> {
            finish();
        });

        // Toolbar back
        findViewById(R.id.toolbar).setOnClickListener(v -> {
            finish();
        });

        // Previous month
        findViewById(R.id.btn_prev_month).setOnClickListener(v -> {
            currentMonth.add(Calendar.MONTH, -1);
            checkDaysBelowBMR();
            updateCalendar();
            updateStatistics();
        });

        // Next month
        findViewById(R.id.btn_next_month).setOnClickListener(v -> {
            currentMonth.add(Calendar.MONTH, 1);
            checkDaysBelowBMR();
            updateCalendar();
            updateStatistics();
        });
    }

    private void updateCalendar() {
        // Update month title
        tvMonthTitle.setText(monthYearFormat.format(currentMonth.getTime()));

        // Clear existing calendar views
        calendarGrid.removeAllViews();

        // Get first day of month and number of days
        Calendar calendar = (Calendar) currentMonth.clone();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Convert Sunday (1) to Monday (0) for Vietnamese calendar
        int startOffset = (firstDayOfWeek == Calendar.SUNDAY) ? 6 : firstDayOfWeek - Calendar.MONDAY;

        // Add empty cells for days before the first day of month
        for (int i = 0; i < startOffset; i++) {
            TextView emptyView = createEmptyDayView();
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(i, 1f);
            params.rowSpec = GridLayout.spec(0);
            emptyView.setLayoutParams(params);
            calendarGrid.addView(emptyView);
        }

        // Get today's date for comparison
        Calendar today = Calendar.getInstance();
        boolean isCurrentMonth = currentMonth.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                currentMonth.get(Calendar.YEAR) == today.get(Calendar.YEAR);

        // Add day cells
        int row = 0;
        int col = startOffset;
        for (int day = 1; day <= daysInMonth; day++) {
            // Check if this is today
            boolean isToday = isCurrentMonth && day == today.get(Calendar.DAY_OF_MONTH);
            // Check if this day is below BMR
            boolean isBelowBMR = daysBelowBMR.containsKey(day) && daysBelowBMR.get(day);
            TextView dayView = createDayView(day, isToday, isBelowBMR);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(col, 1f);
            params.rowSpec = GridLayout.spec(row);
            params.setMargins(4, 4, 4, 4);
            dayView.setLayoutParams(params);

            // Set click listener
            final int dayNumber = day;
            dayView.setOnClickListener(v -> {
                selectedDay = dayNumber;
                // Navigate to HomePageActivity with selected date
                Calendar selectedDate = (Calendar) currentMonth.clone();
                selectedDate.set(Calendar.DAY_OF_MONTH, dayNumber);
                String dateStr = formatDateForMealData(selectedDate);
                
                Intent intent = new Intent(MonitorActivity.this, HomePageActivity.class);
                intent.putExtra("selected_date", dateStr);
                startActivity(intent);
                finish();
            });

            calendarGrid.addView(dayView);

            col++;
            if (col >= 7) {
                col = 0;
                row++;
            }
        }
    }

    private TextView createDayView(int day, boolean isToday, boolean isBelowBMR) {
        TextView dayView = new TextView(this);
        dayView.setText(String.valueOf(day));
        dayView.setTextSize(14);
        dayView.setGravity(Gravity.CENTER);
        dayView.setPadding(8, 8, 8, 8);
        dayView.setMinHeight((int) (48 * getResources().getDisplayMetrics().density));
        dayView.setMinWidth((int) (48 * getResources().getDisplayMetrics().density));
        
        // Priority: Today > Below BMR > Normal
        if (isToday) {
            dayView.setBackgroundResource(R.drawable.day_background);
            dayView.setTextColor(Color.WHITE);
        } else if (isBelowBMR) {
            dayView.setBackgroundResource(R.drawable.calendar_day_orange);
            dayView.setTextColor(Color.WHITE);
        } else {
            dayView.setTextColor(Color.parseColor("#111111"));
        }
        
        return dayView;
    }

    private TextView createEmptyDayView() {
        TextView emptyView = new TextView(this);
        emptyView.setText("");
        emptyView.setMinHeight((int) (48 * getResources().getDisplayMetrics().density));
        emptyView.setMinWidth((int) (48 * getResources().getDisplayMetrics().density));
        return emptyView;
    }

    private void loadUserPhysicalProfile() {
        String email = sessionManager.getEmail();
        if (email == null || email.isEmpty()) {
            updateCalendar();
            updateStatistics();
            return;
        }
        
        UserClient userClient = ApiClient.getClient().create(UserClient.class);
        Call<BaseResponse<PhysicalProfileForm>> call = userClient.getInfo(email);
        
        call.enqueue(new Callback<BaseResponse<PhysicalProfileForm>>() {
            @Override
            public void onResponse(Call<BaseResponse<PhysicalProfileForm>> call, Response<BaseResponse<PhysicalProfileForm>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isError()) {
                    PhysicalProfileForm user = response.body().getData();
                    bmr = user.getBmr() != null ? user.getBmr() : 0;
                    checkDaysBelowBMR();
                    updateCalendar();
                    updateStatistics();
                } else {
                    updateCalendar();
                    updateStatistics();
                }
            }
            
            @Override
            public void onFailure(Call<BaseResponse<PhysicalProfileForm>> call, Throwable t) {
                updateCalendar();
                updateStatistics();
            }
        });
    }
    
    private void checkDaysBelowBMR() {
        daysBelowBMR.clear();
        
        if (bmr <= 0) {
            updateCalendar();
            updateStatistics();
            return;
        }
        
        Calendar calendar = (Calendar) currentMonth.clone();
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        
        // Get start and end dates for the month
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        String startDate = formatDateForMealData(calendar);
        calendar.set(Calendar.DAY_OF_MONTH, daysInMonth);
        String endDate = formatDateForMealData(calendar);
        
        // Load data from Room database for the month
        nutritionRepository.getByDateRange(startDate, endDate, new DailyNutritionRepository.OnDataLoadedListener<List<DailyNutrition>>() {
            @Override
            public void onDataLoaded(List<DailyNutrition> nutritionList) {
                // Process Room data
                for (DailyNutrition nutrition : nutritionList) {
                    try {
                        String[] parts = nutrition.getDate().split("-");
                        int day = Integer.parseInt(parts[2]);
                        double consumed = nutrition.getConsumed();
                        
                        // Mark day as below BMR if consumed calories are less than BMR
                        if (consumed > 0 && consumed < bmr) {
                            daysBelowBMR.put(day, true);
                        }
                    } catch (Exception e) {
                        // Ignore
                    }
                }
                
                // Also check MealDataManager for current day data that might not be saved yet
                Calendar cal = (Calendar) currentMonth.clone();
                for (int day = 1; day <= daysInMonth; day++) {
                    cal.set(Calendar.DAY_OF_MONTH, day);
                    String dateStr = formatDateForMealData(cal);
                    
                    // Check if already processed from Room
                    boolean foundInRoom = false;
                    for (DailyNutrition n : nutritionList) {
                        if (n.getDate().equals(dateStr)) {
                            foundInRoom = true;
                            break;
                        }
                    }
                    
                    if (!foundInRoom) {
                        List<MealDetail> meals = MealDataManager.getInstance().getMealDetailsForDate(dateStr);
                        double totalCalories = 0;
                        
                        for (MealDetail meal : meals) {
                            totalCalories += meal.getCalories();
                        }
                        
                        // Mark day as below BMR if consumed calories are less than BMR
                        if (totalCalories > 0 && totalCalories < bmr) {
                            daysBelowBMR.put(day, true);
                        }
                    }
                }
                
                // Update UI on main thread
                runOnUiThread(() -> {
                    updateCalendar();
                    updateStatistics();
                });
            }
        });
    }
    
    private String formatDateForMealData(Calendar calendar) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // Month is 0-based
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return String.format("%04d-%02d-%02d", year, month, day);
    }
    
    private void updateStatistics() {
        // Update statistics title with current month
        String statsTitle = "Thống kê lượng Calo trong " + monthYearFormatForStats.format(currentMonth.getTime());
        tvStatisticsTitle.setText(statsTitle);

        // Calculate statistics based on meal data
        int daysBelowBMRCount = daysBelowBMR.size();
        tvDaysBelowBMR.setText(String.format("%02d ngày", daysBelowBMRCount));
        
        // Get start and end dates for the month
        Calendar calendar = (Calendar) currentMonth.clone();
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        String startDate = formatDateForMealData(calendar);
        calendar.set(Calendar.DAY_OF_MONTH, daysInMonth);
        String endDate = formatDateForMealData(calendar);
        
        // Load data from Room database
        nutritionRepository.getByDateRange(startDate, endDate, new DailyNutritionRepository.OnDataLoadedListener<List<DailyNutrition>>() {
            @Override
            public void onDataLoaded(List<DailyNutrition> nutritionList) {
                double totalCaloriesConsumed = 0;
                double totalCaloriesNeeded = 0;
                int daysRecommended = 0;
                int daysOverIntake = 0;
                
                // Calculate from Room data
                for (DailyNutrition nutrition : nutritionList) {
                    totalCaloriesConsumed += nutrition.getConsumed();
                    totalCaloriesNeeded += nutrition.getCalories();
                    
                    // Check if day meets recommendations (consumed is close to target)
                    if (nutrition.getCalories() > 0) {
                        double ratio = nutrition.getConsumed() / nutrition.getCalories();
                        if (ratio >= 0.9 && ratio <= 1.1) { // Within 10% of target
                            daysRecommended++;
                        } else if (nutrition.getConsumed() > nutrition.getCalories() * 1.1) {
                            daysOverIntake++;
                        }
                    }
                }
                
                // Also check MealDataManager for current day data
                Calendar cal = (Calendar) currentMonth.clone();
                for (int day = 1; day <= daysInMonth; day++) {
                    cal.set(Calendar.DAY_OF_MONTH, day);
                    String dateStr = formatDateForMealData(cal);
                    
                    // Check if already in Room database
                    boolean foundInRoom = false;
                    for (DailyNutrition n : nutritionList) {
                        if (n.getDate().equals(dateStr)) {
                            foundInRoom = true;
                            break;
                        }
                    }
                    
                    if (!foundInRoom) {
                        List<MealDetail> meals = MealDataManager.getInstance().getMealDetailsForDate(dateStr);
                        double dayCalories = 0;
                        for (MealDetail meal : meals) {
                            dayCalories += meal.getCalories();
                        }
                        
                        if (dayCalories > 0) {
                            totalCaloriesConsumed += dayCalories;
                            if (bmr > 0) {
                                totalCaloriesNeeded += bmr;
                            }
                        }
                    }
                }
                
                // Make final copies for use in lambda
                final int finalDaysRecommended = daysRecommended;
                final int finalDaysOverIntake = daysOverIntake;
                final double finalTotalCaloriesNeeded = totalCaloriesNeeded;
                final double finalTotalCaloriesConsumed = totalCaloriesConsumed;
                
                // Update UI on main thread
                runOnUiThread(() -> {
                    tvDaysRecommended.setText(String.format("%02d ngày", finalDaysRecommended));
                    tvDaysOverIntake.setText(String.format("%02d ngày", finalDaysOverIntake));
                    
                    // Format numbers with dots as thousand separators
                    tvTotalCaloriesNeeded.setText(formatNumberWithDots((int)finalTotalCaloriesNeeded) + " kcal");
                    tvTotalCaloriesConsumed.setText(formatNumberWithDots((int)finalTotalCaloriesConsumed) + " kcal");
                    
                    // Calculate calories to increase and already increased
                    double caloriesToIncrease = Math.max(0, finalTotalCaloriesNeeded - finalTotalCaloriesConsumed);
                    double caloriesIncreased = finalTotalCaloriesConsumed - finalTotalCaloriesNeeded;
                    
                    tvCaloriesToIncrease.setText(formatNumberWithDots((int)caloriesToIncrease) + " kcal");
                    tvCaloriesIncreased.setText(formatNumberWithDots((int)caloriesIncreased) + " kcal");
                });
            }
        });
    }
    
    private String formatNumberWithDots(int number) {
        // Format number with dots as thousand separators (e.g., 106050 -> 106.050)
        String numStr = String.valueOf(Math.abs(number));
        StringBuilder formatted = new StringBuilder();
        
        for (int i = 0; i < numStr.length(); i++) {
            if (i > 0 && (numStr.length() - i) % 3 == 0) {
                formatted.append(".");
            }
            formatted.append(numStr.charAt(i));
        }
        
        if (number < 0) {
            formatted.insert(0, "-");
        }
        
        return formatted.toString();
    }
}

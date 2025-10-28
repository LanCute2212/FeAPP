package com.example.caloriesapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caloriesapp.adapter.AdjustmentOptionAdapter;
import com.example.caloriesapp.apiclient.ApiClient;
import com.example.caloriesapp.apiclient.UserClient;
import com.example.caloriesapp.dto.response.BaseResponse;
import com.example.caloriesapp.dto.response.PhysicalProfileForm;
import com.example.caloriesapp.dto.response.UpdatePhysicalProfileForm;

import com.example.caloriesapp.session.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class PhysicalProfileActivity extends AppCompatActivity {

    private TextView tvName, tvEmail, tvGender, tvAge, tvHeight, tvWeight,
            tvActivity, tvCalories, tvBMI, tvBMR, tvTDEE, tvTarget, tvAdjustment;

    private double currentWeight;
    private double targetWeight = -1;
    private int selectedAdjustmentLevel = 500;
    private boolean isWeightLoss = true;

    private SessionManager sessionManager;
    private static final String PREFS_NAME = "PhysicalProfile";
    private static final String KEY_TARGET_WEIGHT = "target_weight";
    private static final String KEY_ADJUSTMENT_LEVEL = "adjustment_level";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physicalprofile);


        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvGender = findViewById(R.id.tvGender);
        tvAge = findViewById(R.id.tvAge);
        tvHeight = findViewById(R.id.tvHeight);
        tvWeight = findViewById(R.id.tvWeight);
        tvActivity = findViewById(R.id.tvActivity);
        tvCalories = findViewById(R.id.tvCalories);
        tvBMI = findViewById(R.id.tvBMI);
        tvBMR = findViewById(R.id.tvBMR);
        tvTDEE = findViewById(R.id.tvTDEE);
        tvTarget = findViewById(R.id.tvTarget);
        tvAdjustment = findViewById(R.id.tvAdjustment);

        sessionManager = new SessionManager(this);
        
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        targetWeight = prefs.getFloat(KEY_TARGET_WEIGHT, -1);
        selectedAdjustmentLevel = prefs.getInt(KEY_ADJUSTMENT_LEVEL, 500);
        
        findViewById(R.id.target_container).setOnClickListener(v -> showTargetWeightDialog());
        
        findViewById(R.id.adjustment_container).setOnClickListener(v -> showAdjustmentDialog());

        String email = sessionManager.getEmail();
        if (email == null || email.isEmpty()) {
            Toast.makeText(this, "Không có email người dùng", Toast.LENGTH_SHORT).show();
            return;
        }

        UserClient userClient = ApiClient.getClient().create(UserClient.class);
        Call<BaseResponse<PhysicalProfileForm>> call = userClient.getInfo(email);

        call.enqueue(new Callback<BaseResponse<PhysicalProfileForm>>() {
            @Override
            public void onResponse(Call<BaseResponse<PhysicalProfileForm>> call, Response<BaseResponse<PhysicalProfileForm>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isError()) {
                    PhysicalProfileForm user = response.body().getData();

                    currentWeight = user.getWeight();

                    tvName.setText(user.getName());
                    tvEmail.setText(user.getEmail());
                    tvGender.setText(user.getGender());
                    tvAge.setText(user.getAge().toString());
                    tvHeight.setText(""+user.getHeight());
                    tvWeight.setText("" + user.getWeight());
                    tvActivity.setText("" + user.getActivityLevel());
                    tvBMI.setText("" + user.getBmi());
                    tvBMR.setText("" + user.getBmr());
                    tvTDEE.setText("" + user.getTdee());
                    
                    updateTargetAndAdjustment();
                } else {
                    Toast.makeText(PhysicalProfileActivity.this, "Không thể tải dữ liệu người dùng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<PhysicalProfileForm>> call, Throwable t) {
                Toast.makeText(PhysicalProfileActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.btnRegister).setOnClickListener(v -> {
            startActivity(new android.content.Intent(this, EditPhysicalProfile.class));
        });
    }
    
    private void showTargetWeightDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_target_weight, null);
        
        EditText etTargetWeight = dialogView.findViewById(R.id.et_target_weight);
        TextView tvCurrentWeight = dialogView.findViewById(R.id.tv_current_weight);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnSave = dialogView.findViewById(R.id.btn_save);
        
        if (currentWeight > 0) {
            tvCurrentWeight.setText((int)currentWeight + " kg");
        }
        
        if (targetWeight != -1) {
            etTargetWeight.setText(String.valueOf((int)targetWeight));
        }
        
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        
        btnSave.setOnClickListener(v -> {
            String weightStr = etTargetWeight.getText().toString().trim();
            if (weightStr.isEmpty()) {
                etTargetWeight.setError("Vui lòng nhập cân nặng mục tiêu");
                return;
            }
            
            try {
                double weight = Double.parseDouble(weightStr);
                if (weight <= 0) {
                    etTargetWeight.setError("Cân nặng phải lớn hơn 0");
                    return;
                }
                
                targetWeight = weight;
                saveTargetWeight();
                updateTargetAndAdjustment();
                dialog.dismiss();
                
                Toast.makeText(this, "Đã lưu mục tiêu cân nặng", Toast.LENGTH_SHORT).show();
            } catch (NumberFormatException e) {
                etTargetWeight.setError("Vui lòng nhập số hợp lệ");
            }
        });
        
        dialog.show();
    }
    
    private void updateTargetAndAdjustment() {
        if (targetWeight == -1) {
            tvTarget.setText("");
            tvAdjustment.setText("");
            return;
        }
        
        isWeightLoss = targetWeight < currentWeight;
        
        if (targetWeight < currentWeight) {
            tvTarget.setText("Giảm xuống " + (int)targetWeight + " kg");
        } else if (targetWeight > currentWeight) {
            tvTarget.setText("Tăng lên " + (int)targetWeight + " kg");
        } else {
            tvTarget.setText("Target: " + (int)targetWeight + " kg");
        }
        
        String prefix = isWeightLoss ? "Cắt giảm" : "Tăng thêm";
        tvAdjustment.setText(prefix + " " + selectedAdjustmentLevel + " kcal/ngày");
    }
    
    private void saveTargetWeight() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat(KEY_TARGET_WEIGHT, (float)targetWeight);
        editor.apply();
    }
    
    private void saveAdjustmentLevel() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_ADJUSTMENT_LEVEL, selectedAdjustmentLevel);
        editor.apply();
    }
    
    private void showAdjustmentDialog() {
        if (targetWeight == -1) {
            Toast.makeText(this, "Vui lòng đặt mục tiêu cân nặng trước", Toast.LENGTH_SHORT).show();
            return;
        }
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_adjustment_level, null);
        
        TextView tvDialogTitle = dialogView.findViewById(R.id.tv_dialog_title);
        RecyclerView rvAdjustmentOptions = dialogView.findViewById(R.id.rv_adjustment_options);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnSave = dialogView.findViewById(R.id.btn_save);
        
        if (isWeightLoss) {
            tvDialogTitle.setText("Thay đổi mức độ cắt giảm");
        } else {
            tvDialogTitle.setText("Thay đổi mức độ tăng cân");
        }
        
        List<Integer> options = new ArrayList<>();
        options.add(275);
        options.add(500);
        options.add(1000);
        
        rvAdjustmentOptions.setLayoutManager(new LinearLayoutManager(this));
        AdjustmentOptionAdapter adapter = new AdjustmentOptionAdapter(options, selectedAdjustmentLevel, isWeightLoss);
        rvAdjustmentOptions.setAdapter(adapter);
        
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        
        btnSave.setOnClickListener(v -> {
            Integer selectedOption = adapter.getSelectedOption();
            if (selectedOption != null) {
                selectedAdjustmentLevel = selectedOption;
                saveAdjustmentLevel();
                updateTargetAndAdjustment();
                dialog.dismiss();
                
                Toast.makeText(this, "Đã lưu mức độ điều chỉnh", Toast.LENGTH_SHORT).show();
            }
        });
        
        dialog.show();
    }
}

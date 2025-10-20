package com.example.caloriesapp;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.caloriesapp.apiclient.ApiClient;
import com.example.caloriesapp.apiclient.UserClient;
import com.example.caloriesapp.dto.response.PhysicalProfileForm;
import com.example.caloriesapp.dto.response.UpdatePhysicalProfileForm;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhysicalProfileActivity extends AppCompatActivity {

    private TextView tvName, tvEmail, tvGender, tvAge, tvHeight, tvWeight,
            tvActivity, tvGoal, tvCalories, tvBMI, tvBMR, tvTDEE;

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
        tvGoal = findViewById(R.id.tvGoal);
        tvCalories = findViewById(R.id.tvCalories);
        tvBMI = findViewById(R.id.tvBMI);
        tvBMR = findViewById(R.id.tvBMR);
        tvTDEE = findViewById(R.id.tvTDEE);


        String email = getIntent().getStringExtra("email");
        if (email == null || email.isEmpty()) {
            Toast.makeText(this, "Không có email người dùng", Toast.LENGTH_SHORT).show();
            return;
        }

        UserClient userClient = ApiClient.getClient().create(UserClient.class);
        Call<PhysicalProfileForm> call = userClient.getInfo(email);

        call.enqueue(new Callback<PhysicalProfileForm>() {
            @Override
            public void onResponse(Call<PhysicalProfileForm> call, Response<PhysicalProfileForm> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PhysicalProfileForm user = response.body();

                    tvName.setText(user.getName());
                    tvEmail.setText(user.getEmail());
                    tvGender.setText("Gender: " + user.getGender());
                    tvAge.setText("Age: " + user.getAge());
                    tvHeight.setText("Height: " + user.getHeight());
                    tvWeight.setText("Weight: " + user.getWeight());
                    tvActivity.setText("Activity Level: " + user.getActivityLevel());
                    tvGoal.setText("Goal: " + user.getGoal());
                    tvBMI.setText("BMI: " + user.getBmi());
                    tvBMR.setText("BMR: " + user.getBmr());
                    tvTDEE.setText("TDEE: " + user.getTdee());
                } else {
                    Toast.makeText(PhysicalProfileActivity.this, "Không thể tải dữ liệu người dùng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PhysicalProfileForm> call, Throwable t) {
                Toast.makeText(PhysicalProfileActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Nút chỉnh sửa
        findViewById(R.id.btnRegister).setOnClickListener(v -> {
            startActivity(new android.content.Intent(this, EditPhysicalProfile.class));
        });
    }
}

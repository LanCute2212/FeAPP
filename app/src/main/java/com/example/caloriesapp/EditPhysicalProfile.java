package com.example.caloriesapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.caloriesapp.apiclient.ApiClient;
import com.example.caloriesapp.apiclient.UserClient;
import com.example.caloriesapp.dto.request.PhysicalEditProfileForm;
import com.example.caloriesapp.session.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditPhysicalProfile extends AppCompatActivity {

    private EditText txtAge, txtGender, txtWeight, txtHeight, txtLevelActivity, txtGoal;
    private Button btnSave;

    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editphysicalinformation);

        SessionManager sessionManager = new SessionManager(this);
        email = sessionManager.getEmail();
        if (email == null || email.isEmpty()) {
            Toast.makeText(this, "Không có email người dùng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        txtAge = findViewById(R.id.etAge);
        txtGender = findViewById(R.id.etGender);
        txtWeight = findViewById(R.id.etWeight);
        txtHeight = findViewById(R.id.etHeight);
        txtLevelActivity = findViewById(R.id.etLevelActivity);
        txtGoal = findViewById(R.id.etGoal);
        btnSave = findViewById(R.id.btnSave);

        btnSave.setOnClickListener(v -> {
            try {
                int age = Integer.parseInt(txtAge.getText().toString());
                String gender = txtGender.getText().toString();
                double weight = Double.parseDouble(txtWeight.getText().toString());
                double height = Double.parseDouble(txtHeight.getText().toString());
                double activityLevel = Double.parseDouble(txtLevelActivity.getText().toString().trim());
                double goal = Double.parseDouble(txtGoal.getText().toString().trim());



                PhysicalEditProfileForm form = new PhysicalEditProfileForm(24,
                        age, gender, weight, height, activityLevel, goal
                );


                UserClient userClient = ApiClient.getClient().create(UserClient.class);
                Call<Void> call = userClient.update(email, form);

                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(EditPhysicalProfile.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(EditPhysicalProfile.this, HomePageActivity.class));
                        } else {
                            Toast.makeText(EditPhysicalProfile.this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(EditPhysicalProfile.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                Toast.makeText(EditPhysicalProfile.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

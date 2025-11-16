package com.example.caloriesapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.caloriesapp.apiclient.ApiClient;
import com.example.caloriesapp.apiclient.UserClient;
import com.example.caloriesapp.dto.request.PhysicalEditProfileForm;
import com.example.caloriesapp.dto.response.BaseResponse;
import com.example.caloriesapp.session.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditPhysicalProfile extends AppCompatActivity {

    private EditText txtAge, txtWeight, txtHeight, txtGoal;
    private Spinner spinnerGender, spinnerActivityLevel;
    private Button btnSave;

    private String email;
    private String selectedGender = "Nam";
    private double selectedActivityLevel = 1.2;

    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editphysicalinformation);

        SessionManager sessionManager = new SessionManager(this);
        userId = sessionManager.getUserId();
        email = sessionManager.getEmail();
        if (email == null || email.isEmpty()) {
            Toast.makeText(this, "Không có email người dùng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        txtAge = findViewById(R.id.etAge);
        spinnerGender = findViewById(R.id.spinnerGender);
        txtWeight = findViewById(R.id.etWeight);
        txtHeight = findViewById(R.id.etHeight);
        spinnerActivityLevel = findViewById(R.id.spinnerActivityLevel);
        txtGoal = findViewById(R.id.etGoal);
        btnSave = findViewById(R.id.btnSave);

        // Setup back button
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Setup Gender Spinner with custom adapter
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this,
                R.array.gender_options, R.layout.spinner_item);
        genderAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerGender.setAdapter(genderAdapter);

        // Setup Activity Level Spinner with custom adapter
        ArrayAdapter<CharSequence> activityAdapter = ArrayAdapter.createFromResource(this,
                R.array.activity_level_options, R.layout.spinner_item);
        activityAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerActivityLevel.setAdapter(activityAdapter);

        // Setup Gender Spinner listener
        spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] genderOptions = getResources().getStringArray(R.array.gender_options);
                selectedGender = genderOptions[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Setup Activity Level Spinner
        spinnerActivityLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] activityValues = getResources().getStringArray(R.array.activity_level_values);
                selectedActivityLevel = Double.parseDouble(activityValues[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btnSave.setOnClickListener(v -> {
            try {
                int age = Integer.parseInt(txtAge.getText().toString());
                // Convert Vietnamese gender to English for API
                String gender = selectedGender.equals("Nam") ? "Male" : "Female";
                double weight = Double.parseDouble(txtWeight.getText().toString());
                double height = Double.parseDouble(txtHeight.getText().toString());
                double goal = Double.parseDouble(txtGoal.getText().toString().trim());

                PhysicalEditProfileForm form = new PhysicalEditProfileForm(userId,
                        age, gender, weight, height, selectedActivityLevel, goal
                );

                UserClient userClient = ApiClient.getClient().create(UserClient.class);
                Call<BaseResponse> call = userClient.update(email, form);

                call.enqueue(new Callback<BaseResponse>() {
                    @Override
                    public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(EditPhysicalProfile.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(EditPhysicalProfile.this, HomePageActivity.class));
                        } else {
                            Toast.makeText(EditPhysicalProfile.this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse> call, Throwable t) {
                        Toast.makeText(EditPhysicalProfile.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                Toast.makeText(EditPhysicalProfile.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

package com.example.caloriesapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.caloriesapp.apiclient.ApiClient;
import com.example.caloriesapp.apiclient.UserClient;
import com.example.caloriesapp.dto.request.UserRegisterForm;

import retrofit2.Call;

public class RegisterActivity extends AppCompatActivity {

    EditText etUsername, etEmail, etPassword;
    Button btnRegister;
    TextView tvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);

        btnRegister.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty()) {
                etUsername.setError("Enter your username");
                return;
            } else if (email.isEmpty()) {
                etEmail.setError("Enter your email");
                return;
            } else if (password.isEmpty()) {
                etPassword.setError("Enter your password");
                return;
            }

            UserRegisterForm request = new UserRegisterForm(username, email, password);

            UserClient userClient = ApiClient.getClient().create(UserClient.class);
            Call<String> call = userClient.registerUser(request);

            call.enqueue(new retrofit2.Callback<String>() {
                @Override
                public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this,
                                "Server: " + response.body(), Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this,
                                "Đăng ký thất bại! Mã lỗi: " + response.code(),
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Toast.makeText(RegisterActivity.this,
                            "Lỗi kết nối: " + t.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        });

        // 🔹 Xử lý khi bấm "Login"
        tvLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}

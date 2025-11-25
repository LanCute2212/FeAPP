package com.example.caloriesapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.caloriesapp.apiclient.ApiClient;
import com.example.caloriesapp.apiclient.UserClient;
import com.example.caloriesapp.dto.request.UserLoginForm;
import com.example.caloriesapp.dto.response.BaseResponse;
import com.example.caloriesapp.dto.response.LoginResponse;

import com.example.caloriesapp.session.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText etUsername, etPassword;
    Button btnLogin;
    TextView tvRegister;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btn_login);
        tvRegister = findViewById(R.id.tv_register);
        progressBar = findViewById(R.id.progress_bar);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (username.isEmpty()) {
                    etUsername.setError("Enter your username");
                    return;
                }
                if (password.isEmpty()) {
                    etPassword.setError("Enter your password");
                    return;
                }

                showLoading(true);

                UserLoginForm request = new UserLoginForm(username, password);
                UserClient userClient = ApiClient.getClient().create(UserClient.class);

                Call<BaseResponse<LoginResponse>> call = userClient.login(request);

                call.enqueue(new Callback<BaseResponse<LoginResponse>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<LoginResponse>> call, Response<BaseResponse<LoginResponse>> response) {
                        showLoading(false);
                        
                        if (response.isSuccessful() && response.body() != null && !response.body().isError()) {
                            LoginResponse data = response.body().getData();
                            if(data != null) {
                                int userId = data.getUserId();
                                String email = data.getEmail();
                                String token = data.getJwtToken();

                                SessionManager sessionManager = new SessionManager(LoginActivity.this);
                                sessionManager.saveUserSession(userId, email, token);

                                Intent intent = new Intent(LoginActivity.this, HomePageActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<LoginResponse>> call, Throwable t) {
                        showLoading(false);
                        Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            btnLogin.setEnabled(false);
            btnLogin.setText("Logging in...");
        } else {
            progressBar.setVisibility(View.GONE);
            btnLogin.setEnabled(true);
            btnLogin.setText(getString(R.string.loginStr));
        }
    }
}

package com.example.project_mobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project_mobile.api.ApiClient;
import com.example.project_mobile.api.ApiService;
import com.example.project_mobile.databinding.ActivityLoginBinding;
import com.example.project_mobile.dto.AuthResponse;
import com.example.project_mobile.dto.LoginRequest;
import com.example.project_mobile.storage.PreferenceManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPreferences = getSharedPreferences("LoginDetails", MODE_PRIVATE);

        // Load dữ liệu đăng nhập
        binding.etUsername.setText(sharedPreferences.getString("Username", ""));
        binding.etPassword.setText(sharedPreferences.getString("Password", ""));

        // Sự kiện nhấn Sign In
        binding.btnSignIn.setOnClickListener(v -> handleSignInClick());

        // Sự kiện nhấn Sign Up
        binding.tvSignUpNow.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
        });

        // Sự kiện nhấn Forgot Password
        binding.tvForgotPassword.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
        });
    }

    /**
     * Xử lý khi nhấn Sign In
     */
    private void handleSignInClick() {
        binding.btnSignIn.setEnabled(false);

        // Hiển thị lớp phủ và vòng quay
        binding.overlay.setVisibility(View.VISIBLE);
        binding.circularProgress.setVisibility(View.VISIBLE);
        binding.circularProgress.setIndeterminate(true);  // Bắt đầu quay

        // Animation cho nút Sign In
        Animation buttonAnim = AnimationUtils.loadAnimation(this, R.anim.button_scale);
        binding.btnSignIn.startAnimation(buttonAnim);

        buttonAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                attemptLogin();
            }

            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }

    /**
     * Kiểm tra thông tin đăng nhập
     */
    private void attemptLogin() {
        String username = binding.etUsername.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            binding.etUsername.setError("Username is required");
            binding.etUsername.requestFocus();
            resetUI();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            binding.etPassword.setError("Password is required");
            binding.etPassword.requestFocus();
            resetUI();
            return;
        }

        login(username, password);
    }

    /**
     * Gọi API đăng nhập
     */
    private void login(String username, String password) {
        ApiService apiService = ApiClient.getInstance(getApplicationContext());
        LoginRequest loginRequest = new LoginRequest(username, password);

        apiService.login(loginRequest).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                resetUI();

                if (response.isSuccessful() && response.body() != null) {
                    new PreferenceManager(LoginActivity.this).saveToken(response.body().getToken());

                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                resetUI();
                Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Reset UI sau khi API trả về kết quả
     */
    private void resetUI() {
        binding.overlay.setVisibility(View.GONE);
        binding.circularProgress.setVisibility(View.GONE);
        binding.circularProgress.setIndeterminate(false);
        binding.btnSignIn.setEnabled(true);
    }
}

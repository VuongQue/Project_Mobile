package com.example.project_mobile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;

import com.example.project_mobile.api.ApiClient;
import com.example.project_mobile.api.ApiService;
import com.example.project_mobile.databinding.ActivityLoginBinding;
import com.example.project_mobile.dto.AuthResponse;
import com.example.project_mobile.dto.LoginRequest;
import com.example.project_mobile.dto.UserInfoResponse;
import com.example.project_mobile.dto.UsernameRequest;
import com.example.project_mobile.storage.GuestManager;
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

        // Sự kiện nhấn Sign Up với hiệu ứng chuyển
        binding.tvSignUpNow.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);

            Pair<View, String> p1 = Pair.create(binding.tvSignUpNow, "tvSignUpNow");
            Pair<View, String> p2 = Pair.create(binding.btnSignIn, "btnSignIn");

            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(LoginActivity.this, p1, p2);
            startActivity(intent, options.toBundle());
        });

        // Sự kiện nhấn Forgot Password với hiệu ứng chuyển
        binding.tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);

            Pair<View, String> p1 = Pair.create(binding.tvForgotPassword, "tvForgotPassword");
            Pair<View, String> p2 = Pair.create(binding.btnSignIn, "btnSignIn");

            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(LoginActivity.this, p1, p2);
            startActivity(intent, options.toBundle());
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
        binding.circularProgress.setIndeterminate(true);

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
        binding.tvStartAsAGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GuestManager.setGuestMode(getApplicationContext(), true);
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
        });

    }

    /**
     * Kiểm tra thông tin đăng nhập
     */
    private void attemptLogin() {
        String username = binding.etUsername.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        String username = binding.etUsername.getText().toString();
        String password = binding.etPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password)) {
            binding.etPassword.setError("Password is required");
            binding.etPassword.requestFocus();
            resetUI();
            return;
        }

       
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

                    // Hiệu ứng chuyển Activity với Animation
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);


					Pair<View, String> p1 = Pair.create(binding.btnSignIn, "btnSignIn");
                    Pair<View, String> p2 = Pair.create(binding.etUsername, "etUsername");
                    Pair<View, String> p3 = Pair.create(binding.etPassword, "etPassword");

                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(LoginActivity.this, p1, p2, p3);
                    startActivity(intent, options.toBundle());
                    String username = preferenceManager.getUsername();
                    ApiService apiService = ApiClient.getInstance(getApplicationContext());
                    apiService.getUserInfo(new UsernameRequest(username)).enqueue(new Callback<UserInfoResponse>() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onResponse(@NonNull Call<UserInfoResponse> call, @NonNull Response<UserInfoResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                preferenceManager.saveUserInfo(response.body());

                            } else {
                                Log.e("API_ERROR", "Code: " + response.code());
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<UserInfoResponse> call, @NonNull Throwable t) {
                            Log.e("API_ERROR", "Failed to fetch data", t);
                        }
                    });

                    Toast.makeText(LoginActivity.this, R.string.login_success, Toast.LENGTH_SHORT).show();
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

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}

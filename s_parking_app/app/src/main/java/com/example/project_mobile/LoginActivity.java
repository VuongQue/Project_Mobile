package com.example.project_mobile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project_mobile.api.ApiClient;
import com.example.project_mobile.api.ApiService;
import com.example.project_mobile.databinding.ActivityLoginBinding;
import com.example.project_mobile.dto.AuthResponse;
import com.example.project_mobile.dto.LoginRequest;
import com.example.project_mobile.dto.UserInfoResponse;
import com.example.project_mobile.dto.UsernameRequest;
import com.example.project_mobile.storage.GuestManager;
import com.example.project_mobile.storage.PreferenceManager;
import com.example.project_mobile.utils.LocalHelper;
import com.example.project_mobile.utils.SetUp;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;

    private PreferenceManager preferenceManager;
    private SetUp setUp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferenceManager = new PreferenceManager(this);
        setUp = new SetUp(this);

        setUp.loadTheme();
        setUp.loadLocale();

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.etUsername.setText(preferenceManager.getUsername());
        binding.etPassword.setText(preferenceManager.getPassword());
        binding.cbRememberMe.setChecked(preferenceManager.getRememberMeStatus());

        binding.btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });
        binding.tvSignUpNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });
        binding.tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });
        binding.tvStartAsAGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GuestManager.setGuestMode(getApplicationContext(), true);
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
        });

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocalHelper.setLocale(newBase));
    }

    private void attemptLogin() {
        binding.etUsername.setError(null);
        binding.etPassword.setError(null);

        String username = binding.etUsername.getText().toString();
        String password = binding.etPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(username)) {
            binding.etUsername.setError(getString(R.string.error_field_required));
            focusView = binding.etUsername;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        }
        else
        {
            GuestManager.setGuestMode(getApplicationContext(), false);
            login(username, password);
            if (binding.cbRememberMe.isChecked()) {
                preferenceManager.saveLoginDetails(username, password, true);
            } else {
                preferenceManager.clearLoginData();
            }
        }
    }

    private void login(String username, String password) {
        LoginRequest loginRequest = new LoginRequest(username, password);
        ApiService apiService = ApiClient.getInstance(getApplicationContext());
        apiService.login(loginRequest).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PreferenceManager preferenceManager = new PreferenceManager(LoginActivity.this);
                    preferenceManager.saveToken(response.body().getToken());

                    preferenceManager.saveLoginDetails(username, password, true);
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

                    Toast.makeText(LoginActivity.this, preferenceManager.getUsername(), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }
                else {
                    Toast.makeText(LoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "API Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

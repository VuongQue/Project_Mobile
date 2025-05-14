package com.example.project_mobile;

import static com.example.project_mobile.api.ApiClient.BASE_URL;
import static com.example.project_mobile.utils.Validate.isPasswordValid;

import android.annotation.SuppressLint;
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
import com.example.project_mobile.utils.SetUp;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;

    private PreferenceManager preferenceManager;
    private SetUp setUp;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(this);
        setUp = new SetUp(this);

        setUp.loadLocale();
        setUp.loadTheme();

        sharedPreferences = getSharedPreferences("LoginDetails", MODE_PRIVATE);

        binding.etUsername.setText(sharedPreferences.getString("Username", ""));
        binding.etPassword.setText(sharedPreferences.getString("Password", ""));
        binding.cbRememberMe.setChecked(sharedPreferences.getBoolean("Status", false));

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
            if (binding.cbRememberMe.isChecked())
                new PreferenceManager(this).saveLoginDetails(username, password, true);
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

                    // >>>> Thêm đoạn này để lưu Username
                    SharedPreferences prefs = getSharedPreferences("LoginDetails", MODE_PRIVATE);
                    prefs.edit().putString("Username", username).apply();

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

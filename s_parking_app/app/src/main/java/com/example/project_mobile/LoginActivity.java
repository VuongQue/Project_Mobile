package com.example.project_mobile;

import static com.example.project_mobile.api.ApiClient.BASE_URL;
import static com.example.project_mobile.utils.Validate.isPasswordValid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
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
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
    }

    private void attemptLogin() {
        binding.etUsername.setError(null);
        binding.etPassword.setError(null);

        String username = binding.etUsername.getText().toString();
        String password = binding.etPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password))
        {
            binding.etPassword.setError(getString(R.string.error_invalid_password));
            focusView = binding.etPassword;
            cancel = true;
        }

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
            login(username, password);
            if (binding.cbRememberMe.isChecked())
                new PreferenceManager(this).saveloginDetails(username, password, true);
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

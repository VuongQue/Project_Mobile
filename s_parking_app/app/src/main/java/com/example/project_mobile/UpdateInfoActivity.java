package com.example.project_mobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project_mobile.api.ApiClient;
import com.example.project_mobile.api.ApiService;
import com.example.project_mobile.dto.UpdateInfoRequest;
import com.example.project_mobile.dto.UserInfoResponse;
import com.example.project_mobile.storage.PreferenceManager;
import com.example.project_mobile.utils.LocalHelper;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateInfoActivity extends AppCompatActivity {

    private EditText edtFullname, edtPhone, etLicensePlate, edtPassword;
    private String username;
    private ApiService apiService;
    private String source;
    private SharedPreferences sharedPreferences;

    UserInfoResponse userInfoResponse;

    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_info);

        edtFullname = findViewById(R.id.etFullname);
        edtPhone = findViewById(R.id.edtPhone);
        etLicensePlate = findViewById(R.id.etLicensePlate);
        edtPassword = findViewById(R.id.edtPassword);

        apiService = ApiClient.getInstance(this);
        sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);

        preferenceManager = new PreferenceManager(getApplicationContext());
        userInfoResponse = preferenceManager.getUserInfo();

        source = getIntent().getStringExtra("source");
        username = getIntent().getStringExtra("username");

        if (username == null || username.isEmpty()) {
            username = sharedPreferences.getString("Username", "");
        }

        if ("profile_update".equals(source)) {
            edtPassword.setVisibility(View.GONE);
            loadUserData();
        }

        findViewById(R.id.btnUpdateInfo).setOnClickListener(v -> updateUserInfo());
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocalHelper.setLocale(newBase));
    }

    private void loadUserData() {

        edtFullname.setText(userInfoResponse.getFullName());
        edtPhone.setText(userInfoResponse.getPhone());
        etLicensePlate.setText(userInfoResponse.getLicensePlate());
    }

    private void updateUserInfo() {
        String fullname = edtFullname.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String licensePlate = etLicensePlate.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        UpdateInfoRequest request;

        if ("profile_update".equals(source)) {
            request = new UpdateInfoRequest(username, fullname, phone, licensePlate);
        } else {
            request = new UpdateInfoRequest(username, fullname, phone, licensePlate, password);
        }

        apiService.updateUserInfo(request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(UpdateInfoActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    handleResultBasedOnSource();
                } else {
                    Toast.makeText(UpdateInfoActivity.this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(UpdateInfoActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleResultBasedOnSource() {
        if ("otp_verification".equals(source)) {
            Intent intent = new Intent(UpdateInfoActivity.this, LoginActivity.class);
            startActivity(intent);
            finishAffinity();
        } else if ("profile_update".equals(source)) {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
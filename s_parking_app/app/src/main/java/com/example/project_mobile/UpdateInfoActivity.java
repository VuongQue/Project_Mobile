package com.example.project_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project_mobile.api.ApiClient;
import com.example.project_mobile.api.ApiService;
import com.example.project_mobile.dto.UpdateInfoRequest;
import com.example.project_mobile.utils.Validate;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateInfoActivity extends AppCompatActivity {

    private EditText edtFullname, edtPhone, etLicensePlate, edtPassword;
    private String username;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_info);

        edtFullname = findViewById(R.id.etFullname);
        edtPhone = findViewById(R.id.edtPhone);
        etLicensePlate = findViewById(R.id.etLicensePlate);
        edtPassword = findViewById(R.id.edtPassword);

        username = getIntent().getStringExtra("username");
        apiService = ApiClient.getInstance(this);

        findViewById(R.id.btnUpdateInfo).setOnClickListener(v -> updateUserInfo());
    }

    private void updateUserInfo() {
        String fullname = edtFullname.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String key = etLicensePlate.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        // Kiểm tra tính hợp lệ
        if (!Validate.isPhoneNumberValid(phone)) {
            Toast.makeText(this, "Số điện thoại không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Validate.isLicensePlateValid(key)) {
            Toast.makeText(this, "Biển số xe không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Validate.isPasswordValid(password)) {
            Toast.makeText(this, R.string.error_invalid_password, Toast.LENGTH_LONG).show();
            return;
        }

        UpdateInfoRequest request = new UpdateInfoRequest(username, fullname, phone, key, password);

        apiService.updateUserInfo(request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(UpdateInfoActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(UpdateInfoActivity.this, LoginActivity.class));
                    finishAffinity();
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
}

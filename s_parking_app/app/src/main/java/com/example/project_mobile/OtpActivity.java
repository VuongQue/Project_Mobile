package com.example.project_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project_mobile.api.ApiClient;
import com.example.project_mobile.api.ApiService;
import com.example.project_mobile.dto.OTPRequest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpActivity extends AppCompatActivity {

    private EditText edtOtp;
    private String username;
    private String purpose;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        edtOtp = findViewById(R.id.edtOtp);
        findViewById(R.id.btnVerifyOtp).setOnClickListener(v -> verifyOtp());

        username = getIntent().getStringExtra("username");
        purpose = getIntent().getStringExtra("purpose"); // Lấy purpose từ intent

        apiService = ApiClient.getInstance(this);
    }

    private void verifyOtp() {
        String otp = edtOtp.getText().toString().trim();

        if (otp.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập mã OTP", Toast.LENGTH_SHORT).show();
            return;
        }

        OTPRequest request = new OTPRequest(username, otp, purpose); // Truyền purpose

        apiService.verifyOtp(request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(OtpActivity.this, "Xác thực thành công", Toast.LENGTH_SHORT).show();

                    // Tùy vào purpose mà chuyển activity
                    if ("FORGOT_PASSWORD".equals(purpose)) {
                        Intent intent = new Intent(OtpActivity.this, ResetPasswordActivity.class);
                        intent.putExtra("username", username);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(OtpActivity.this, UpdateInfoActivity.class);
                        intent.putExtra("username", username);
                        startActivity(intent);
                    }

                    finish();
                } else {
                    Toast.makeText(OtpActivity.this, "OTP sai hoặc đã hết hạn", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(OtpActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

package com.example.project_mobile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class SignUpActivity extends AppCompatActivity {

    private EditText edtStudentId;
    private ApiService apiService;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edtStudentId = findViewById(R.id.edtStudentId);
        findViewById(R.id.btnSign).setOnClickListener(v -> sendOtp());
        apiService = ApiClient.getInstance(this);
    }

    private void sendOtp() {
        String username = edtStudentId.getText().toString().trim();

        if (username.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập mã số sinh viên", Toast.LENGTH_SHORT).show();
            return;
        }

        OTPRequest request = new OTPRequest(username, null, "ACTIVATE");

        apiService.sendOtp(request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(SignUpActivity.this, "OTP đã gửi về email", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignUpActivity.this, OtpActivity.class);
                    intent.putExtra("username", username);
                    intent.putExtra("purpose", "ACTIVATE");
                    startActivity(intent);
                } else {
                    Toast.makeText(SignUpActivity.this, "Không tìm thấy tài khoản hoặc lỗi gửi OTP", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(SignUpActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
                Log.e("sendOtp", "Error", t);
            }
        });
    }
}

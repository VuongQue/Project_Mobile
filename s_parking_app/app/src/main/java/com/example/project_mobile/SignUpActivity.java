package com.example.project_mobile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project_mobile.api.ApiClient;
import com.example.project_mobile.api.ApiService;
import com.example.project_mobile.dto.OTPRequest;
import com.google.android.material.button.MaterialButton;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    private EditText edtStudentId;
    private MaterialButton btnSign;
    private TextView tvSignInHere;
    private ApiService apiService;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edtStudentId = findViewById(R.id.edtStudentId);
        btnSign = findViewById(R.id.btnSign);
        tvSignInHere = findViewById(R.id.tvSignInHere);

        apiService = ApiClient.getInstance(this);

        applyAnimations();

        btnSign.setOnClickListener(v -> sendOtp());
    }

    /**
     * Áp dụng Animation
     */
    private void applyAnimations() {
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);
        Animation bounce = AnimationUtils.loadAnimation(this, R.anim.bounce);

        findViewById(R.id.ivLogo).startAnimation(fadeIn);
        findViewById(R.id.tvTitle).startAnimation(fadeIn);
        findViewById(R.id.tvSubTitle).startAnimation(fadeIn);

        edtStudentId.startAnimation(slideIn);
        btnSign.startAnimation(bounce);
        tvSignInHere.startAnimation(slideIn);
    }

    /**
     * Gửi OTP
     */
    private void sendOtp() {
        String studentId = edtStudentId.getText().toString().trim();

        if (studentId.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập mã số sinh viên", Toast.LENGTH_SHORT).show();
            return;
        }

        OTPRequest request = new OTPRequest(studentId, null, "ACTIVATE");

        apiService.sendOtp(request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(SignUpActivity.this, "OTP đã gửi về email", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignUpActivity.this, OtpActivity.class);
                    intent.putExtra("username", studentId);
                    startActivity(intent);
                } else {
                    Toast.makeText(SignUpActivity.this, "Lỗi gửi OTP", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(SignUpActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
                Log.e("SignUpActivity", "Error: " + t.getMessage());
            }
        });
    }
}

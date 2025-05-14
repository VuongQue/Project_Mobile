package com.example.project_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;

import com.example.project_mobile.api.ApiClient;
import com.example.project_mobile.api.ApiService;
import com.example.project_mobile.dto.ResetPasswordRequest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText edtNewPassword, edtConfirmPassword;
    private View rootView;
    private String username;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        edtNewPassword = findViewById(R.id.edtNewPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        rootView = findViewById(android.R.id.content);

        findViewById(R.id.btnResetPassword).setOnClickListener(v -> handleResetPassword(v));

        username = getIntent().getStringExtra("username");
        apiService = ApiClient.getInstance(this);

        applyAnimations();
    }

    /**
     * Áp dụng Animation cho toàn bộ màn hình
     */
    private void applyAnimations() {
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);
        Animation bounce = AnimationUtils.loadAnimation(this, R.anim.bounce);

        rootView.startAnimation(fadeIn);
        edtNewPassword.startAnimation(slideIn);
        edtConfirmPassword.startAnimation(slideIn);
        findViewById(R.id.btnResetPassword).startAnimation(bounce);
    }

    /**
     * Xử lý đặt lại mật khẩu và áp dụng hiệu ứng chuyển Activity
     */
    private void handleResetPassword(View view) {
        String newPassword = edtNewPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Không được để trống mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        ResetPasswordRequest request = new ResetPasswordRequest(username, newPassword);

        apiService.resetPassword(request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ResetPasswordActivity.this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);

                    // Tạo hiệu ứng chuyển Activity
                    Pair<View, String> p1 = Pair.create(findViewById(R.id.edtNewPassword), "newPasswordTransition");
                    Pair<View, String> p2 = Pair.create(findViewById(R.id.edtConfirmPassword), "confirmPasswordTransition");
                    Pair<View, String> p3 = Pair.create(findViewById(R.id.btnResetPassword), "resetButtonTransition");

                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            ResetPasswordActivity.this, p1, p2, p3
                    );

                    startActivity(intent, options.toBundle());
                    finishAffinity();
                } else {
                    Toast.makeText(ResetPasswordActivity.this, "Không thể đặt lại mật khẩu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ResetPasswordActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

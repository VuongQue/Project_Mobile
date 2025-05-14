package com.example.project_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;

import com.example.project_mobile.api.ApiClient;
import com.example.project_mobile.api.ApiService;
import com.example.project_mobile.dto.UpdateInfoRequest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateInfoActivity extends AppCompatActivity {

    private EditText edtFullname, edtPhone, edtSecurityKey, edtPassword;
    private String username;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_info);

        edtFullname = findViewById(R.id.edtFullname);
        edtPhone = findViewById(R.id.edtPhone);
        edtSecurityKey = findViewById(R.id.edtSecurityKey);
        edtPassword = findViewById(R.id.edtPassword);

        username = getIntent().getStringExtra("username");
        apiService = ApiClient.getInstance(this);

        findViewById(R.id.btnUpdateInfo).setOnClickListener(v -> updateUserInfo(v));
    }

    /**
     * Cập nhật thông tin người dùng với hiệu ứng chuyển Activity
     */
    private void updateUserInfo(View view) {
        String fullname = edtFullname.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String key = edtSecurityKey.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        UpdateInfoRequest request = new UpdateInfoRequest(username, fullname, phone, key, password);

        apiService.updateUserInfo(request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(UpdateInfoActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(UpdateInfoActivity.this, LoginActivity.class);

                    // Tạo hiệu ứng chuyển Activity
                    Pair<View, String> p1 = Pair.create(findViewById(R.id.edtFullname), "fullnameTransition");
                    Pair<View, String> p2 = Pair.create(findViewById(R.id.edtPhone), "phoneTransition");
                    Pair<View, String> p3 = Pair.create(findViewById(R.id.edtSecurityKey), "securityKeyTransition");
                    Pair<View, String> p4 = Pair.create(findViewById(R.id.edtPassword), "passwordTransition");
                    Pair<View, String> p5 = Pair.create(findViewById(R.id.btnUpdateInfo), "updateButtonTransition");

                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            UpdateInfoActivity.this, p1, p2, p3, p4, p5
                    );

                    startActivity(intent, options.toBundle());
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

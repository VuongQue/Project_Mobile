package com.example.project_mobile.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.project_mobile.R;
import com.example.project_mobile.api.ApiClient;
import com.example.project_mobile.api.ApiService;
import com.example.project_mobile.databinding.FragmentProfileBinding;
import com.example.project_mobile.dto.UserInfoResponse;
import com.example.project_mobile.dto.UsernameRequest;
import com.example.project_mobile.storage.PreferenceManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    FragmentProfileBinding binding;
    SharedPreferences sharedPreferences;
    private boolean isKeyVisible = false;

    public ProfileFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater
            , @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferences = requireActivity().getSharedPreferences("UserInfo", MODE_PRIVATE);
        boolean flag = sharedPreferences.getBoolean("isLoaded", false);
        if (!flag)
        {
            LoadFromAPI();
        }

        Load();

        binding.btnReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadFromAPI();
            }
        });
        binding.icHidden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kiểm tra trạng thái hiện tại của mật khẩu
                if (isKeyVisible) {
                    binding.etKey.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);  // Chế độ ẩn
                    binding.icHidden.setImageResource(R.drawable.white_hidden);  // Thay đổi icon mắt (Ví dụ: mắt mở)
                } else {
                    binding.etKey.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);  // Chế độ hiện
                    binding.icHidden.setImageResource(R.drawable.white_eye);  // Thay đổi icon mắt (Ví dụ: mắt đóng)
                }
                isKeyVisible = !isKeyVisible;  // Đổi trạng thái
            }
        });
    }

    private void Load() {
        try {
            binding.tvName.setText(sharedPreferences.getString("FullName", ""));
            binding.tvEmail.setText(sharedPreferences.getString("Email", ""));
            binding.tvPhone.setText(sharedPreferences.getString("Phone", ""));
            binding.etKey.setText(sharedPreferences.getString("Security_Key", ""));

        } catch (IllegalStateException ex) {
            Log.e("ProfileFragment", "Activity is null (requireActivity threw), fallback to safe handling.");
        }
    }

    private void LoadFromAPI() {
        String username = requireActivity().getSharedPreferences("LoginDetails", MODE_PRIVATE).getString("Username", "");
        ApiService apiService = ApiClient.getInstance(getContext());
        apiService.getUserInfo(new UsernameRequest(username)).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<UserInfoResponse> call, @NonNull Response<UserInfoResponse> response) {
                if (response.isSuccessful() & response.body() != null) {
                    new PreferenceManager(getContext()).saveUserInfo(response.body());

                    Load();
                } else {
                    Log.e("API_ERROR", "Code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserInfoResponse> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Failed to fetch data", t);
            }
        });
    }


}

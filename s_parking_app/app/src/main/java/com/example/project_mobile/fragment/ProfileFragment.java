package com.example.project_mobile.fragment;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.project_mobile.R;
import com.example.project_mobile.UpdateInfoActivity;
import com.example.project_mobile.api.ApiClient;
import com.example.project_mobile.api.ApiService;
import com.example.project_mobile.databinding.FragmentProfileBinding;
import com.example.project_mobile.dto.SuccessResponse;
import com.example.project_mobile.dto.UpdateAvatarRequest;
import com.example.project_mobile.dto.UserInfoResponse;
import com.example.project_mobile.dto.UsernameRequest;
import com.example.project_mobile.storage.PreferenceManager;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    FragmentProfileBinding binding;
    SharedPreferences sharedPreferences;

    PreferenceManager preferenceManager;
    private ImageView selectedImageView;
    Uri selectedImageUri;
    Button uploadButton;
    Button selectImageButton;
    Dialog dialog;
    private static final int PICK_IMAGE_REQUEST = 1;

    public ProfileFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LoadFromAPI();

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
        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_upload_image);
        selectedImageView = dialog.findViewById(R.id.selectedImageView);
        selectImageButton = dialog.findViewById(R.id.selectImageButton);
        uploadButton = dialog.findViewById(R.id.uploadButton);

        preferenceManager = new PreferenceManager(requireContext());

        sharedPreferences = requireActivity().getSharedPreferences("UserInfo", MODE_PRIVATE);
        LoadFromAPI();

        binding.btnUpdate.setOnClickListener(v -> {
            String username = preferenceManager.getUsername();

            if (username.isEmpty()) {
                Toast.makeText(getActivity(), "Không tìm thấy tài khoản", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(getActivity(), UpdateInfoActivity.class);
            intent.putExtra("username", username);  // Truyền username qua Intent
            intent.putExtra("source", "profile_update");
            startActivity(intent);
        });

        binding.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUploadImageDialog();
            }
        });
        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImageToCloudinary();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("ProfileFragment", "onResume - Gọi LoadFromAPI để cập nhật dữ liệu mới");
        preferenceManager = new PreferenceManager(requireContext());
        LoadFromAPI();
    }
    private void sendToServer(String url) {
        String username = requireActivity().getSharedPreferences("LoginDetails", MODE_PRIVATE).getString("Username", "");
        ApiService apiService = ApiClient.getInstance(getContext());
        UpdateAvatarRequest request = new UpdateAvatarRequest(username, url);
        apiService.updateAvatar(request).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<SuccessResponse> call, @NonNull Response<SuccessResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Tải lên thành công", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(@NonNull Call<SuccessResponse> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Failed to fetch data", t);
            }
        });
    }

    private void showUploadImageDialog() {
        dialog.show();
    }

    // Mở gallery để chọn ảnh
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Handle result from picking an image
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();

            // Sử dụng Glide để load ảnh vào ImageView
            Glide.with(requireContext())
                    .load(selectedImageUri)
                    .into(selectedImageView);

            Log.d("Image Selection", "Selected Image URI: " + selectedImageUri.toString());

            uploadButton.setEnabled(true);
        }
    }


    // Tải ảnh lên Cloudinary
    private void uploadImageToCloudinary() {
        if (selectedImageUri == null) {
            Toast.makeText(getContext(), "Vui lòng chọn ảnh!", Toast.LENGTH_SHORT).show();
            return;
        }
        HashMap<String, Object> config = new HashMap<>();
        config.put("cloud_name", "dbwzloucf");
        config.put("api_key", "322178371811559");

        MediaManager.init(getContext(), config);

        MediaManager.get().upload(selectedImageUri)
                .unsigned("mobile_project")
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        Log.d("UPLOAD", "Bắt đầu upload...");
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {
                        // Có thể show tiến trình
                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        String avatarUrl = (String) resultData.get("secure_url");

                        Log.d("UPLOAD", "Upload thành công: " + avatarUrl);
                        sendToServer(avatarUrl);
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Log.d("UPLOAD", error.getDescription());
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {
                        Log.e("UPLOAD", "Upload bị hoãn lại: " + error.getDescription());
                    }
                }).dispatch();
        LoadFromAPI();
        dialog.cancel();
    }


    private void Load() {
        try {
            UserInfoResponse userInfoResponse = preferenceManager.getUserInfo();

            if (userInfoResponse != null) {
                Log.d("Load()", "License Plate: " + userInfoResponse.getLicensePlate());
                Log.d("Load()", "Full Name: " + userInfoResponse.getFullName());

                binding.tvLicensePlate.setText(userInfoResponse.getLicensePlate());
                binding.tvName.setText(userInfoResponse.getFullName());
                binding.tvEmail.setText(userInfoResponse.getEmail());
                binding.tvPhone.setText(userInfoResponse.getPhone());

                String avatarUrl = userInfoResponse.getAvatarUrl();
                if (!avatarUrl.isEmpty()) {
                    Glide.with(requireContext())
                            .load(Uri.parse(avatarUrl))
                            .into(binding.avatar);
                } else {
                    binding.avatar.setImageResource(android.R.drawable.sym_def_app_icon);
                }
            } else {
                Log.e("Load()", "UserInfoResponse is null");
            }

        } catch (Exception ex) {
            Log.e("Load()", "Exception: " + ex.getMessage());
        }
    }

    private void LoadFromAPI() {
        if (preferenceManager !=null)
        {
            String username = preferenceManager.getUsername();
            ApiService apiService = ApiClient.getInstance(getContext());
            apiService.getUserInfo(new UsernameRequest(username)).enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<UserInfoResponse> call, @NonNull Response<UserInfoResponse> response) {
                    if (response.isSuccessful() & response.body() != null) {
                        preferenceManager.saveUserInfo(response.body());

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
        else {
            Log.e("ProfileFragment", "PreferenceManager is null");
        }
    }
}
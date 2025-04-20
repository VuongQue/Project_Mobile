package com.example.project_mobile.fragment;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

import static com.example.project_mobile.api.CloudinaryConfig.getCloudinaryInstance;

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
import com.cloudinary.utils.ObjectUtils;
import com.example.project_mobile.R;
import com.example.project_mobile.api.ApiClient;
import com.example.project_mobile.api.ApiService;
import com.example.project_mobile.databinding.FragmentProfileBinding;
import com.example.project_mobile.dto.SuccessResponse;
import com.example.project_mobile.dto.UpdateAvatarRequest;
import com.example.project_mobile.dto.UserInfoResponse;
import com.example.project_mobile.dto.UsernameRequest;
import com.example.project_mobile.storage.PreferenceManager;
import com.example.project_mobile.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    FragmentProfileBinding binding;
    SharedPreferences sharedPreferences;
    private ImageView selectedImageView;
    Uri selectedImageUri;
    Button uploadButton;
    Button selectImageButton;
    Dialog dialog;
    private static final int PICK_IMAGE_REQUEST = 1;
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
        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_upload_image);
        selectedImageView = dialog.findViewById(R.id.selectedImageView);
        selectImageButton = dialog.findViewById(R.id.selectImageButton);
        uploadButton = dialog.findViewById(R.id.uploadButton);

        sharedPreferences = requireActivity().getSharedPreferences("UserInfo", MODE_PRIVATE);
        LoadFromAPI();

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

    private void sendToServer(String url) {
        String username = requireActivity().getSharedPreferences("LoginDetails", MODE_PRIVATE).getString("Username", "");
        ApiService apiService = ApiClient.getInstance(getContext());
        UpdateAvatarRequest request = new UpdateAvatarRequest(username, url);
        apiService.updateAvatar(request).enqueue(new Callback<SuccessResponse>() {
            @Override
            public void onResponse(Call<SuccessResponse> call, Response<SuccessResponse> response) {
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            selectedImageView.setImageURI(selectedImageUri);
            uploadButton.setEnabled(true); // Kích hoạt nút tải lên
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
                        sendToServer(avatarUrl); // 👈 Gửi về API
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
            binding.tvName.setText(sharedPreferences.getString("FullName", ""));
            binding.tvEmail.setText(sharedPreferences.getString("Email", ""));
            binding.tvPhone.setText(sharedPreferences.getString("Phone", ""));
            binding.etKey.setText(sharedPreferences.getString("Security_Key", ""));
            String avatarUrl = sharedPreferences.getString("Avatar_Url", ""); // Lấy URL từ SharedPreferences

            if (!avatarUrl.isEmpty()) {
                Glide.with(getContext())
                        .load(Uri.parse(avatarUrl))  // Tải ảnh từ URL
                        .into(binding.avatar);  // Gán vào ImageView
            } else {
                // Nếu không có URL hợp lệ, có thể sử dụng ảnh mặc định
                Glide.with(getContext())
                        .load(android.R.drawable.sym_def_app_icon)  // Sử dụng ảnh mặc định từ drawable
                        .into(binding.avatar);
            }

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

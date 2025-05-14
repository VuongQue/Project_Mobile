package com.example.project_mobile.fragment;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
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
import com.example.project_mobile.api.ApiClient;
import com.example.project_mobile.api.ApiService;
import com.example.project_mobile.databinding.FragmentProfileBinding;
import com.example.project_mobile.dto.ParkingAreaResponse;
import com.example.project_mobile.dto.SuccessResponse;
import com.example.project_mobile.dto.UpdateAvatarRequest;
import com.example.project_mobile.dto.UserInfoResponse;
import com.example.project_mobile.dto.UsernameRequest;
import com.example.project_mobile.storage.PreferenceManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    FragmentProfileBinding binding;
    private PreferenceManager preferenceManager;
    private ImageView selectedImageView;
    Uri selectedImageUri;
    Button uploadButton;
    Button selectImageButton;
    Dialog dialog;
    private static final int PICK_IMAGE_REQUEST = 1;

    public ProfileFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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

        Load();

        binding.btnReload.setOnClickListener(v -> Load());

        binding.avatar.setOnClickListener(v -> showUploadImageDialog());

        selectImageButton.setOnClickListener(v -> openGallery());

        uploadButton.setOnClickListener(v -> uploadImageToCloudinary());
    }

    private void showUploadImageDialog() {
        dialog.show();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            selectedImageView.setImageURI(selectedImageUri);
            uploadButton.setEnabled(true);
        }
    }

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
                        Toast.makeText(getContext(), "Bắt đầu tải lên...", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {
                        int progress = (int) ((bytes * 100) / totalBytes);
                        Log.d("UPLOAD_PROGRESS", "Đã tải lên: " + progress + "%");
                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        String avatarUrl = (String) resultData.get("secure_url");
                        sendToServer(avatarUrl);
                        preferenceManager.saveUserInfo(new UserInfoResponse(
                                preferenceManager.getUserInfo().getUsername(),
                                preferenceManager.getUserInfo().getFullName(),
                                preferenceManager.getUserInfo().getEmail(),
                                preferenceManager.getUserInfo().getPhone(),
                                preferenceManager.getUserInfo().getLicensePlate(),
                                avatarUrl
                        ));
                        Load();
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Toast.makeText(getContext(), "Lỗi tải lên: " + error.getDescription(), Toast.LENGTH_SHORT).show();
                        Log.d("UPLOAD_ERROR", error.getDescription());
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {
                        Toast.makeText(getContext(), "Tải lên bị hoãn lại. Đang thực hiện lại...", Toast.LENGTH_SHORT).show();
                    }
                }).dispatch();
        dialog.cancel();
    }

    private void Load() {

        UserInfoResponse userInfo = preferenceManager.getUserInfo();
        if (userInfo!= null)
        {
            binding.tvName.setText(userInfo.getFullName());
            binding.tvEmail.setText(userInfo.getEmail());
            binding.tvPhone.setText(userInfo.getPhone());
            binding.tvLicensePlate.setText(userInfo.getLicensePlate());

            String avatarUrl = userInfo.getAvatarUrl();
            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                Glide.with(requireContext()).load(Uri.parse(avatarUrl)).into(binding.avatar);
            } else {
                Glide.with(requireContext()).load(android.R.drawable.sym_def_app_icon).into(binding.avatar);
            }
        }

    }

    private void sendToServer(String url) {
        String username = preferenceManager.getUserInfo().getUsername();
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
                Log.e("API_ERROR", "Failed to update avatar", t);
            }
        });
    }
}

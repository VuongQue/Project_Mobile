package com.example.project_mobile.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.project_mobile.storage.GuestManager;
import com.example.project_mobile.adapter.NotificationAdapter;
import com.example.project_mobile.api.ApiClient;
import com.example.project_mobile.api.ApiService;
import com.example.project_mobile.databinding.FragmentNotificationBinding;
import com.example.project_mobile.dto.NotificationRequest;
import com.example.project_mobile.dto.NotificationResponse;
import com.example.project_mobile.dto.SuccessResponse;
import com.example.project_mobile.dto.UsernameRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationFragment extends Fragment {

    private FragmentNotificationBinding binding;
    private List<NotificationResponse> notificationList;
    private NotificationAdapter adapter;
    private String username;
    private boolean isGuest;

    public NotificationFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isGuest = GuestManager.isGuest(requireContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        loadNotifications();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNotificationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        username = requireActivity().getSharedPreferences("LoginDetails", MODE_PRIVATE).getString("Username", "");
        notificationList = new ArrayList<>();

        adapter = new NotificationAdapter(getContext(), notificationList, new NotificationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(NotificationResponse notificationResponse, int position) {
                if (!isGuest) {
                    updateNotificationStatus(notificationResponse, position);
                } else {
                    Toast.makeText(getContext(), "Guest Mode: Không thể cập nhật thông báo.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        binding.recyclerView.setAdapter(adapter);

        loadNotifications();
    }

    /**
     * Tải thông báo cho người dùng.
     * Khách (Guest) chỉ có thể tải dữ liệu qua phương thức GET.
     */
    private void loadNotifications() {
        ApiService apiService = ApiClient.getInstance(getContext());
        notificationList.clear();
        adapter.notifyDataSetChanged();

        UsernameRequest request = new UsernameRequest(username);
        apiService.getMyNotifications(request).enqueue(new Callback<List<NotificationResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<NotificationResponse>> call, @NonNull Response<List<NotificationResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    notificationList.clear();
                    notificationList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e("API_ERROR", "Code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<NotificationResponse>> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Failed to fetch notifications", t);
            }
        });
    }

    /**
     * Cập nhật trạng thái thông báo.
     * Chỉ cho phép với người dùng đã đăng nhập.
     */
    private void updateNotificationStatus(NotificationResponse notificationResponse, int position) {
        ApiService apiService = ApiClient.getInstance(getContext());

        if (!notificationResponse.isRead()) {
            NotificationRequest request = new NotificationRequest(username, notificationResponse.getId());

            apiService.updateNotificationStatus(request).enqueue(new Callback<SuccessResponse>() {
                @Override
                public void onResponse(@NonNull Call<SuccessResponse> call, @NonNull Response<SuccessResponse> response) {
                    if (response.isSuccessful()) {
                        notificationResponse.setRead(true);
                        adapter.notifyItemChanged(position);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<SuccessResponse> call, @NonNull Throwable t) {
                    Log.e("API_ERROR", "Failed to update notification", t);
                }
            });
        }
    }
}
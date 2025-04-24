package com.example.project_mobile.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
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
    FragmentNotificationBinding binding;
    public static List<NotificationResponse> notificationList;
    private NotificationAdapter adapter;
    String username;

    public NotificationFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        load();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater
            , @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        binding = FragmentNotificationBinding.inflate(inflater, container, false);
        // recyclerView
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        username = requireActivity().getSharedPreferences("LoginDetails", MODE_PRIVATE).getString("Username", "");
        load();
    }

    private void load() {
        notificationList = new ArrayList<>();
        adapter = new NotificationAdapter(getContext(), notificationList, new NotificationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(NotificationResponse notificationResponse, int position) {
                if (!notificationResponse.isRead())
                {
                    ApiService apiService = ApiClient.getInstance(getContext());
                    NotificationRequest request = new NotificationRequest(username, notificationResponse.getId());
                    apiService.updateNotificationStatus(request).enqueue(new Callback<>() {
                        @Override
                        public void onResponse(@NonNull Call<SuccessResponse> call, @NonNull Response<SuccessResponse> response) {
                            if (response.isSuccessful()) {
                                notificationResponse.setRead(true);
                                adapter.notifyItemChanged(position);
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<SuccessResponse> call, @NonNull Throwable t) {
                            Log.e("API_ERROR", "Failed to update data", t);
                        }
                    });

                }
            }
        });
        binding.recyclerView.setAdapter(adapter);
        UsernameRequest request = new UsernameRequest(username);
        ApiService apiService = ApiClient.getInstance(getContext());
        apiService.getMyNotifications(request).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<NotificationResponse>> call, @NonNull Response<List<NotificationResponse>> response) {
                if (response.isSuccessful() && !Objects.requireNonNull(response.body()).isEmpty())
                {
                    notificationList.clear();
                    notificationList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                }
                else {
                    Log.e("API_ERROR", "Code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<NotificationResponse>> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Failed to fetch data", t);
            }
        });

    }
}

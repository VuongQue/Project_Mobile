package com.example.project_mobile.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.project_mobile.adapter.NotificationAdapter;
import com.example.project_mobile.databinding.FragmentNotificationBinding;
import com.example.project_mobile.model.Notification;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NotificationFragment extends Fragment {
    FragmentNotificationBinding binding;
    private List<Notification> notificationList;
    private NotificationAdapter adapter;

    public NotificationFragment() {
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
        binding = FragmentNotificationBinding.inflate(inflater, container, false);
        // recyclerView
        Load();

        return binding.getRoot();
    }

    private void Load() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        notificationList = new ArrayList<>();
        adapter = new NotificationAdapter(getContext(), notificationList, new NotificationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Notification notification) {
                Toast.makeText(getContext(), "Selected: " + notification.getId(), Toast.LENGTH_SHORT).show();
            }
        });
        binding.recyclerView.setAdapter(adapter);

        notificationList.clear();
        notificationList.add(new Notification(1L, "Thông báo 1", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", new Date(2025 - 1900, 2, 9, 8, 30)));
        notificationList.add(new Notification(2L, "Thông báo 2", "bbbbbbbbbbbbbb", new Date(2025 - 1900, 2, 10, 9, 15)));
        notificationList.add(new Notification(3L, "Thông báo 3", "cccccccccccccccc", new Date(2025 - 1900, 2, 11, 7, 50)));
        notificationList.add(new Notification(4L, "Thông báo 4", "ddddddddddddd", new Date(2025 - 1900, 2, 12, 10, 5)));

        adapter.notifyDataSetChanged();
    }
}

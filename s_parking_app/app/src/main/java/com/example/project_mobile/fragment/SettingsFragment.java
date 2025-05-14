package com.example.project_mobile.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.project_mobile.LoginActivity;
import com.example.project_mobile.databinding.FragmentSettingsBinding;
import com.example.project_mobile.storage.GuestManager;
import com.example.project_mobile.storage.PreferenceManager;
import com.example.project_mobile.dto.UserInfoResponse;
import com.example.project_mobile.utils.SetUp;

public class SettingsFragment extends Fragment {

    FragmentSettingsBinding binding;

    Boolean isGuest;
    private PreferenceManager preferenceManager;
    private SetUp setUp;

    private final BroadcastReceiver logoutReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("ACTION_LOGOUT".equals(intent.getAction()) && isAdded()) {
                requireActivity().finish();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(requireContext());

        // Đăng ký BroadcastReceiver
        ContextCompat.registerReceiver(requireContext(), logoutReceiver, new IntentFilter("ACTION_LOGOUT"), ContextCompat.RECEIVER_NOT_EXPORTED);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);

        setupLanguageSpinner();
        setupModeSwitch();
        setupLogoutButton();
        isGuest = GuestManager.isGuest(requireContext());

        if (!isGuest) {
            loadUserInfo();
        }
        return binding.getRoot();
    }

    private void loadUserInfo() {
        UserInfoResponse userInfo = preferenceManager.getUserInfo();
        binding.tvName.setText(userInfo != null ? userInfo.getFullName() : "Guest");

        String avatarUrl = userInfo != null ? userInfo.getAvatarUrl() : null;
        Glide.with(requireContext())
                .load(avatarUrl != null ? Uri.parse(avatarUrl) : android.R.drawable.sym_def_app_icon)
                .into(binding.avatar);
    }

    private void setupLogoutButton() {
        binding.btnLogOut.setOnClickListener(v -> {
            preferenceManager.clearAllData();

            // Gửi Broadcast chỉ trong ứng dụng hiện tại
            Intent logoutIntent = new Intent("ACTION_LOGOUT");
            logoutIntent.setPackage(requireContext().getPackageName());
            requireContext().sendBroadcast(logoutIntent);

            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            requireActivity().finish();
        });
    }

    private void setupLanguageSpinner() {
        String[] languages = {"English", "Tiếng Việt"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.languageSpinner.setAdapter(adapter);

        binding.languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLanguage = position == 0 ? "en" : "vi";
                preferenceManager.setLanguage(selectedLanguage);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupModeSwitch() {
        boolean darkModeOn = preferenceManager.isDarkMode();
        binding.themSw.setChecked(darkModeOn);

        binding.themSw.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferenceManager.setDarkMode(isChecked);

            setUp = new SetUp(getContext());
            setUp.loadTheme();
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        requireContext().unregisterReceiver(logoutReceiver);
    }
}
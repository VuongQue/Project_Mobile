package com.example.project_mobile.fragment;

import static com.example.project_mobile.MainActivity.setUp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.example.project_mobile.databinding.FragmentSettingsBinding;
import com.example.project_mobile.utils.SetUp;

public class SettingsFragment extends Fragment {

    FragmentSettingsBinding binding;
    SharedPreferences sharedPreferences;

    public SettingsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Inflate layout
        binding = FragmentSettingsBinding.inflate(inflater, container, false);

        // Setup language & dark mode
        setupLanguageSpinner();
        setupModeSwitch();


        return binding.getRoot();
    }

    private void setupLanguageSpinner() {
        String[] languages = {"English", "Tiếng Việt"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.languageSpinner.setAdapter(adapter);

        binding.languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
                                       int position, long id) {
                String selectedLanguage = position == 0 ? "en" : "vi";
                setUp.setLocale(selectedLanguage);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });
    }

    private void setupModeSwitch() {
        sharedPreferences = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        boolean darkModeOn = sharedPreferences.getBoolean("darkMode", false);

        // Set trạng thái ban đầu cho switch
        binding.themSw.setChecked(darkModeOn);

        binding.themSw.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SetUp setUp = new SetUp(requireContext());
            setUp.setMode(isChecked);

            // Lưu lại tab hiện tại (ví dụ đang ở Settings)
            SharedPreferences tabPref = requireContext().getSharedPreferences("tabState", Context.MODE_PRIVATE);
            tabPref.edit().putInt("currentTab", 4).apply();

            // Gọi recreate
            new android.os.Handler().postDelayed(() -> {
                if (getActivity() != null) {
                    getActivity().recreate();
                }
            }, 100);

        });


    }
}

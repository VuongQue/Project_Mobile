package com.example.project_mobile.fragment;

import static com.example.project_mobile.MainActivity.setUp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.project_mobile.databinding.FragmentProfileBinding;
import com.example.project_mobile.databinding.FragmentSettingsBinding;
import com.example.project_mobile.utils.SetUp;

public class SettingsFragment extends Fragment {

    FragmentSettingsBinding binding;

    public SettingsFragment() {
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
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        // recyclerView
        setupLanguageSpinner();
        return binding.getRoot();
    }

    private void setupLanguageSpinner() {

        // Tạo danh sách các ngôn ngữ
        String[] languages = {"English", "Tiếng Việt"};

        // Sử dụng ArrayAdapter để gán danh sách ngôn ngữ vào Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.languageSpinner.setAdapter(adapter);

        // Thiết lập sự kiện khi người dùng chọn ngôn ngữ từ Spinner
        binding.languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, android.view.View selectedItemView, int position, long id) {
                String selectedLanguage = position == 0 ? "en" : "vi"; // English -> "en", Tiếng Việt -> "vi"
                setUp.setLocale(selectedLanguage);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Không làm gì khi không có lựa chọn
            }
        });
    }


}
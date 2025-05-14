package com.example.project_mobile.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;

import androidx.appcompat.app.AppCompatDelegate;

import com.example.project_mobile.storage.PreferenceManager;

import java.util.Locale;

public class SetUp {

    private final Context context;
    private final PreferenceManager preferenceManager;

    private static final String KEY_DARK_MODE = "darkMode";

    public SetUp(Context context) {
        this.context = context.getApplicationContext(); // Sử dụng application context để tránh memory leak
        this.preferenceManager = new PreferenceManager(context);
    }

    /**
     * Tải ngôn ngữ đã lưu và áp dụng
     */
    public void loadLocale() {
        String language = preferenceManager.getLanguage();
        setLocale(language);
    }

    /**
     * Thay đổi ngôn ngữ và lưu vào SharedPreferences
     */
    public void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        context.getResources().updateConfiguration(config, metrics);

        // Lưu ngôn ngữ đã chọn
        preferenceManager.setLanguage(lang);
    }

    /**
     * Thiết lập theme dựa trên chế độ đã lưu
     */
    public void loadTheme() {
        boolean isDarkModeOn = preferenceManager.getSettings().getBoolean(KEY_DARK_MODE, false);
        AppCompatDelegate.setDefaultNightMode(
                isDarkModeOn ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
    }

    /**
     * Thay đổi chế độ sáng/tối và lưu vào SharedPreferences
     */
    public void setMode(boolean isDark) {
        preferenceManager.getSettings().edit()
                .putBoolean(KEY_DARK_MODE, isDark)
                .apply();

        AppCompatDelegate.setDefaultNightMode(
                isDark ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
    }

    /**
     * Xóa toàn bộ dữ liệu
     */
    public void clearData() {
        preferenceManager.clearAllData();
    }
}

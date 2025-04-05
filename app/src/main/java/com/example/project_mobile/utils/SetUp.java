package com.example.project_mobile.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;

import com.example.project_mobile.storage.PreferenceManager;

import java.util.Locale;

public class SetUp {

    private Context context;
    private PreferenceManager preferenceManager;

    public SetUp(Context context) {
        this.context = context;
        this.preferenceManager = new PreferenceManager(context);
    }

    // Hàm tải ngôn ngữ đã lưu từ SharedPreferences và thay đổi ngôn ngữ
    public void loadLocale() {
        String language = preferenceManager.getLanguage();
        setLocale(language);
    }

    // Hàm thay đổi ngôn ngữ
    public void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        Configuration config = context.getResources().getConfiguration();
        config.setLocale(locale);

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        context.getResources().updateConfiguration(config, metrics);

        // Lưu ngôn ngữ đã chọn
        preferenceManager.setLanguage(lang);
    }
}

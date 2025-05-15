package com.example.project_mobile.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;

import com.example.project_mobile.storage.PreferenceManager;

import java.util.Locale;

public class LocalHelper {

    public static Context setLocale(Context context) {
        PreferenceManager preferenceManager = new PreferenceManager(context);
        String language = preferenceManager.getLanguage(); // lấy từ SharedPreferences
        return updateResources(context, language);
    }

    public static Context setLocale(Context context, String language) {
        PreferenceManager preferenceManager = new PreferenceManager(context);
        preferenceManager.setLanguage(language); // lưu lại ngôn ngữ
        return updateResources(context, language);
    }

    private static Context updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return context.createConfigurationContext(config);
        } else {
            context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
            return context;
        }
    }
}

package com.example.project_mobile.storage;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {

    private static final String PREF_SETTINGS = "Settings";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public PreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_SETTINGS, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // Lưu ngôn ngữ vào SharedPreferences
    public void setLanguage(String language) {
        editor.putString("My_Lang", language);
        editor.apply();
    }

    // Lấy ngôn ngữ đã lưu
    public String getLanguage() {
        return sharedPreferences.getString("My_Lang", "en"); // mặc định là tiếng Anh
    }
}

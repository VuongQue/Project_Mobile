package com.example.project_mobile.storage;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
    Context context;

    public PreferenceManager(Context context) {
        this.context = context;
    }

    // Lưu ngôn ngữ vào SharedPreferences
    public void setLanguage(String language) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor = sharedPreferences.edit();
        editor.putString("My_Lang", language);
        editor.apply();
    }
    // Lấy ngôn ngữ đã lưu
    public String getLanguage() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        return sharedPreferences.getString("My_Lang", "en");
    }

    public void saveloginDetails(String username, String password, boolean status){
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Username", username);
        editor.putString("Password", password);
        editor.putBoolean("Status", status);
        editor.apply();
    }

    public void saveToken(String token) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Token", token);
        editor.apply();
    }
}

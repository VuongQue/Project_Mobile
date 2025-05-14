package com.example.project_mobile.storage;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.example.project_mobile.dto.UserInfoResponse;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class PreferenceManager {

    private static final String SETTINGS_PREF = "Settings";
    private static final String LOGIN_PREF = "secure_login_prefs";
    private static final String USER_INFO_PREF = "secure_user_info_prefs";

    private static final String KEY_LANGUAGE = "My_Lang";
    private static final String DEFAULT_LANGUAGE = "en";

    private static final String KEY_USERNAME = "Username";
    private static final String KEY_PASSWORD = "Password";
    private static final String KEY_STATUS = "Status";
    private static final String KEY_TOKEN = "Token";

    private static final String KEY_FULL_NAME = "FullName";
    private static final String KEY_EMAIL = "Email";
    private static final String KEY_PHONE = "Phone";
    private static final String KEY_LICENSE_PLATE = "License_Plate";
    private static final String KEY_AVATAR_URL = "Avatar_Url";
    private static final String KEY_IS_LOADED = "isLoaded";

    // Dark mode
    private static final String KEY_DARK_MODE = "darkMode";

    private final Context context;

    public PreferenceManager(Context context) {
        this.context = context.getApplicationContext();
    }

    /**
     * Lấy SharedPreferences cho Settings (không mã hóa)
     */
    public SharedPreferences getSettings() {
        return context.getSharedPreferences(SETTINGS_PREF, Context.MODE_PRIVATE);
    }

    /**
     * Tạo hoặc lấy EncryptedSharedPreferences cho dữ liệu nhạy cảm
     */
    private SharedPreferences getEncryptedSharedPreferences(String prefName) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            return EncryptedSharedPreferences.create(
                    context,
                    prefName,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error initializing EncryptedSharedPreferences");
        }
    }

    /**
     * Lưu ngôn ngữ (không cần mã hóa)
     */
    public void setLanguage(String language) {
        getSettings().edit().putString(KEY_LANGUAGE, language).apply();
    }

    /**
     * Lấy ngôn ngữ đã lưu
     */
    public String getLanguage() {
        return getSettings().getString(KEY_LANGUAGE, DEFAULT_LANGUAGE);
    }

    /**
     * Lưu thông tin đăng nhập (Sử dụng EncryptedSharedPreferences)
     */
    public void saveLoginDetails(String username, String password, boolean status) {
        SharedPreferences.Editor editor = getEncryptedSharedPreferences(LOGIN_PREF).edit();
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_PASSWORD, password);
        editor.putBoolean(KEY_STATUS, status);
        editor.apply();
    }

    /**
     * Lưu token
     */
    public void saveToken(String token) {
        getEncryptedSharedPreferences(LOGIN_PREF).edit().putString(KEY_TOKEN, token).apply();
    }

    /**
     * Lấy token
     */
    public String getToken() {
        return getEncryptedSharedPreferences(LOGIN_PREF).getString(KEY_TOKEN, null);
    }

    /**
     * Lưu thông tin người dùng
     */
    public void saveUserInfo(UserInfoResponse userInfoResponse) {
        SharedPreferences.Editor editor = getEncryptedSharedPreferences(USER_INFO_PREF).edit();
        editor.putString(KEY_FULL_NAME, userInfoResponse.getFullName());
        editor.putString(KEY_EMAIL, userInfoResponse.getEmail());
        editor.putString(KEY_PHONE, userInfoResponse.getPhone());
        editor.putString(KEY_LICENSE_PLATE, userInfoResponse.getLicensePlate());
        editor.putString(KEY_AVATAR_URL, userInfoResponse.getAvatarUrl());
        editor.putBoolean(KEY_IS_LOADED, true);
        editor.apply();
    }

    /**
     * Lấy thông tin người dùng
     */
    public UserInfoResponse getUserInfo() {
        SharedPreferences prefs = getEncryptedSharedPreferences(USER_INFO_PREF);
        if (!prefs.getBoolean(KEY_IS_LOADED, false)) {
            return null;
        }

        return new UserInfoResponse(
                prefs.getString(KEY_USERNAME, ""),
                prefs.getString(KEY_FULL_NAME, ""),
                prefs.getString(KEY_EMAIL, ""),
                prefs.getString(KEY_PHONE, ""),
                prefs.getString(KEY_LICENSE_PLATE, ""),
                prefs.getString(KEY_AVATAR_URL, "")
        );
    }

    /**
     * Lưu chế độ sáng/tối
     */
    public void setDarkMode(boolean isDark) {
        getSettings().edit().putBoolean(KEY_DARK_MODE, isDark).apply();
    }

    /**
     * Kiểm tra chế độ sáng/tối
     */
    public boolean isDarkMode() {
        return getSettings().getBoolean(KEY_DARK_MODE, false);
    }

    /**
     * Xóa thông tin đăng nhập
     */
    public void clearLoginData() {
        getEncryptedSharedPreferences(LOGIN_PREF).edit().clear().apply();
    }

    /**
     * Xóa thông tin người dùng
     */
    public void clearUserInfo() {
        getEncryptedSharedPreferences(USER_INFO_PREF).edit().clear().apply();
    }

    /**
     * Xóa toàn bộ dữ liệu
     */
    public void clearAllData() {
        clearLoginData();
        clearUserInfo();
        getSettings().edit().clear().apply();
    }
}
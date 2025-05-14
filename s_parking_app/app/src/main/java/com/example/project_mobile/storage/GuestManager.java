package com.example.project_mobile.storage;

import android.content.Context;
import android.content.SharedPreferences;

public class GuestManager {

    private static final String PREF_NAME = "userState";
    private static final String KEY_IS_GUEST = "isGuest";

    public static void setGuestMode(Context context, boolean isGuest) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_IS_GUEST, isGuest).apply();
    }

    public static boolean isGuest(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_IS_GUEST, false);
    }
}

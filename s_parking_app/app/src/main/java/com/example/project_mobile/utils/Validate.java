package com.example.project_mobile.utils;

public class Validate {
    public static boolean isPasswordValid(String password) {
        if (password.length() < 4)
            return false;
        return true;
    }
}

package com.example.project_mobile.utils;

import android.util.Patterns;
import java.util.regex.Pattern;

/**
 * Utility class for data validation.
 * Includes methods to validate license plates, passwords, emails, and phone numbers.
 */
public class Validate {

    /**
     * ========================
     *  BIỂN SỐ XE MÁY
     * ========================
     */

    // Định dạng xe máy cũ: 65-E1-26531
    private static final String BIKE_PATTERN_OLD = "^[0-9]{2}-[A-Z]{1,2}[0-9]-[0-9]{4,5}$";

    // Định dạng xe máy mới: 65-E1 26531
    private static final String BIKE_PATTERN_NEW = "^[0-9]{2}-[A-Z]{1,2}[0-9] [0-9]{4,5}$";

    // Định dạng xe máy mới có dấu chấm: 65-E1 123.45
    private static final String BIKE_PATTERN_DOT = "^[0-9]{2}-[A-Z]{1,2}[0-9] [0-9]{3}\\.[0-9]{2}$";

    /**
     * ========================
     *  BIỂN SỐ Ô TÔ
     * ========================
     */

    // Định dạng ô tô cũ: 30A-12345
    private static final String CAR_PATTERN_OLD = "^[0-9]{2}[A-Z]-[0-9]{4,5}$";

    // Định dạng ô tô mới có dấu chấm: 30A-123.45
    private static final String CAR_PATTERN_DOT = "^[0-9]{2}[A-Z]-[0-9]{3}\\.[0-9]{2}$";

    /**
     * ========================
     *  PHƯƠNG THỨC KIỂM TRA BIỂN SỐ XE
     * ========================
     */

    /**
     * Kiểm tra tính hợp lệ của biển số xe (xe máy và ô tô).
     *
     * @param licensePlate Biển số xe cần kiểm tra
     * @return true nếu biển số hợp lệ, false nếu không hợp lệ
     */
    public static boolean isLicensePlateValid(String licensePlate) {
        if (licensePlate == null) return false;

        // Kiểm tra xe máy
        boolean isBikeValid = Pattern.matches(BIKE_PATTERN_OLD, licensePlate) ||  // 65-E1-26531
                Pattern.matches(BIKE_PATTERN_NEW, licensePlate) ||  // 65-E1 26531
                Pattern.matches(BIKE_PATTERN_DOT, licensePlate);    // 65-E1 123.45

        // Kiểm tra ô tô
        boolean isCarValid = Pattern.matches(CAR_PATTERN_OLD, licensePlate) ||    // 30A-12345
                Pattern.matches(CAR_PATTERN_DOT, licensePlate);      // 30A-123.45

        return isBikeValid || isCarValid;
    }

    /**
     * ========================
     *  MẬT KHẨU
     * ========================
     */

    /**
     * Kiểm tra tính hợp lệ của mật khẩu.
     * - Tối thiểu 6 ký tự
     * - Ít nhất 1 chữ hoa, 1 chữ số, 1 ký tự đặc biệt
     *
     * @param password Mật khẩu cần kiểm tra
     * @return true nếu mật khẩu hợp lệ, false nếu không hợp lệ
     */
    public static boolean isPasswordValid(String password) {
        if (password == null || password.length() < 6) return false;

        boolean hasUpperCase = false;
        boolean hasDigit = false;
        boolean hasSpecialChar = false;

        String specialChars = "!@#$%^&*()-_=+[]{}|;:'\",.<>?/`~";

        for (char ch : password.toCharArray()) {
            if (Character.isUpperCase(ch)) hasUpperCase = true;
            if (Character.isDigit(ch)) hasDigit = true;
            if (specialChars.contains(String.valueOf(ch))) hasSpecialChar = true;
        }

        return hasUpperCase && hasDigit && hasSpecialChar;
    }

    /**
     * ========================
     *  EMAIL
     * ========================
     */

    /**
     * Kiểm tra tính hợp lệ của email sử dụng `Patterns.EMAIL_ADDRESS`.
     *
     * @param email Email cần kiểm tra
     * @return true nếu email hợp lệ, false nếu không hợp lệ
     */
    public static boolean isEmailValid(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * ========================
     *  SỐ ĐIỆN THOẠI
     * ========================
     */

    /**
     * Kiểm tra tính hợp lệ của số điện thoại (10-11 chữ số).
     *
     * @param phoneNumber Số điện thoại cần kiểm tra
     * @return true nếu hợp lệ, false nếu không hợp lệ
     */
    public static boolean isPhoneNumberValid(String phoneNumber) {
        if (phoneNumber == null) return false;
        String phonePattern = "^\\d{10,11}$";
        return Pattern.matches(phonePattern, phoneNumber);
    }
}

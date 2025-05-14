package com.example.project_mobile.utils;

import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class FormatAndCipher {
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");  // Định dạng mong muốn
            return dateTime.format(formatter);  // Chuyển đổi LocalDateTime thành chuỗi
        } else {
            return "Chưa có thời gian";  // Hoặc giá trị mặc định nếu dateTime là null
        }
    }

}

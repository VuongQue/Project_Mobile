package com.example.s_parking.utils;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class CalculateUtil {
    public static float calculateParkingFee(LocalDateTime checkIn, LocalDateTime checkOut) {
        // Nếu quá 21h cùng ngày → 50.000đ
        if (checkOut.toLocalTime().isAfter(LocalTime.of(21, 0)) &&
                checkIn.toLocalDate().equals(checkOut.toLocalDate())) {
            return 50000;
        }

        // Nếu gửi > 12 tiếng → 9.000đ
        long durationInMinutes = Duration.between(checkIn, checkOut).toMinutes();
        if (durationInMinutes > 12 * 60) {
            return 9000;
        }

        // Nếu gửi vào Chủ nhật → 5.000đ
        if (checkIn.getDayOfWeek() == DayOfWeek.SUNDAY) {
            return 5000;
        }

        // Từ Thứ 2 → Thứ 7
        if (checkOut.toLocalTime().isAfter(LocalTime.of(18, 30))) {
            return 5_000;
        } else {
            return 4_000;
        }
    }
}

package com.example.s_parking.service;

import com.example.s_parking.entity.User;

public interface OTPService {
    void sendOTP(User user, String purpose);
    boolean verifyOTP(User user, String code, String purpose);
}

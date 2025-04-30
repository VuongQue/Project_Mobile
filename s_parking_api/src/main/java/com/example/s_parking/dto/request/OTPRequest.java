package com.example.s_parking.dto.request;

import lombok.Getter;
import lombok.Setter;

public class OTPRequest {
    @Setter
    @Getter
    private String username;
    private String otp;

    public String getOTP() {
        return otp;
    }

    public void setOTP(String otp) {
        this.otp = otp;
    }
}

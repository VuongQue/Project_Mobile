package com.example.project_mobile.dto;

public class OTPRequest {
    private String username;
    private String otp;
    private String purpose;

    public OTPRequest(String username, String otp, String purpose) {
        this.username = username;
        this.otp = otp;
        this.purpose = purpose;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }
}

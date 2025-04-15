package com.example.project_mobile.dto;

public class UserInfoResponse {
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private String securityKey;

    public UserInfoResponse() {

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSecurityKey() {
        return securityKey;
    }

    public void setSecurityKey(String securityKey) {
        this.securityKey = securityKey;
    }

    public UserInfoResponse(String username, String fullName, String email, String phone, String securityKey) {
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.securityKey = securityKey;
    }
}

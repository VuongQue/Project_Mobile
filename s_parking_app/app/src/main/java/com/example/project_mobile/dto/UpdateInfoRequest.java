package com.example.project_mobile.dto;

public class UpdateInfoRequest {

    private String username;
    private String fullname;
    private String phone;
    private String password;

    public UpdateInfoRequest() {
    }

    public UpdateInfoRequest(String username, String fullname, String phone, String password) {
        this.username = username;
        this.fullname = fullname;
        this.phone = phone;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

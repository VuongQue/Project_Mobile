package com.example.project_mobile.dto;

public class UpdateAvatarRequest {
    String username;
    String avatarUrl;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public UpdateAvatarRequest(String username, String avatarUrl) {
        this.username = username;
        this.avatarUrl = avatarUrl;
    }
}

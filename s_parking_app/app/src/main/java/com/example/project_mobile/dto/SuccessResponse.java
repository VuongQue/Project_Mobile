package com.example.project_mobile.dto;

public class SuccessResponse {
    boolean status;
    String message;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public SuccessResponse(boolean status, String message) {
        this.status = status;
        this.message = message;
    }
}

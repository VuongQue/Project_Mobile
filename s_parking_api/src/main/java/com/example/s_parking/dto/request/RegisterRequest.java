package com.example.s_parking.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String role; // ví dụ: ROLE_USER hoặc ROLE_ADMIN
}


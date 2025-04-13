package com.example.s_parking.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
public class RegisterRequest {
    public String username;
    public String password;
    public String role; // ví dụ: ROLE_USER hoặc ROLE_ADMIN
}


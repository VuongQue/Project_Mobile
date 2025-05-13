package com.example.s_parking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private String avatarUrl;
}

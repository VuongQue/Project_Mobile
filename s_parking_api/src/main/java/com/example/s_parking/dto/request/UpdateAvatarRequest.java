package com.example.s_parking.dto.request;

import lombok.Data;

@Data
public class UpdateAvatarRequest {
    String username;
    String avatarUrl;
}
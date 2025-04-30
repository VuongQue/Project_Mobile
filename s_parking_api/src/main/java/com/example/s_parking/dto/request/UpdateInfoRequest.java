package com.example.s_parking.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateInfoRequest {
    private String username;
    private String password;
    private String phone;
    private String fullname;
    private String securityKey;
}

package com.example.s_parking.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OTPRequest {
    private String username;
    private String otp;
    private String purpose;
}

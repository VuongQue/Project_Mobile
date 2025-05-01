package com.example.s_parking.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OTPPurposeRequest {
    private String username;
    private String purpose;

}

package com.example.s_parking.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MomoPaymentRequest {
    private String orderId;
    private String amount;
    private String orderInfo;

}

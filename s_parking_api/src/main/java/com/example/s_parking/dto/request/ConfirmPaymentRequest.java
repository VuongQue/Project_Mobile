package com.example.s_parking.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ConfirmPaymentRequest {
    private String transactionId;
    private Long bookingId;
    private List<Long> sessionIds;

    public ConfirmPaymentRequest() {}

    public ConfirmPaymentRequest(String transactionId, Long bookingId, List<Long> sessionIds) {
        this.transactionId = transactionId;
        this.bookingId = bookingId;
        this.sessionIds = sessionIds;
    }

}

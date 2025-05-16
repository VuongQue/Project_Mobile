package com.example.s_parking.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ConfirmPaymentRequest {
    private String transactionId;
    private Long bookingId;
    private List<Long> sessionIds;

    // Thêm trường status để cập nhật trạng thái thanh toán
    private String status;

    public ConfirmPaymentRequest() {}

    public ConfirmPaymentRequest(String transactionId, Long bookingId, List<Long> sessionIds, String status) {
        this.transactionId = transactionId;
        this.bookingId = bookingId;
        this.sessionIds = sessionIds;
        this.status = status;
    }
}

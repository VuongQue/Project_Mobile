package com.example.project_mobile.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ConfirmPaymentRequest {

    @SerializedName("transactionId")
    private String transactionId;

    @SerializedName("bookingId")
    private Long bookingId;

    @SerializedName("sessionIds")
    private List<Long> sessionIds;

    public ConfirmPaymentRequest(String transactionId, Long bookingId, List<Long> sessionIds) {
        this.transactionId = transactionId;
        this.bookingId = bookingId;
        this.sessionIds = sessionIds;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public List<Long> getSessionIds() {
        return sessionIds;
    }
}

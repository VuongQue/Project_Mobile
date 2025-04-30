package com.example.project_mobile.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ConfirmPaymentRequest {
    @SerializedName("transactionId")
    private String transactionId;

    @SerializedName("sessionIds")
    private List<Long> sessionIds;

    public ConfirmPaymentRequest(String transactionId, List<Long> sessionIds) {
        this.transactionId = transactionId;
        this.sessionIds = sessionIds;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public List<Long> getSessionIds() {
        return sessionIds;
    }
}

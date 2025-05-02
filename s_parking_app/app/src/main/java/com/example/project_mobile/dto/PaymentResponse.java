package com.example.project_mobile.dto;

public class PaymentResponse {
    private String transactionId;

    public PaymentResponse() {
    }

    public PaymentResponse(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}

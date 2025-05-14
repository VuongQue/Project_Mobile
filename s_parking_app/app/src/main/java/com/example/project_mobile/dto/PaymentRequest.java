package com.example.project_mobile.dto;

public class PaymentRequest {
    private String amount;
    private String method;
    private String status;
    private String transactionId;
    private String orderInfo;

    public PaymentRequest() {
    }

    public PaymentRequest(String amount, String method, String status, String transactionId, String orderInfo) {
        this.amount = amount;
        this.method = method;
        this.status = status;
        this.transactionId = transactionId;
        this.orderInfo = orderInfo;
    }

    // Getter - Setter
    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getOrderInfo() {
        return orderInfo;
    }

    public void setOrderInfo(String orderInfo) {
        this.orderInfo = orderInfo;
    }
}
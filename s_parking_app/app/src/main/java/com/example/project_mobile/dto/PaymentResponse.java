package com.example.project_mobile.dto;

public class PaymentResponse {
    private String transactionId;
    private double amount;
    private String method;
    private String status;
    private String payUrl;

    public PaymentResponse() {
        this.transactionId = transactionId;
    }

    public PaymentResponse(String transactionId, double amount, String method, String status, String payUrl) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.method = method;
        this.status = status;
        this.payUrl = payUrl;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
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

    public String getPayUrl() {
        return payUrl;
    }

    public void setPayUrl(String payUrl) {
        this.payUrl = payUrl;
    }
}

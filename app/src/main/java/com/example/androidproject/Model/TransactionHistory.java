package com.example.androidproject.Model;

public class TransactionHistory {
    private String transactionId;
    private String transactionCartId;
    private String transactionUserId;
    private String transactionProductId;
    private String transactionTimeStamp;
    private String transactionProductName;
    private String transactionCartQty;
    private String transactionProductPrice;
    private String transactionTotal;
    private boolean notQueue;

    public TransactionHistory(){}
    public TransactionHistory(String transactionId, String transactionUserId,String transactionProductName, String transactionTotal, String transactionTimeStamp, boolean notQueue) {
        this.transactionId = transactionId;
        this.transactionUserId = transactionUserId;
        this.transactionTimeStamp = transactionTimeStamp;
        this.transactionProductName = transactionProductName;
        this.transactionTotal = transactionTotal;
        this.notQueue = notQueue;
    }

    public TransactionHistory(String transactionId, String transactionCartId, String transactionUserId, String transactionProductId, String transactionCartQty, String transactionTotal, String transactionTimeStamp, boolean notQueue) {
        this.transactionId = transactionId;
        this.transactionCartId = transactionCartId;
        this.transactionUserId = transactionUserId;
        this.transactionProductId = transactionProductId;
        this.transactionCartQty = transactionCartQty;
        this.transactionTotal = transactionTotal;
        this.transactionTimeStamp = transactionTimeStamp;
        this.notQueue = notQueue;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getTransactionCartId() {
        return transactionCartId;
    }

    public String getTransactionUserId() {
        return transactionUserId;
    }

    public String getTransactionProductId() {
        return transactionProductId;
    }

    public String getTransactionTimeStamp() {
        return transactionTimeStamp;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public void setTransactionCartId(String transactionCartId) {
        this.transactionCartId = transactionCartId;
    }

    public void setTransactionUserId(String transactionUserId) {
        this.transactionUserId = transactionUserId;
    }

    public void setTransactionProductId(String transactionProductId) {
        this.transactionProductId = transactionProductId;
    }

    public String getTransactionProductName() {
        return transactionProductName;
    }

    public String getTransactionCartQty() {
        return transactionCartQty;
    }

    public String getTransactionProductPrice() {
        return transactionProductPrice;
    }

    public String getTransactionTotal() {
        return transactionTotal;
    }

    public void setTransactionTimeStamp(String transactionTimeStamp) {
        this.transactionTimeStamp = transactionTimeStamp;
    }

    public void setTransactionProductName(String transactionProductName) {
        this.transactionProductName = transactionProductName;
    }

    public void setTransactionCartQty(String transactionCartQty) {
        this.transactionCartQty = transactionCartQty;
    }

    public void setTransactionProductPrice(String transactionProductPrice) {
        this.transactionProductPrice = transactionProductPrice;
    }

    public void setTransactionTotal(String transactionTotal) {
        this.transactionTotal = transactionTotal;
    }

    public boolean isNotQueue() {
        return notQueue;
    }
}

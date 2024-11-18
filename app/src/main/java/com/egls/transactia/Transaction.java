package com.egls.transactia;

import com.google.firebase.Timestamp;

public class Transaction {
    private String transactionId; // New field for the transaction ID
    private String transactionTitle;
    private String senderID;
    private String receiverID;
    private Timestamp timestamp;

    public Transaction() {}

    public Transaction(String transactionId, String transactionTitle, String senderID, String receiverID, Timestamp timestamp) {
        this.transactionId = transactionId;
        this.transactionTitle = transactionTitle;
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.timestamp = timestamp;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionTitle() {
        return transactionTitle;
    }

    public void setTransactionTitle(String transactionTitle) {
        this.transactionTitle = transactionTitle;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(String receiverID) {
        this.receiverID = receiverID;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}

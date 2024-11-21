package com.egls.transactia;

import com.google.firebase.Timestamp;

public class Transaction {
    private String transactionid; // New field for the transaction ID
    private String transactionTitle;
    private String senderID;
    private String receiverID;
    private Timestamp timestamp;
    private String status;

    public Transaction() {}

    public Transaction(String transactionid, String transactionTitle, String senderID, String receiverID, Timestamp timestamp, String status) {
        this.transactionid = transactionid;
        this.transactionTitle = transactionTitle;
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.timestamp = timestamp;
        this.status = status;
    }

    public String getTransactionid() {
        return transactionid;
    }

    public void setTransactionid(String transactionid) {
        this.transactionid = transactionid;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

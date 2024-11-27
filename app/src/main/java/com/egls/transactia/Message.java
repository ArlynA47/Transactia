package com.egls.transactia;

public class Message {
    private String senderId;
    private String receiverId;
    private String messageText;
    private com.google.firebase.Timestamp timestamp;

    public Message() {
        // Required for Firestore
    }

    public Message(String senderId, String receiverId, String messageText, com.google.firebase.Timestamp timestamp) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageText = messageText;
        this.timestamp = timestamp;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public String getMessageText() {
        return messageText;
    }

    public com.google.firebase.Timestamp getTimestamp() {
        return timestamp;
    }
}


package com.egls.transactia;

import com.google.firebase.Timestamp;

public class Notification {
    private String title;
    private String message;
    private String status;
    private Timestamp timestamp;
    private String userId;
    private String documentId; // For Firestore document ID

    // Empty constructor for Firestore deserialization
    public Notification() {}

    public Notification(String title, String message, String status, Timestamp timestamp, String userId) {
        this.title = title;
        this.message = message;
        this.status = status;
        this.timestamp = timestamp;
        this.userId = userId;
    }

    // Getters and setters
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getUserId() {
        return userId;
    }
}


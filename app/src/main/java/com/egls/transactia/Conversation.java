package com.egls.transactia;

import com.google.firebase.Timestamp;

import java.util.List;

public class Conversation {
    private String conversationId;
    private String lastMessage;
    private Timestamp timestamp;
    private List<String> participants;

    // Default constructor for Firestore
    public Conversation() {
    }

    public Conversation(String conversationId, String lastMessage, Timestamp timestamp, List<String> participants) {
        this.conversationId = conversationId;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
        this.participants = participants;
    }

    // Getters and Setters
    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }
}

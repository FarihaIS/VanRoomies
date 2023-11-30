package com.chads.vanroomies;

import java.io.Serializable;

// ChatGPT Usage: No
public class ChatMessage implements Serializable {
    private String sender;
    private String message;
    private long timestamp;

    public ChatMessage(String sender, String message, long timestamp) {
        this.sender = sender;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getChatSender() { return sender; }

    public String getChatMessage() { return message; }

    public long getChatTimestamp() { return timestamp; }

    public void setChatSender(String sender) { this.sender = sender; }

    public void setChatMessage(String message) { this.message = message; }

    public void setChatTimestamp(long timestamp) { this.timestamp = timestamp; }
}
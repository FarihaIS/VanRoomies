package com.chads.vanroomies;

public class ChatMessage {
    private String chatMessageUser;
    private String chatMessageText;
    private long chatMessageTimestamp;

    public ChatMessage(String user, String text, long timestamp) {
        this.chatMessageUser = user;
        this.chatMessageText = text;
        this.chatMessageTimestamp = timestamp;
    }

    public String getChatUser() { return chatMessageUser; }

    public String getChatText() { return chatMessageText; }

    public long getChatTimestamp() { return chatMessageTimestamp; }

    public void setChatUser(String user) { this.chatMessageUser = user; }

    public void setChatText(String text) { this.chatMessageText = text; }

    public void setChatTimestamp(long timestamp) { this.chatMessageTimestamp = timestamp; }
}

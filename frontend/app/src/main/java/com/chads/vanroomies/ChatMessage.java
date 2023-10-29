package com.chads.vanroomies;

public class ChatMessage {
    private String chatUser;
    private String chatText;
    private long chatTimestamp;

    public ChatMessage(String user, String text, long timestamp) {
        this.chatUser = user;
        this.chatText = text;
        this.chatTimestamp = timestamp;
    }

    public String getChatUser() { return chatUser; }

    public String getChatText() { return chatText; }

    public long getChatTimestamp() { return chatTimestamp; }

    public void setChatUser(String user) { this.chatUser = user; }

    public void setChatText(String text) { this.chatText = text; }

    public void setChatTimestamp(long timestamp) { this.chatTimestamp = timestamp; }
}

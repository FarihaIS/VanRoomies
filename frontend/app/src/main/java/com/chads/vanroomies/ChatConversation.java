package com.chads.vanroomies;

import java.util.ArrayList;
import java.util.List;

public class ChatConversation {
    private String _id;
    private List<String> users;
    private ArrayList<ChatMessage> messages;
    private String __v;

    public ChatConversation(String _id, List<String> users, ArrayList<ChatMessage> messages, String __v) {
        this._id = _id;
        this.users = users;
        this.messages = messages;
        this.__v = __v;
    }

    public String get_id() { return _id; }

    public List<String> getUsers() { return users; }

    public ArrayList<ChatMessage> getMessages() { return messages; }

    public String get__v() { return __v; }

    public void set_id(String __id) { this._id = __id; }

    public void setUsers(List<String> users) { this.users = users; }

    public void setMessages(ArrayList<ChatMessage> messages) { this.messages = messages; }

    public void set__v(String __v) { this.__v = __v; }
}
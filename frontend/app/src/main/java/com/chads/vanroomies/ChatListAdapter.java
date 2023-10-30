package com.chads.vanroomies;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Map;

public class ChatListAdapter extends RecyclerView.Adapter {
    private Context chatListContext;
    private ArrayList<Map.Entry<UserProfile, ArrayList<ChatMessage>>> chatListMessages;
    private String chatListUserId;

    public ChatListAdapter(Context context, Map<UserProfile, ArrayList<ChatMessage>> messages, String userId) {
        this.chatListContext = context;
        this.chatListMessages = new ArrayList<>(messages.entrySet());
        this.chatListUserId = userId;
    }

    @Override
    public int getItemCount() { return chatListMessages.size(); }

    @Override
    public ChatListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(chatListContext).inflate(R.layout.row_chat, parent, false);
        return new ChatListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        Map.Entry<UserProfile, ArrayList<ChatMessage>> entry = chatListMessages.get(position);
        UserProfile otherUserProfile = entry.getKey();
        ArrayList<ChatMessage> otherUserMessages = entry.getValue();

        String otherUserName = otherUserProfile.getUserProfileName();
        int otherUserImage = otherUserProfile.getUserProfileImageId();

        ((ChatListHolder) holder).name.setText(otherUserName);
        ((ChatListHolder) holder).image.setImageResource(otherUserImage);

        holder.itemView.setOnClickListener(v -> {
            Intent chatChannelIntent = new Intent(chatListContext, ChatChannelActivity.class);
            chatChannelIntent.putExtra("thisUserId", chatListUserId);
            chatChannelIntent.putExtra("otherUserProfile", otherUserProfile);
            chatChannelIntent.putExtra("otherUserMessages", otherUserMessages);
            chatListContext.startActivity(chatChannelIntent);
        });
    }

    public static class ChatListHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name;

        ChatListHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.chat_row_image);
            name = itemView.findViewById(R.id.chat_row_name);
        }
    }
}


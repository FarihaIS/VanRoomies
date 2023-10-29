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

public class ChatListAdapter extends RecyclerView.Adapter {

    private Context chatListContext;
    private ArrayList<UserProfile> chatListUsers;
    private String chatListUserId;

    public ChatListAdapter(Context context, ArrayList<UserProfile> users, String userId) {
        this.chatListContext = context;
        this.chatListUsers = users;
        this.chatListUserId = userId;
    }

    @Override
    public int getItemCount() {
        return chatListUsers.size();
    }

    @Override
    public ChatListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(chatListContext).inflate(R.layout.row_chat, parent, false);
        return new ChatListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        String otherUserId = chatListUsers.get(position).getUserProfileId();
        String otherUserName = chatListUsers.get(position).getUserProfileName();
        int otherUserImage = chatListUsers.get(position).getUserProfileImageId();

        ((ChatListHolder) holder).name.setText(otherUserName);
        ((ChatListHolder) holder).image.setImageResource(otherUserImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chatChannelIntent = new Intent(chatListContext, ChatChannelActivity.class);
                chatChannelIntent.putExtra("myUserId", chatListUserId);
                chatChannelIntent.putExtra("otherUserId", otherUserId);
                chatListContext.startActivity(chatChannelIntent);
            }
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


package com.chads.vanroomies;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Map;

public class ChatListAdapter extends RecyclerView.Adapter {
    private final Context context;
    private final ArrayList<Map.Entry<UserProfile, ArrayList<ChatMessage>>> chats;
    // TODO: If defined somewhere else, can get rid of userId
    private final String userId;

    public ChatListAdapter(Context context, Map<UserProfile, ArrayList<ChatMessage>> chats, String userId) {
        this.context = context;
        this.chats = new ArrayList<>(chats.entrySet());
        this.userId = userId;
    }

    @Override
    public int getItemCount() { return chats.size(); }

    @Override
    public ChatListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_chat, parent, false);
        return new ChatListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        Map.Entry<UserProfile, ArrayList<ChatMessage>> entry = chats.get(position);
        UserProfile otherUserProfile = entry.getKey();
        ArrayList<ChatMessage> otherUserMessages = entry.getValue();

        String otherUserName = otherUserProfile.getFirstName();
        if (otherUserName.isEmpty()) {
            ((ChatListHolder) holder).nameView.setText(R.string.default_name);
        }
        else {
            String fullName = otherUserProfile.getFirstName() + " " + otherUserProfile.getLastName();
            ((ChatListHolder) holder).nameView.setText(fullName);
        }

        String otherUserImage = otherUserProfile.getProfilePicture();
        if (otherUserImage.isEmpty()) {
            ((ChatListHolder) holder).imageView.setImageResource(R.drawable.ic_profile);
        }
        else {
            byte[] decodedString = Base64.decode(otherUserImage, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            ((ChatListHolder) holder).imageView.setImageBitmap(decodedByte);
        }

        ((ChatListHolder) holder).deleteButton.setOnClickListener(v -> {
            // TODO: Add delete chat functionality
        });

        holder.itemView.setOnClickListener(v -> {
            Intent chatChannelIntent = new Intent(context, ChatChannelActivity.class);
            chatChannelIntent.putExtra("thisUserId", userId);
            chatChannelIntent.putExtra("otherUserProfile", otherUserProfile);
            chatChannelIntent.putExtra("otherUserMessages", otherUserMessages);
            context.startActivity(chatChannelIntent);
        });
    }

    public static class ChatListHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nameView;
        Button deleteButton;

        ChatListHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.chat_row_image);
            nameView = itemView.findViewById(R.id.chat_row_name);
            deleteButton = itemView.findViewById(R.id.chat_row_delete);
        }
    }
}

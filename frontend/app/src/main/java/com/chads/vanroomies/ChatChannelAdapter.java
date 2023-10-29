package com.chads.vanroomies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatChannelAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 0;
    private Context chatChannelContext;
    final private ArrayList<ChatMessage> chatChannelList;
    final private String chatChannelUserId;

    public ChatChannelAdapter(Context context, ArrayList<ChatMessage> messageList, String userId) {
        chatChannelContext = context;
        chatChannelList = messageList;
        chatChannelUserId = userId;
    }

    @Override
    public int getItemCount() {
        return chatChannelList.size();
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = (ChatMessage) chatChannelList.get(position);
        if (message.getChatUser().equals(chatChannelUserId)) {
            return VIEW_TYPE_MESSAGE_SENT;
        }
        else {
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    @Override
    public MessageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(chatChannelContext)
                    .inflate(R.layout.item_chat_me, parent, false);
            return new MessageHolder(view, true);
        }
        else {
            view = LayoutInflater.from(chatChannelContext)
                    .inflate(R.layout.item_chat_other, parent, false);
            return new MessageHolder(view, false);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = (ChatMessage) chatChannelList.get(position);
        ((MessageHolder) holder).bind(message);
    }

    private static class MessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, dateText, timeText;

        MessageHolder(View itemView, boolean isSent) {
            super(itemView);
            if (isSent) {
                messageText = (TextView) itemView.findViewById(R.id.chat_me_text);
                dateText = (TextView) itemView.findViewById(R.id.chat_me_date);
                timeText = (TextView) itemView.findViewById(R.id.chat_me_timestamp);
            }
            else {
                messageText = (TextView) itemView.findViewById(R.id.chat_other_text);
                dateText = (TextView) itemView.findViewById(R.id.chat_other_date);
                timeText = (TextView) itemView.findViewById(R.id.chat_other_timestamp);
            }
        }

        void bind(ChatMessage message) {
            SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMM d");
            SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");
            Date timestamp = new Date(message.getChatTimestamp());
            messageText.setText(message.getChatText());
            dateText.setText(dateFormatter.format(timestamp));
            timeText.setText(timeFormatter.format(timestamp));
        }
    }
}
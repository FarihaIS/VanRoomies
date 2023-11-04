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
import java.util.Locale;

public class ChatChannelAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 0;
    private final Context context;
    final private ArrayList<ChatMessage> messages;
    final private String userId;

    public ChatChannelAdapter(Context context, ArrayList<ChatMessage> messages, String userId) {
        this.context = context;
        this.messages = messages;
        this.userId = userId;
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = messages.get(position);
        if (message.getChatSender().equals(userId)) {
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
            view = LayoutInflater.from(context)
                    .inflate(R.layout.item_chat_me, parent, false);
            return new MessageHolder(view, true);
        }
        else {
            view = LayoutInflater.from(context)
                    .inflate(R.layout.item_chat_other, parent, false);
            return new MessageHolder(view, false);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        ((MessageHolder) holder).bind(message);
    }

    private static class MessageHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        TextView dateText;
        TextView timeText;

        MessageHolder(View itemView, boolean isSent) {
            super(itemView);

            if (isSent) {
                messageText = itemView.findViewById(R.id.chat_me_text);
                dateText = itemView.findViewById(R.id.chat_me_date);
                timeText = itemView.findViewById(R.id.chat_me_timestamp);
            }
            else {
                messageText = itemView.findViewById(R.id.chat_other_text);
                dateText = itemView.findViewById(R.id.chat_other_date);
                timeText = itemView.findViewById(R.id.chat_other_timestamp);
            }
        }

        void bind(ChatMessage message) {
            SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMM d", Locale.getDefault());
            SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date timestamp = new Date(message.getChatTimestamp());
            messageText.setText(message.getChatMessage());
            dateText.setText(dateFormatter.format(timestamp));
            timeText.setText(timeFormatter.format(timestamp));
        }
    }
}
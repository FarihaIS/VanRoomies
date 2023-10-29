package com.chads.vanroomies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatListAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 0;
    private Context mContext;
    final private ArrayList<ChatMessage> mMessageList;
    final private String mUserId;
    public ChatListAdapter(Context context, ArrayList<ChatMessage> messageList, String userId) {
        mContext = context;
        mMessageList = messageList;
        mUserId = userId;
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = (ChatMessage) mMessageList.get(position);
        if (message.getChatUser().equals(mUserId)) {
            return VIEW_TYPE_MESSAGE_SENT;
        }
        else {
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }
    // Inflates the appropriate layout according to the ViewType.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_me, parent, false);
            return new MessageHolder(view, true);
        }
        else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_other, parent, false);
            return new MessageHolder(view, false);
        }
    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = (ChatMessage) mMessageList.get(position);
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
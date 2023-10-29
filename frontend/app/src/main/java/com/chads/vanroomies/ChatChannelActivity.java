package com.chads.vanroomies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.Calendar;

public class ChatChannelActivity extends AppCompatActivity {
    final static String TAG = "ChatChannelActivity";
    private RecyclerView mChatRecycler;
    private ChatListAdapter mChatAdapter;
    private ArrayList<ChatMessage> mChatList;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_channel);

        Intent chatIntent = getIntent();
        userId = chatIntent.getStringExtra("userId");
        mChatList = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2023);
        calendar.set(Calendar.MONTH, Calendar.OCTOBER); // October is 9 in Calendar
        calendar.set(Calendar.DAY_OF_MONTH, 28);
        calendar.set(Calendar.HOUR_OF_DAY, 13);
        calendar.set(Calendar.MINUTE, 5);
        calendar.set(Calendar.SECOND, 0);

        // TODO: Get chatList from backend
        mChatList.add(new ChatMessage(userId, "Hey! What's up?", calendar.getTimeInMillis()));
        calendar.set(Calendar.MINUTE, 5);
        mChatList.add(new ChatMessage("vbndbnb72", "Nothing much, wby?", calendar.getTimeInMillis()));
        calendar.set(Calendar.MINUTE, 7);
        mChatList.add(new ChatMessage(userId, "Eatin cheetos, yk how it is", calendar.getTimeInMillis()));
        calendar.set(Calendar.MINUTE, 8);
        mChatList.add(new ChatMessage(userId, "Wanna come over????", calendar.getTimeInMillis()));
        calendar.set(Calendar.MINUTE, 10);
        mChatList.add(new ChatMessage("vbndbnb72", "Yoo let's do it", calendar.getTimeInMillis()));
        calendar.set(Calendar.MINUTE, 15);
        mChatList.add(new ChatMessage("vbndbnb72", "What time should I come over btw?", calendar.getTimeInMillis()));
        mChatList.add(new ChatMessage("vbndbnb72", "You free now?", calendar.getTimeInMillis()));
        calendar.set(Calendar.MINUTE, 16);
        mChatList.add(new ChatMessage(userId, "Yuhh pull up bro", calendar.getTimeInMillis()));

        mChatRecycler = (RecyclerView) findViewById(R.id.recycler_chat);
        mChatAdapter = new ChatListAdapter(this, mChatList, userId);
        mChatRecycler.setLayoutManager(new LinearLayoutManager(this));
        mChatRecycler.setAdapter(mChatAdapter);
    }
}
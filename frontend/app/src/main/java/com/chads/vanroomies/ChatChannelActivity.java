package com.chads.vanroomies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class ChatChannelActivity extends AppCompatActivity {
    final static String TAG = "ChatChannelActivity";
    private RecyclerView chatChannelRecycler;
    private ChatChannelAdapter chatChannelAdapter;
    private ArrayList<ChatMessage> chatMessages;
    private TextView chatChannelText;
    private Button chatChannelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_channel);

        Intent chatIntent = getIntent();
        String myUserId = chatIntent.getStringExtra("myUserId");
        String otherUserId = chatIntent.getStringExtra("otherUserId");
        chatMessages = new ArrayList<>();

        chatChannelText = findViewById(R.id.edit_chat_message);
        chatChannelButton = findViewById(R.id.chat_send_button);
        chatChannelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = chatChannelText.getText().toString().trim();
                if (TextUtils.isEmpty(message)) {//if empty
                    Toast.makeText(ChatChannelActivity.this, "Please Write Something Here", Toast.LENGTH_LONG).show();
                } else {
                    // TODO: PUT request to send message
                }
                chatChannelText.setText("");
            }
        });

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2023);
        calendar.set(Calendar.MONTH, Calendar.OCTOBER); // October is 9 in Calendar
        calendar.set(Calendar.DAY_OF_MONTH, 28);
        calendar.set(Calendar.HOUR_OF_DAY, 13);
        calendar.set(Calendar.MINUTE, 5);
        calendar.set(Calendar.SECOND, 0);

        // TODO: Get chatList from backend using myUserId and otherUserId
        chatMessages.add(new ChatMessage(myUserId, "Hey! What's up?", calendar.getTimeInMillis()));
        calendar.set(Calendar.MINUTE, 5);
        chatMessages.add(new ChatMessage(otherUserId, "Nothing much, wby?", calendar.getTimeInMillis()));
        calendar.set(Calendar.MINUTE, 7);
        chatMessages.add(new ChatMessage(myUserId, "Eatin cheetos, yk how it is", calendar.getTimeInMillis()));
        calendar.set(Calendar.MINUTE, 8);
        chatMessages.add(new ChatMessage(myUserId, "Wanna come over????", calendar.getTimeInMillis()));
        calendar.set(Calendar.MINUTE, 10);
        chatMessages.add(new ChatMessage(otherUserId, "Yoo let's do it", calendar.getTimeInMillis()));
        calendar.set(Calendar.MINUTE, 15);
        chatMessages.add(new ChatMessage(otherUserId, "What time should I come over btw?", calendar.getTimeInMillis()));
        chatMessages.add(new ChatMessage(otherUserId, "You free now?", calendar.getTimeInMillis()));
        calendar.set(Calendar.MINUTE, 16);
        chatMessages.add(new ChatMessage(myUserId, "Yuhh pull up bro", calendar.getTimeInMillis()));

        chatChannelRecycler = (RecyclerView) findViewById(R.id.recycler_chat);
        chatChannelAdapter = new ChatChannelAdapter(this, chatMessages, myUserId);
        chatChannelRecycler.setLayoutManager(new LinearLayoutManager(this));
        chatChannelRecycler.setAdapter(chatChannelAdapter);
    }
}
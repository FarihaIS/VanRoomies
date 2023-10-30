package com.chads.vanroomies;

// Reference: https://sendbird.com/developer/tutorials/android-chat-tutorial-building-a-messaging-ui
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import io.socket.client.IO;
import io.socket.client.Socket;

public class ChatChannelActivity extends AppCompatActivity {
    final static String TAG = "ChatChannelActivity";
    private ChatChannelAdapter chatChannelAdapter;
    private String thisUserId;
    private UserProfile chatUser;
    private ArrayList<ChatMessage> chatMessages;
    private RecyclerView chatChannelRecycler;
    private ImageView chatChannelImage;
    private TextView chatChannelName;
    private TextView chatChannelText;
    private Button chatChannelButton;
    private Socket chatSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_channel);

        Intent chatIntent = getIntent();
        thisUserId = chatIntent.getStringExtra("thisUserId");
        chatUser = (UserProfile) chatIntent.getSerializableExtra("otherUserProfile");
        chatMessages = (ArrayList<ChatMessage>) chatIntent.getSerializableExtra("otherUserMessages");

        try {
            chatSocket = IO.socket(Constants.localBaseServerURL);
            chatSocket.connect();
        } catch (URISyntaxException e) {
            Log.d(TAG, Log.getStackTraceString(e));
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2023);
        calendar.set(Calendar.MONTH, Calendar.OCTOBER); // October is 9 in Calendar
        calendar.set(Calendar.DAY_OF_MONTH, 28);
        calendar.set(Calendar.HOUR_OF_DAY, 13);
        calendar.set(Calendar.MINUTE, 5);
        calendar.set(Calendar.SECOND, 0);

        chatChannelRecycler = findViewById(R.id.recycler_chat);
        chatChannelRecycler.setLayoutManager(new LinearLayoutManager(this));

        chatChannelImage = findViewById(R.id.chat_image);
        chatChannelImage.setImageResource(chatUser.getUserProfileImageId());

        chatChannelName = findViewById(R.id.chat_name);
        chatChannelName.setText(chatUser.getUserProfileName());

        chatChannelText = findViewById(R.id.edit_chat_message);
        chatChannelButton = findViewById(R.id.chat_send_button);

        chatChannelButton.setOnClickListener(v -> {
            String message = chatChannelText.getText().toString().trim();
            if (TextUtils.isEmpty(message)) {//if empty
                Toast.makeText(ChatChannelActivity.this, "Please Write Something Here", Toast.LENGTH_LONG).show();
            } else {
                // TODO: PUT request to send message
                Log.d(TAG, "Adding new message");
                ChatMessage newMessage = new ChatMessage(thisUserId, message, System.currentTimeMillis());
                chatSocket.emit("private message", newMessage);
                chatMessages.add(newMessage);
                chatChannelAdapter = new ChatChannelAdapter(ChatChannelActivity.this, chatMessages, thisUserId);
                chatChannelRecycler.setAdapter(chatChannelAdapter);
                chatChannelRecycler.scrollToPosition(chatMessages.size() - 1);
            }
            chatChannelText.setText("");
        });

        chatChannelAdapter = new ChatChannelAdapter(this, chatMessages, thisUserId);
        chatChannelRecycler.setAdapter(chatChannelAdapter);
        chatChannelRecycler.scrollToPosition(chatMessages.size() - 1);
    }
}
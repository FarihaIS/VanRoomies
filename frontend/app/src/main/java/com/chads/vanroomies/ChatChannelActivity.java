package com.chads.vanroomies;

// Reference: https://sendbird.com/developer/tutorials/android-chat-tutorial-building-a-messaging-ui
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import de.hdodenhof.circleimageview.CircleImageView;
import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

// ChatGPT Usage: No
public class ChatChannelActivity extends AppCompatActivity {
    final static String TAG = "ChatChannelActivity";
    private ChatChannelAdapter chatChannelAdapter;
    private String thisUserId;
    private UserProfile chatUser;
    private ArrayList<ChatMessage> chatMessages;
    private RecyclerView chatChannelRecycler;
    private TextView chatChannelText;
    private Socket chatSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_chat_channel);

        // Retrieve UserProfile and list of ChatMessages from ChatListAdapter
        Intent chatIntent = getIntent();
        thisUserId = chatIntent.getStringExtra("thisUserId");
        chatUser = (UserProfile) chatIntent.getParcelableExtra("otherUserProfile");
        chatMessages = (ArrayList<ChatMessage>) chatIntent.getSerializableExtra("otherUserMessages");

        setUpChatSocket();
        setUpChatChannelLayout();
        setUpChatChannelButton();
        updateChatChannelLayout();
        chatSocketListen();
    }

    private void chatSocketListen() {
        chatSocket.on(Constants.privateMessageEvent, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject payload = (JSONObject) args[0];
                ChatChannelActivity.this.runOnUiThread(() -> {
                    try {
                        String from = payload.getString("from");
                        String content = payload.getString("content");

                        if (from.equals(chatUser.get_id())) {
                            ChatMessage thisMessage = new ChatMessage(from, content, System.currentTimeMillis());
                            chatMessages.add(thisMessage);
                            updateChatChannelLayout();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    private void updateChatChannelLayout() {
        chatChannelAdapter = new ChatChannelAdapter(this, chatMessages, thisUserId);
        chatChannelRecycler.setAdapter(chatChannelAdapter);
        chatChannelRecycler.scrollToPosition(chatMessages.size() - 1);
    }

    private void chatSocketEmitEvent(String message) {
        JSONObject messageObj = new JSONObject();
        try {
            messageObj.put("content", message);
            messageObj.put("to", chatUser.get_id());
        } catch (JSONException e) {
            Log.d(TAG, Log.getStackTraceString(e));
        }
        chatSocket.emit(Constants.privateMessageEvent, messageObj, (Ack) args -> {
            JSONObject response = (JSONObject) args[0];
            try {
                Log.d(TAG, "Emit event response code is " + response.getString("status"));
            }
            catch (JSONException e) {
                Log.d(TAG, Log.getStackTraceString(e));
            }
        });
    }

    private void setUpChatChannelButton() {
        Button chatChannelButton;
        chatChannelButton = findViewById(R.id.chat_send_button);
        chatChannelButton.setOnClickListener(v -> {
            String message = chatChannelText.getText().toString().trim();
            if (message.isEmpty()) {
                Toast.makeText(ChatChannelActivity.this, R.string.invalid_input_message, Toast.LENGTH_LONG).show();
            }
            else {
                ChatMessage newMessage = new ChatMessage(thisUserId, message, System.currentTimeMillis());
                chatMessages.add(newMessage);
                chatSocketEmitEvent(message);

                chatChannelAdapter = new ChatChannelAdapter(ChatChannelActivity.this, chatMessages, thisUserId);
                chatChannelRecycler.setAdapter(chatChannelAdapter);
                chatChannelRecycler.scrollToPosition(chatMessages.size() - 1);
            }
            chatChannelText.setText("");
        });
    }

    private void setUpChatChannelLayout() {
        CircleImageView chatChannelImage;
        TextView chatChannelName;
        chatChannelRecycler = findViewById(R.id.recycler_chat);
        chatChannelRecycler.setLayoutManager(new LinearLayoutManager(this));

        chatChannelImage = findViewById(R.id.chat_image);
        if (chatUser.getProfilePicture() != null) {
            String imageString = chatUser.getProfilePicture().toString().matches(Constants.base64Regex)
                    ? chatUser.getProfilePicture().toString() : "";
            byte[] decodedString = Base64.decode(imageString, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            chatChannelImage.setImageBitmap(decodedByte);
        }
        else {
            chatChannelImage.setImageResource(R.drawable.ic_profile);
        }

        chatChannelName = findViewById(R.id.chat_name);
        if (chatUser.getFirstName().isEmpty()) {
            chatChannelName.setText(R.string.default_name);
        }
        else {
            String fullName = chatUser.getFirstName() + " " + chatUser.getLastName();
            chatChannelName.setText(fullName);
        }

        chatChannelText = findViewById(R.id.edit_chat_message);
    }

    private void setUpChatSocket() {
        Map<String, String> userIdMap = new HashMap<>();
        userIdMap.put("userId", thisUserId);
        IO.Options socketOptions = IO.Options.builder().setAuth(userIdMap).build();

        try {
            chatSocket = IO.socket(Constants.baseServerURL, socketOptions);
            chatSocket.connect();
        } catch (URISyntaxException e) {
            Log.d(TAG, Log.getStackTraceString(e));
        }
    }
}
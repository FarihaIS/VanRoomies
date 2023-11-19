package com.chads.vanroomies;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatListAdapter extends RecyclerView.Adapter {
    final static String TAG = "ChatListAdapter";
    private final Context context;
    private final ArrayList<Map.Entry<UserProfile, ArrayList<ChatMessage>>> chats;
    private final String userId;
    private OkHttpClient httpClient;
    private FragmentActivity fragmentActivity;

    public ChatListAdapter(Context context, Map<UserProfile, ArrayList<ChatMessage>> chats, String userId, OkHttpClient httpClient, FragmentActivity fragmentActivity) {
        this.context = context;
        this.chats = new ArrayList<>(chats.entrySet());
        this.userId = userId;
        this.httpClient = httpClient;
        this.fragmentActivity = fragmentActivity;
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
        if (otherUserImage != null) {
            String imageString = otherUserImage.matches(Constants.base64Regex)
                    ? otherUserImage : "";
            byte[] decodedString = Base64.decode(imageString, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            ((ChatListHolder) holder).imageView.setImageBitmap(decodedByte);
        }
        else {
            ((ChatListHolder) holder).imageView.setImageResource(R.drawable.ic_profile);
        }
        
        ((ChatListHolder) holder).unmatchView.setOnClickListener(v -> showUnmatchUserAlert(entry));
        ((ChatListHolder) holder).blockView.setOnClickListener(v -> showBlockUserAlert(entry));

        holder.itemView.setOnClickListener(v -> {
            Intent chatChannelIntent = new Intent(context, ChatChannelActivity.class);
            chatChannelIntent.putExtra("thisUserId", userId);
            chatChannelIntent.putExtra("otherUserProfile", otherUserProfile);
            chatChannelIntent.putExtra("otherUserMessages", otherUserMessages);
            context.startActivity(chatChannelIntent);
        });
    }

    private void showUnmatchUserAlert(Map.Entry<UserProfile, ArrayList<ChatMessage>> entry) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Are you sure you want to unmatch with " + entry.getKey().getFirstName() + " " + entry.getKey().getLastName() + "? You will lose your chat with them permanently.")
                .setPositiveButton("Yes", (dialog, which) -> {
                    Log.d(TAG, "Sending unmatch user request");
                    sendUnmatchUserRequest(httpClient, fragmentActivity, entry);
                })
                .setNegativeButton("No", (dialog, which) -> {
                    Log.d(TAG, "Dismissing dialog for unmatch user");
                    dialog.dismiss();
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void sendUnmatchUserRequest(OkHttpClient httpClient, FragmentActivity fragmentActivity, Map.Entry<UserProfile, ArrayList<ChatMessage>> entry) {
        String url = Constants.baseServerURL + Constants.userEndpoint + userId + Constants.unmatchByUserIdEndpoint;
        Log.d(TAG, "Unmatch with userId " + entry.getKey().get_id());
        RequestBody requestBody = new FormBody.Builder()
                .add("unmatchedId", entry.getKey().get_id())
                .build();
        Request request = new Request.Builder()
                .url(url)
                .put(requestBody)
                .build();
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d(TAG, e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                fragmentActivity.runOnUiThread(() -> {
                    try {
                        String responseData = response.body().string();
                        if (response.body() == null) {
                            Log.d(TAG, "responseData in sendUnmatchUserRequest is null");
                        } else {
                            Log.d(TAG, "responseData in sendUnmatchUserRequest is " + responseData);
                            chats.remove(entry);
                            notifyDataSetChanged();
                        }
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    private void showBlockUserAlert(Map.Entry<UserProfile, ArrayList<ChatMessage>> entry) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Are you sure you want to block " + entry.getKey().getFirstName() + " " + entry.getKey().getLastName() + " permanently?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    Log.d(TAG, "Sending block user request");
                    sendBlockUserRequest(httpClient, fragmentActivity, entry);
                })
                .setNegativeButton("No", (dialog, which) -> {
                    Log.d(TAG, "Dismissing dialog for block user");
                    dialog.dismiss();
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void sendBlockUserRequest(OkHttpClient httpClient, FragmentActivity fragmentActivity, Map.Entry<UserProfile, ArrayList<ChatMessage>> entry) {
        String url = Constants.baseServerURL + Constants.userEndpoint + userId + Constants.blockByUserIdEndpoint;
        Log.d(TAG, "Block userId " + entry.getKey().get_id());
        RequestBody requestBody = new FormBody.Builder()
                .add("blockedId", entry.getKey().get_id())
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d(TAG, e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                fragmentActivity.runOnUiThread(() -> {
                    try {
                        String responseData = response.body().string();
                        if (response.body() == null) {
                            Log.d(TAG, "responseData in sendBlockUserRequest is null");
                        } else {
                            Log.d(TAG, "responseData in sendBlockUserRequest is " + responseData);
                            chats.remove(entry);
                            notifyDataSetChanged();
                        }
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    public static class ChatListHolder extends RecyclerView.ViewHolder {
        CircleImageView imageView;
        TextView nameView;
        ImageButton unmatchView;
        ImageButton blockView;

        ChatListHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.chat_row_image);
            nameView = itemView.findViewById(R.id.chat_row_name);
            unmatchView = itemView.findViewById(R.id.chat_row_unmatch);
            blockView = itemView.findViewById(R.id.chat_row_block);
        }
    }
}

package com.chads.vanroomies;

// Reference: https://www.geeksforgeeks.org/how-to-implement-chat-functionality-in-social-media-android-app/
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

// ChatGPT Usage: Partial
public class ChatFragment extends Fragment {
    final static String TAG = "ChatFragment";
    private RecyclerView chatListRecycler;
    private String thisUserId;
    private OkHttpClient httpClient;
    private Gson gson;
    private LinkedHashMap<UserProfile, ArrayList<ChatMessage>> allChatMessages;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chat, container, false);

        SharedPreferences sharedPref = getActivity().getSharedPreferences(Constants.userData, Context.MODE_PRIVATE);
        thisUserId = sharedPref.getString(Constants.userIdKey, Constants.userDefault);
        Log.d(TAG, sharedPref.getString(Constants.userIdKey, Constants.userDefault));

        gson = new Gson();
        httpClient = HTTPSClientFactory.createClient(getActivity().getApplication());
        allChatMessages = new LinkedHashMap<>();
        chatListRecycler = v.findViewById(R.id.chatlistrecycle);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        allChatMessages = new LinkedHashMap<>();
        getAllChatMessages(httpClient, getActivity(), this.getView());
    }

    private void updateChatFragment(View v) {
        ChatListAdapter chatListAdapter = new ChatListAdapter(v.getContext(), allChatMessages, thisUserId, httpClient, getActivity());
        chatListRecycler.setAdapter((chatListAdapter));
    }

    private void getAllChatMessages(OkHttpClient client, Activity activity, View v) {

        Request request = new Request.Builder().url(Constants.baseServerURL + Constants.chatsByUserIdEndpoint + thisUserId).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d(TAG, e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                activity.runOnUiThread(() -> {
                    try {
                        String responseData = response.body().string();
                        if (responseData != null) {
                            Type listType = new TypeToken<List<ChatConversation>>(){}.getType();
                            Log.d(TAG, "responseData for Conversations is " + responseData);
                            List<ChatConversation> allConversations = gson.fromJson(responseData, listType);

                            if (allConversations.isEmpty()) {
                                Toast.makeText(getActivity(), R.string.no_chats_found, Toast.LENGTH_LONG).show();
                            }

                            // Iterate through all user conversations
                            for (ChatConversation conversation : allConversations) {
                                UserProfile user;
                                ArrayList<ChatMessage> eachMessageList;

                                // Get userId of both users in a conversation
                                List<String> userPair = conversation.getUsers();
                                if (userPair.get(0).equals(thisUserId)) {
                                    user = new UserProfile(userPair.get(1));
                                } else {
                                    user = new UserProfile(userPair.get(0));
                                }
                                eachMessageList = conversation.getMessages();
                                CompletableFuture<Response> future = updateUserProfile(httpClient, user, eachMessageList);
                                future.join();
                                Log.d(TAG, "Inside getAllChatMessages: first name is " + user.getFirstName());
                            }
                            updateChatFragment(v);
                        } else {
                            Log.d(TAG, "Inside getAllChatMessages: responseData is null");
                        }
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }
    private CompletableFuture<Response> updateUserProfile(OkHttpClient client, UserProfile user, ArrayList<ChatMessage> messages) {
        String url = Constants.baseServerURL + Constants.userEndpoint + user.get_id();
        Request request = new Request.Builder().url(url).build();
        OkHttpResponseFuture callback = new OkHttpResponseFuture() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d(TAG, e.getMessage());
                future.completeExceptionally(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                try {
                    String responseData = response.body().string();
                    if (response.body() != null) {
                        UserProfile fullUserProfile = gson.fromJson(responseData, UserProfile.class);

                        user.setFirstName(fullUserProfile.getFirstName());
                        user.setLastName(fullUserProfile.getLastName());
                        user.setProfilePicture(fullUserProfile.getProfilePicture());
                        Log.d(TAG, "Inside updateUserProfile: first name is " + user.getFirstName());
                        allChatMessages.put(user, messages);
                    } else {
                        Log.d(TAG, "Inside updateUserProfile: responseData is null");
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                future.complete(response);
            }
        };
        client.newCall(request).enqueue(callback);
        return callback.future;
    }
    public static class OkHttpResponseFuture implements Callback {
        public final CompletableFuture<Response> future = new CompletableFuture<>();

        @Override public void onFailure(Call call, IOException e) {
            future.completeExceptionally(e);
        }

        @Override public void onResponse(Call call, Response response) {
            future.complete(response);
        }
    }
}

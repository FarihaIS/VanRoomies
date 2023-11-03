package com.chads.vanroomies;

// Reference: https://www.geeksforgeeks.org/how-to-implement-chat-functionality-in-social-media-android-app/
import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
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
import java.util.List;
import java.util.Map;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {
    final static String TAG = "ChatFragment";
    private RecyclerView chatListRecycler;
    private ChatListAdapter chatListAdapter;
    private String thisUserId;
    private OkHttpClient httpClient;
    private Gson gson;
    private Map<UserProfile, ArrayList<ChatMessage>> allChatMessages;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public ChatFragment() {
        // Required empty public constructor
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatFragment newInstance(String param1, String param2) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chat, container, false);

        httpClient = HTTPSClientFactory.createClient(getActivity().getApplication());
        gson = new Gson();
        allChatMessages = new HashMap<>();
        // TODO: Get userId from backend
        thisUserId = "653eb733261cabe911fe75fb";
        chatListRecycler = v.findViewById(R.id.chatlistrecycle);

        getAllChatMessages(httpClient, getActivity(), v);

        return v;
    }

    private void updateChatFragment(View v) {
        chatListAdapter = new ChatListAdapter(v.getContext(), allChatMessages, thisUserId);
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
                            }
                            else {
                                user = new UserProfile(userPair.get(0));
                            }

                            eachMessageList = conversation.getMessages();
                            updateUserProfile(httpClient, getActivity(), user, eachMessageList, v);
                            Log.d(TAG, "Inside getAllChatMessages: first name is " + user.getFirstName());
                        }
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    private void updateUserProfile(OkHttpClient client, Activity activity, UserProfile user, ArrayList<ChatMessage> messages, View v) {
        String url = Constants.baseServerURL + Constants.userEndpoint + user.get_id();
        Request request = new Request.Builder().url(url).build();
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
                        UserProfile fullUserProfile = gson.fromJson(responseData, UserProfile.class);

                        user.setFirstName(fullUserProfile.getFirstName());
                        user.setLastName(fullUserProfile.getLastName());
                        user.setProfilePicture(fullUserProfile.getProfilePicture());
                        Log.d(TAG, "Inside updateUserProfile: first name is " + user.getFirstName());
                        allChatMessages.put(user, messages);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                    updateChatFragment(v);
                });
            }
        });
    }
}
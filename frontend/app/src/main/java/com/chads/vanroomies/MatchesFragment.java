package com.chads.vanroomies;

// Reference: https://www.geeksforgeeks.org/tinder-swipe-view-with-example-in-android/
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Objects;

import com.daprlabs.cardstack.SwipeDeck;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MatchesFragment extends Fragment {
    final static String TAG = "MatchesFragment";
    private final static String LAST_SEEN_MATCH_INDEX_KEY = "userMatches";
    private final static String USER_MATCHES_KEY = "lastSeenMatchIndex";
    private ArrayList<UserProfile> userMatches;
    private SwipeDeck cardStack;
    private String thisUserId;
    private OkHttpClient httpClient;
    private Gson gson;
    private int lastSeenMatchIndex = 0;

    // ChatGPT Usage: No
    @Override
    public void onPause() {
        super.onPause();
        saveLastSeenMatchIndex();
    }

    // ChatGPT Usage: No
    @Override
    public void onResume() {
        super.onResume();
        // Reload the last seen index when the fragment is resumed
        loadLastSeenMatchIndex();
    }

    // ChatGPT Usage: No
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    // ChatGPT Usage: No
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_matches, container, false);

        SharedPreferences sharedPref = getActivity().getSharedPreferences(Constants.userData, Context.MODE_PRIVATE);
        thisUserId = sharedPref.getString(Constants.userIdKey, Constants.userDefault);
        Log.d(TAG, sharedPref.getString(Constants.userIdKey, Constants.userDefault));

        httpClient = HTTPSClientFactory.createClient(getActivity().getApplication());
        gson = new Gson();
        cardStack = v.findViewById(R.id.matches_swipe_deck);
        userMatches = new ArrayList<>();

        if (savedInstanceState != null) {
            Log.d(TAG, "There is some saved instance state!");
            ArrayList<Parcelable> parcelableArrayList = savedInstanceState.getParcelableArrayList(USER_MATCHES_KEY);
            if (parcelableArrayList != null) {
                userMatches = (ArrayList<UserProfile>) ((ArrayList) parcelableArrayList);
                updateMatchesFragmentLayout(v);
            }
            lastSeenMatchIndex = savedInstanceState.getInt(LAST_SEEN_MATCH_INDEX_KEY, 0);
            Log.d(TAG, "Retrieving index from saved state = " + lastSeenMatchIndex);
        }
        else {
            getAllMatches(httpClient, getActivity(), v);
        }

        return v;
    }

    // ChatGPT Usage: No
    private void updateMatchesFragmentLayout(View v) {
        MatchDeckAdapter matchDeckAdapter = new MatchDeckAdapter(v.getContext(), userMatches);
        cardStack.setAdapter(matchDeckAdapter);
        cardStack.setSelection(lastSeenMatchIndex);
        Log.d(TAG, "Setting cardstack selection to " + lastSeenMatchIndex);

        cardStack.setEventCallback(new SwipeDeck.SwipeEventCallback() {
            @Override
            public void cardSwipedLeft(int position) {
                Log.d(TAG, "Match Rejected");
                lastSeenMatchIndex = position + 1;
            }

            @Override
            public void cardSwipedRight(int position) {
                Log.d(TAG, "Match accepted");
                excludeUserFromFutureMatches(httpClient, getActivity(), userMatches.get(position).get_id());
                startChatWithMatchedUser(httpClient, getActivity(), userMatches.get(position).get_id());
            }

            @Override
            public void cardsDepleted() {
                Log.d(TAG, "Fetching more matches");
                Toast.makeText(getActivity(), R.string.reshow_matches, Toast.LENGTH_SHORT).show();
                getAllMatches(httpClient, getActivity(), v);
            }

            @Override
            public void cardActionDown() {
                Log.d(TAG, "CARDS MOVED DOWN");
            }

            @Override
            public void cardActionUp() {
                Log.d(TAG, "CARDS MOVED UP");
            }
        });
    }

    // ChatGPT Usage: No
    private void getAllMatches(OkHttpClient client, Activity activity, View v) {
        String url = Constants.baseServerURL + Constants.userEndpoint + thisUserId + Constants.matchesByUserIdEndpoint;
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d(TAG, e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                    activity.runOnUiThread(() -> {
                        if (response.isSuccessful()) {
                            try {
                                String responseData = response.body().string();
                                if (response.body() == null) {
                                    Log.d(TAG, "Empty response data in getAllMatches.");
                                } else {
                                    Type listType = new TypeToken<ArrayList<UserProfile>>(){}.getType();
                                    ArrayList<UserProfile> allMatches = gson.fromJson(responseData, listType);
                                    allMatches.removeIf(Objects::isNull);
                                    userMatches = allMatches;
                                }

                                if (userMatches.isEmpty()) {
                                    Toast.makeText(getActivity(), R.string.no_matches_found, Toast.LENGTH_LONG).show();
                                }
                                else {
                                    updateMatchesFragmentLayout(v);
                                }
                            }
                            catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(getActivity(), R.string.no_matches_found, Toast.LENGTH_LONG).show();
                        }
                    });
            }
        });
    }

    // ChatGPT Usage: No
    private void excludeUserFromFutureMatches(OkHttpClient client, Activity activity, String matchUserId) {
        String url = Constants.baseServerURL + Constants.userEndpoint + thisUserId + Constants.matchesByUserIdEndpoint;
        Log.d(TAG, "Exclude userId " + matchUserId);
        RequestBody requestBody = new FormBody.Builder()
                .add("excludedId", matchUserId)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .put(requestBody)
                .build();

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
                        if (response.body() == null) {
                            Log.d(TAG, "responseData in excludeUserFromFutureMatches is null");
                        } else {
                            Log.d(TAG, "responseData in excludeUserFromFutureMatches is " + responseData);
                        }
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    // ChatGPT Usage: No
    private void startChatWithMatchedUser(OkHttpClient client, Activity activity, String matchUserId) {
        String url = Constants.baseServerURL + Constants.chatsByUserIdEndpoint + thisUserId;
        RequestBody requestBody = new FormBody.Builder()
                .add("to", matchUserId)
                .add("content", Constants.defaultFirstMessage)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

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
                        if (response.body() == null) {
                            Log.d(TAG, "responseData in startChatWithMatchedUser is null");
                        } else {
                            Log.d(TAG, "responseData in startChatWithMatchedUser is " + responseData);
                        }
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    // ChatGPT Usage: No
    private void saveLastSeenMatchIndex() {
        SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(LAST_SEEN_MATCH_INDEX_KEY, lastSeenMatchIndex);
        editor.apply();
    }

    // ChatGPT Usage: No
    private void loadLastSeenMatchIndex() {
        SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        lastSeenMatchIndex = prefs.getInt(LAST_SEEN_MATCH_INDEX_KEY, 0);
    }
}
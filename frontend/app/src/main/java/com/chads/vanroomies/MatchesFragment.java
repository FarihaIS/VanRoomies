package com.chads.vanroomies;

// Reference: https://www.geeksforgeeks.org/tinder-swipe-view-with-example-in-android/
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MatchesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MatchesFragment extends Fragment {
    final static String TAG = "MatchesFragment";
    private ArrayList<UserProfile> userMatches;
    private MatchDeckAdapter matchDeckAdapter;
    private SwipeDeck cardStack;
    private String thisUserId;
    private OkHttpClient httpClient;
    private Gson gson;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MatchesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MatchesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MatchesFragment newInstance(String param1, String param2) {
        MatchesFragment fragment = new MatchesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_matches, container, false);

        SharedPreferences sharedPref = getActivity().getSharedPreferences(Constants.userData, Context.MODE_PRIVATE);
        thisUserId = sharedPref.getString(Constants.userIdKey, Constants.userDefault);
        Log.d(TAG, sharedPref.getString(Constants.userIdKey, Constants.userDefault));

        httpClient = HTTPSClientFactory.createClient(getActivity().getApplication());
        gson = new Gson();
        userMatches = new ArrayList<>();
        cardStack = v.findViewById(R.id.matches_swipe_deck);

        getAllMatches(httpClient, getActivity(), v);
        return v;
    }

    private void updateMatchesFragmentLayout(View v) {
        matchDeckAdapter = new MatchDeckAdapter(v.getContext(), userMatches);
        cardStack.setAdapter(matchDeckAdapter);

        cardStack.setEventCallback(new SwipeDeck.SwipeEventCallback() {
            @Override
            public void cardSwipedLeft(int position) {
                Log.d(TAG, "Match Rejected");
            }

            @Override
            public void cardSwipedRight(int position) {
                Log.d(TAG, "Match accepted");
                excludeUserFromFutureMatches(httpClient, getActivity(), v, userMatches.get(position).get_id());
                startChatWithMatchedUser(httpClient, getActivity(), v, userMatches.get(position).get_id());
            }

            @Override
            public void cardsDepleted() {
                Log.d(TAG, "No more matches");
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
                                if (responseData == null) {
                                    Log.d(TAG, "Empty response data in getAllMatches...");
                                } else {
                                    Type listType = new TypeToken<ArrayList<UserProfile>>(){}.getType();
                                    ArrayList<UserProfile> allMatches = gson.fromJson(responseData, listType);
                                    userMatches = allMatches;
                                }

                                if (userMatches.isEmpty()) {
                                    Toast.makeText(getActivity(), R.string.no_matches_found, Toast.LENGTH_LONG).show();
                                }
                                else {
                                    Log.d(TAG, "Line 162");
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

    private void excludeUserFromFutureMatches(OkHttpClient client, Activity activity, View v, String matchUserId) {
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
                        if (responseData == null) {
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

    private void startChatWithMatchedUser(OkHttpClient client, Activity activity, View v, String matchUserId) {
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
                        if (responseData == null) {
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
}
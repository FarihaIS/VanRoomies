package com.chads.vanroomies;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionSpec;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PushNotificationService extends FirebaseMessagingService {
    private final String TAG = "PushNotificationService";
    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectionSpecs(Arrays.asList(ConnectionSpec.MODERN_TLS, ConnectionSpec.COMPATIBLE_TLS))
            .build();

    // ChatGPT usage: Partial
    @Override
    public void onNewToken(@NonNull String token) {
        Log.d(TAG, "New token generating...");
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (account == null) {
            Log.d(TAG, "Not signed into Google, won't send token then...");
        } else {
            SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(Constants.userData, Context.MODE_PRIVATE);
            String userId = sharedPref.getString(Constants.userIdKey, Constants.userDefault);
            sendRegistrationToServer(token, httpClient, userId);
        }
    }

    // ChatGPT usage: Partial
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());
    }
    public static void sendRegistrationToServer(String token, OkHttpClient client, String userId) {
        RequestBody formBody = new FormBody.Builder()
                .add("token", token)
                .add("userId", userId)
                .build();
        Request request = new Request.Builder()
                .url(Constants.baseServerURL + Constants.firebaseTokenEndpoint)
                .post(formBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                System.out.println("Token request failed: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.body() != null) {
                    System.out.println("Token request OK: " + response.body().string());
                }
            }
        });
    }

}

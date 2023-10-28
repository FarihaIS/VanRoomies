package com.chads.vanroomies;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetHelloWorldTest {
    final static String TAG = "GetHelloWorldTest";
    public static String testGetHelloWorld(OkHttpClient client, Activity act){
        Request request = new Request.Builder().url(Constants.localBaseServerURL + Constants.helloWorldEndpoint).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d(TAG, e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                act.runOnUiThread(() -> {
                    try {
                        String responseData = response.body().string();
                        Log.d(TAG, responseData);
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                });
            }
        });
        return null;
    }
}

package com.chads.vanroomies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    final static String TAG = "MainActivity";
    private int RC_SIGN_IN = 1;
    private OkHttpClient httpClient;
    final static Gson g = new Gson();

    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setLoginButton();
    }
    private void setLoginButton() {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(Constants.clientId)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        findViewById(R.id.login_button).setOnClickListener(view -> {
            signIn();
        });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            httpClient = HTTPSClientFactory.createClient(MainActivity.this.getApplication());

            RegistrationTaskParams params = new RegistrationTaskParams(httpClient, MainActivity.this, account);
            AsyncTaskRunner getUserIdTask = new AsyncTaskRunner();
            getUserIdTask.execute(params);

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null){
            httpClient = HTTPSClientFactory.createClient(MainActivity.this.getApplication());
            RegistrationTaskParams params = new RegistrationTaskParams(httpClient, MainActivity.this, account);
            AsyncTaskRunner getUserIdTask = new AsyncTaskRunner();
            getUserIdTask.execute(params);
        }
    }

    private static class RegistrationTaskParams {
        OkHttpClient client;
        Activity act;
        GoogleSignInAccount account;

        RegistrationTaskParams(OkHttpClient client, Activity act, GoogleSignInAccount account) {
            this.client = client;
            this.act = act;
            this.account = account;
        }
    }

    private class AsyncTaskRunner extends AsyncTask<RegistrationTaskParams, String, String> {
        private String resp;

        @Override
        protected String doInBackground(RegistrationTaskParams... params) {
            try {
                RegistrationTaskParams taskParameters = params[0];
                getUserId(taskParameters.client, taskParameters.act, taskParameters.account);
            } catch (Exception e) {
                e.printStackTrace();
                resp = e.getMessage();
            }
            return resp;
        }

        @Override
        protected void onPostExecute(String result) {
            Intent profileIntent = new Intent(MainActivity.this, HomeActivity.class); // intent for fragments
            Bundle b = new Bundle();
            b.putString("userId", result);
            profileIntent.putExtras(b);
            startActivity(profileIntent);
        }
    }

    public void getUserId(OkHttpClient client, Activity act, GoogleSignInAccount account){
        RequestBody formBody = new FormBody.Builder()
                .add("idToken", account.getIdToken())
                .add("firstName", account.getGivenName())
                .add("lastName", account.getFamilyName())
                .add("email", account.getEmail())
                .build();

        Request request = new Request.Builder().url(Constants.baseServerURL + Constants.loginEndpoint)
                .post(formBody)
                .build();
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
                        Map responseDataMap = g.fromJson(responseData, Map.class);

                        SharedPreferences sharedPref = MainActivity.this.getSharedPreferences(Constants.userData, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();

                        // Defining userId and userToken to be stored in SharedPreferences
                        String userId = responseDataMap.get("userId").toString();
                        if (response.code() == 201) {
                            // We only get and save userToken when the user is created for the first time
                            String userToken = responseDataMap.get("userToken").toString();
                            editor.putString(Constants.userTokenKey, userToken);
                        }
                        editor.putString(Constants.userIdKey, userId);
                        editor.apply();

                    } catch (IOException e){
                        e.printStackTrace();
                    }
                });
            }
        });
    }

}
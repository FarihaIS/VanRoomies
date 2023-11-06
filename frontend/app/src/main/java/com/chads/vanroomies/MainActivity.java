package com.chads.vanroomies;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
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
    private ActivityResultLauncher<Intent> launcher;
    final static Gson g = new Gson();

    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setLoginButton();
    }
    private void setLoginButton() {
        signIn();
        findViewById(R.id.login_button).setOnClickListener(view -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            launcher.launch(signInIntent);
        });
    }

    private void signIn() {
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    handleSignInResult(task);
                }
            }
        });

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(Constants.clientId)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
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

    private void getFirebaseTokenAndSend(String userId) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }
                    // Get new FCM registration token
                    String token = task.getResult();
                    PushNotificationService.sendRegistrationToServer(token, httpClient, userId);
                    Log.d(TAG, token);
                });
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
    }

    public void getUserId(OkHttpClient client, Activity act, GoogleSignInAccount account){
        // If idToken or email is null, then something very bad has happened
        String email = account.getEmail();
        String idToken = account.getIdToken();
        Objects.requireNonNull(email);
        Objects.requireNonNull(idToken);
        // We need to handle null family and given names, but since backend requires them, we set them to empty string
        String familyName = (account.getGivenName() != null) ? account.getGivenName() : "Friend";
        String givenName =  (account.getFamilyName() != null) ? account.getFamilyName() : "Friend";

        RequestBody formBody = new FormBody.Builder()
                .add("idToken", idToken)
                .add("firstName", givenName)
                .add("lastName", familyName)
                .add("email", email)
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
                        getFirebaseTokenAndSend(userId);

                        Intent profileIntent = new Intent(MainActivity.this, HomeActivity.class); // intent for fragments
                        Bundle b = new Bundle();
                        b.putString("userId", userId);
                        profileIntent.putExtras(b);
                        startActivity(profileIntent);

                    } catch (IOException e){
                        e.printStackTrace();
                    }
                });
            }
        });
    }

}

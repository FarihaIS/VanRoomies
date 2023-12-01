package com.chads.vanroomies;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import org.json.JSONException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ProfileFragment extends Fragment {
    final static String TAG = "ProfileFragment";
    private GoogleSignInClient mGoogleSignInClient;
    private OkHttpClient httpClient;
    final static Gson g = new Gson();
    private TextView profileName;
    private TextView profileEmail;
    private TextView profileDesc;
    private TextView profileBirthday;
    private TextView preferencesMinPrice;
    private TextView preferencesMaxPrice;
    private TextView preferencesHousingType;
    private TextView preferencesRoommateCount;
    private TextView preferencesPetFriendly;
    private TextView preferencesSmoking;
    private TextView preferencesPartying;
    private TextView preferencesDrinking;
    private TextView preferencesNoise;
    private TextView preferencesGender;
    private TextView preferencesLeaseLength;
    private Button editPreferencesButton;
    private ImageView profilePicture;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        httpClient = HTTPSClientFactory.createClient(getActivity().getApplication());

        // Bundle used for the first time this is loaded in-case the information isn't done saving
        Bundle b = getActivity().getIntent().getExtras();
        String userId = b.getString("userId");
        if (userId == null){
            SharedPreferences sharedPref = getActivity().getSharedPreferences(Constants.userData, Context.MODE_PRIVATE);
            userId = sharedPref.getString(Constants.userIdKey, Constants.userDefault);
        }
        Log.d(TAG, userId);

        // For testing connectivity with backend
        String result = GetHelloWorldTest.testGetHelloWorld(httpClient , getActivity());
        if (result != null){
            Log.d(TAG, result);
        }

        // Setup Sign-Out
        Button signOutButton = view.findViewById(R.id.sign_out_button);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(Constants.clientId)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        signOutButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                signOut();
            }
        });

        // Get profile
        profileName = view.findViewById(R.id.profile_name);
        profileEmail = view.findViewById(R.id.profile_email);
        profileDesc = view.findViewById(R.id.profile_blurb);
        profileBirthday = view.findViewById(R.id.profile_birthday);
        profilePicture = view.findViewById(R.id.profile_picture);
        getProfile(httpClient, view, getActivity(), userId);

        // Get user preferences
        preferencesMinPrice = view.findViewById(R.id.preferences_minPrice);
        preferencesMaxPrice = view.findViewById(R.id.preferences_maxPrice);
        preferencesHousingType = view.findViewById(R.id.preferences_housingType);
        preferencesRoommateCount = view.findViewById(R.id.preferences_roommateCount4);
        preferencesPetFriendly = view.findViewById(R.id.preferences_petFriendly);
        preferencesSmoking = view.findViewById(R.id.preferences_smoking);
        preferencesPartying = view.findViewById(R.id.preferences_partying);
        preferencesDrinking = view.findViewById(R.id.preferences_drinking);
        preferencesNoise = view.findViewById(R.id.preferences_noise);
        preferencesGender = view.findViewById(R.id.preferences_gender);
        preferencesLeaseLength = view.findViewById(R.id.preferences_leaseLength);
        getUserPreferences(httpClient, view, getActivity(), userId);

        // Set up button for editing user bio
        Button editDescButton = view.findViewById(R.id.edit_desc_button);
        String finalUserId = userId;
        editDescButton.setOnClickListener(temp -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
            // Initialize the edit box
            final EditText et = new EditText(getContext());
            et.setText(profileDesc.getText());
            et.setHeight(pxFromDp(getContext(), 250));
            alertDialogBuilder.setView(et);

            alertDialogBuilder.setCancelable(true).setPositiveButton("Save", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                    try {
                        updateUserBio(httpClient, view, getActivity(), finalUserId, et.getText().toString());
                    } catch (JSONException e) {
                        Log.d(TAG, Log.getStackTraceString(e));
                    }
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        });

        // Set up button for editing user preferences
        editPreferencesButton = view.findViewById(R.id.edit_preferences_button);
        String finalUserId1 = userId;
        editPreferencesButton.setOnClickListener(temp -> {
            // Setting up Add Listing Prompt
            Context context = view.getContext();
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);

            final EditText minPrice = new EditText(context);
            final EditText maxPrice = new EditText(context);
            final EditText housingType = new EditText(context);
            final EditText roommateCount = new EditText(context);
            final EditText petFriendly = new EditText(context);
            final EditText smoking = new EditText(context);
            final EditText partying = new EditText(context);
            final EditText drinking = new EditText(context);
            final EditText noise = new EditText(context);
            final EditText gender = new EditText(context);
            // final EditText moveInDate = new EditText(context);
            final EditText leaseLength = new EditText(context);

            // If we are editing and not creating, prefill fields
            if (editPreferencesButton.getText().equals(getString(R.string.edit_preferences_button))) {
                minPrice.setText(getExistingPreferenceField(preferencesMinPrice.getText()));
                maxPrice.setText(getExistingPreferenceField(preferencesMaxPrice.getText()));
                housingType.setText(getExistingPreferenceField(preferencesHousingType.getText()));
                roommateCount.setText(getExistingPreferenceField(preferencesRoommateCount.getText()));
                petFriendly.setText(getExistingPreferenceField(preferencesPetFriendly.getText()));
                smoking.setText(getExistingPreferenceField(preferencesSmoking.getText()));
                partying.setText(getExistingPreferenceField(preferencesPartying.getText()));
                drinking.setText(getExistingPreferenceField(preferencesDrinking.getText()));
                noise.setText(getExistingPreferenceField(preferencesNoise.getText()));
                gender.setText(getExistingPreferenceField(preferencesGender.getText()));
                leaseLength.setText(getExistingPreferenceField(preferencesLeaseLength.getText()));
            }

            minPrice.setHint("Minimum Price (Numerical)");
            maxPrice.setHint("Maximum Price (Numerical)");
            housingType.setHint("Housing Type (Must be: 'studio', '1-bedroom', '2-bedroom', or 'other')");
            roommateCount.setHint("Preferred # Roommates (Numerical)");
            petFriendly.setHint("Pets Allowed? (Y/N)");
            smoking.setHint("'no-smoking', 'neutral', or 'regular'");
            partying.setHint("'no-partying', 'neutral', or 'regular'");
            drinking.setHint("'no-drinking', 'neutral', or 'regular'");
            noise.setHint("'no-noise', 'neutral', or 'regular'");
            gender.setHint("'male', 'female', or 'neutral'");
            leaseLength.setHint("Preferred Lease Length (Numerical)");

            layout.addView(minPrice);
            layout.addView(maxPrice);
            layout.addView(housingType);
            layout.addView(roommateCount);
            layout.addView(petFriendly);
            layout.addView(smoking);
            layout.addView(partying);
            layout.addView(drinking);
            layout.addView(noise);
            layout.addView(gender);
            layout.addView(leaseLength);

            alertDialogBuilder.setView(layout); // Again this is a set method, not add
            alertDialogBuilder.setCancelable(true).setPositiveButton("Save", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                    List<String> preferenceParams = new ArrayList<>();
                    preferenceParams.add(minPrice.getText().toString());
                    preferenceParams.add(maxPrice.getText().toString());
                    preferenceParams.add(housingType.getText().toString());
                    preferenceParams.add(roommateCount.getText().toString());
                    preferenceParams.add(petFriendly.getText().toString());
                    preferenceParams.add(smoking.getText().toString());
                    preferenceParams.add(partying.getText().toString());
                    preferenceParams.add(drinking.getText().toString());
                    preferenceParams.add(noise.getText().toString());
                    preferenceParams.add(gender.getText().toString());
                    preferenceParams.add(leaseLength.getText().toString());

                    String error = validatePreferences(preferenceParams);
                    // Ensuring all fields are filled out + valid
                    if (error == null){
                        Log.d(TAG, "Editing/Adding Preferences.");
                        editOrAddPreferences(httpClient, view, getActivity(), finalUserId1, preferenceParams);
                    }
                    else {
                        Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                    }
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        });

        // Inflate the layout for this fragment
        return view;
    }

    public String validatePreferences(List<String> preferenceParams){
        if (preferenceParams.contains("")) {
            return "Please fill in all fields.";
        } else if (!isNumeric(preferenceParams.get(0))) {
            return "The min price must be numerical (i.e. '1500').";
        } else if (!isNumeric(preferenceParams.get(1))) {
            return "The max price must be numerical (i.e. '1500').";
        } else if (!preferenceParams.get(2).equals("studio") && !preferenceParams.get(2).equals("1-bedroom")
                && !preferenceParams.get(2).equals("2-bedroom") && !preferenceParams.get(2).equals("other")) {
            return "Housing Type must be one of: 'studio', " +
                    "'1-bedroom', '2-bedroom', or 'other'";
        } else if (!isNumeric(preferenceParams.get(3))){ //
            return "The preferred roommates must be numerical (i.e. '3').";
        } else if (!preferenceParams.get(4).equals("Y") && !preferenceParams.get(4).equals("N")) {
            return "Pet Friendly must be 'Y' or 'N'";
        } else if (!preferenceParams.get(5).equals("no-smoking") && !preferenceParams.get(5).equals("neutral")
                && !preferenceParams.get(5).equals("regular")) {
            return "Smoking must be 'no-smoking', 'neutral', or 'regular'";
        } else if (!preferenceParams.get(6).equals("no-partying") && !preferenceParams.get(6).equals("neutral")
                && !preferenceParams.get(6).equals("regular")) {
            return "Partying must be 'no-partying', 'neutral', or 'regular'";
        } else if (!preferenceParams.get(7).equals("no-drinking") && !preferenceParams.get(7).equals("neutral")
                && !preferenceParams.get(7).equals("regular")) {
            return "Drinking must be 'no-drinking', 'neutral', or 'regular'";
        } else if (!preferenceParams.get(8).equals("no-noise") && !preferenceParams.get(8).equals("neutral")
                && !preferenceParams.get(8).equals("regular")) {
            return "Noise must be 'no-noise', 'neutral', or 'regular'";
        } else if (!preferenceParams.get(9).equals("male") && !preferenceParams.get(9).equals("female")
                && !preferenceParams.get(9).equals("neutral")) {
            return "Gender must be 'male', 'female', or 'neutral'";
        } else if (!isNumeric(preferenceParams.get(0))) {
            return "The lease must be numerical (i.e. '12').";
        }
        return null;
    }

    public void getProfile(OkHttpClient client, View view, Activity act, String user_id){
        Request request = new Request.Builder().url(Constants.baseServerURL + Constants.userEndpoint + user_id).build();
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
                        Map result = g.fromJson(responseData, Map.class);
                        profileName.setText(String.format("%s %s", result.get("firstName"), result.get("lastName")));
                        profileEmail.setText((CharSequence) result.get("email"));
                        if (result.get("bio") != null){
                            profileDesc.setText((CharSequence) result.get("bio"));
                        }
                        else {
                            profileDesc.setText("");
                        }
                        profileBirthday.setVisibility(view.INVISIBLE);
//                        if (result.get("birthday") != null) {
//                            String birthday = result.get("birthday").toString();
//                            profileBirthday.setVisibility(view.VISIBLE);
//                            profileBirthday.setText(String.format("%s %s", "Birthday:", birthday.split("T")[0]));
//                        } else {
//                            profileBirthday.setText("");
//                            profileBirthday.setVisibility(view.INVISIBLE);
//                        }

                        if (result.get("profilePicture") != null) {
                            String imageString = result.get("profilePicture").toString().matches(Constants.base64Regex)
                                    ? result.get("profilePicture").toString() : "";
                            byte[] decodedString = Base64.decode(imageString, Base64.DEFAULT);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            profilePicture.setImageBitmap(decodedByte);
                        }
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    public void getUserPreferences(OkHttpClient client, View view, Activity act, String user_id){
        Request request = new Request.Builder().url(Constants.baseServerURL + Constants.userPreferencesEndpoint(user_id)).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d(TAG, e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                act.runOnUiThread(() -> {
                    try {
                        ResponseBody responseBody = response.body();
                        String responseData = responseBody.string();
                        UserPreferencesResponseResult result = g.fromJson(responseData, UserPreferencesResponseResult.class);
                        // User has Preferences
                        if (result.getMinPrice() != null) {
                            Log.d(TAG, "Existing Preferences Found!");
                            preferencesMinPrice.setText(String.format("Min Price: %s", result.getMinPrice()));
                            preferencesMaxPrice.setText(String.format("Max Price: %s", result.getMaxPrice()));
                            preferencesHousingType.setText(String.format("Housing Type: %s", result.getHousingType()));
                            preferencesRoommateCount.setText(String.format("Roommates: %s", result.getRoommateCount()));
                            preferencesPetFriendly.setText(String.format("Pet Friendly: %s", result.getPetFriendly()));
                            preferencesSmoking.setText(String.format("Smoking: %s", result.getSmoking()));
                            preferencesPartying.setText(String.format("Partying: %s", result.getPartying()));
                            preferencesDrinking.setText(String.format("Drinking: %s", result.getDrinking()));
                            preferencesNoise.setText(String.format("Noise: %s", result.getNoise()));
                            preferencesGender.setText(String.format("Gender: %s", result.getGender()));
                            preferencesLeaseLength.setText(String.format("Lease Length: %s", result.getLeaseLength()));
                            editPreferencesButton.setText(getString(R.string.edit_preferences_button));
                        }
                        else {
                            Log.d(TAG, "No existing preferences");
                            preferencesMinPrice.setText(String.format("Min Price: %s", "N/A"));
                            preferencesMaxPrice.setText(String.format("Max Price: %s", "N/A"));
                            preferencesHousingType.setText(String.format("Housing Type: %s", "N/A"));
                            preferencesRoommateCount.setText(String.format("Roommates: %s", "N/A"));
                            preferencesPetFriendly.setText(String.format("Pet Friendly: %s", "N/A"));
                            preferencesSmoking.setText(String.format("Smoking: %s", "N/A"));
                            preferencesPartying.setText(String.format("Partying: %s", "N/A"));
                            preferencesDrinking.setText(String.format("Drinking: %s", "N/A"));
                            preferencesNoise.setText(String.format("Noise: %s", "N/A"));
                            preferencesGender.setText(String.format("Gender: %s", "N/A"));
                            preferencesLeaseLength.setText(String.format("Lease Length: %s", "N/A"));
                            editPreferencesButton.setText(getString(R.string.add_preferences_button));
                        }

                    } catch (IOException e){
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    public void editOrAddPreferences(OkHttpClient client, View view, Activity act, String user_id, List<String> preferenceParams){
        String petFriendly = preferenceParams.get(4).equals("Y") ? "true" : "false";
        // Setting up a POST request
        RequestBody formBody = new FormBody.Builder()
                .add("minPrice", preferenceParams.get(0))
                .add("maxPrice", preferenceParams.get(1))
                .add("housingType", preferenceParams.get(2))
                .add("roommateCount", preferenceParams.get(3))
                .add("petFriendly", petFriendly)
                .add("smoking", preferenceParams.get(5))
                .add("partying", preferenceParams.get(6))
                .add("drinking", preferenceParams.get(7))
                .add("noise", preferenceParams.get(8))
                .add("gender", preferenceParams.get(9))
                .add("moveInDate", "2024-01-01") // ToDo: Get from user and parse in future milestones
                .add("leaseLength", preferenceParams.get(10))
                .build();

        Request request;
        if (editPreferencesButton.getText().equals("Add")){
            request = new Request.Builder().url(Constants.baseServerURL + Constants.userPreferencesEndpoint(user_id))
                .post(formBody) // POST
                .build();
        }
        else {
            request = new Request.Builder().url(Constants.baseServerURL + Constants.userPreferencesEndpoint(user_id))
                .put(formBody) // PUT
                .build();
        }

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d(TAG, e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                act.runOnUiThread(() -> getUserPreferences(client, view, act, user_id));
            }
        });
    }
    public void updateUserBio(OkHttpClient client, View view, Activity act, String user_id, String desc) throws JSONException {
        // Setting up the request
        RequestBody formBody = new FormBody.Builder()
                .add("bio", desc)
                .build();

        Request request = new Request.Builder()
                .url(Constants.baseServerURL + Constants.userEndpoint + user_id)
                .put(formBody) // PUT
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d(TAG, e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                act.runOnUiThread(() -> {
                    Log.d(TAG, response.toString());
                    profileDesc.setText(desc);
                });
            }
        });
    }

    // From https://stackoverflow.com/questions/37929419/set-size-of-contents-dynamically-in-dp-in-android
    public static int pxFromDp(Context context, float dp) {
        return (int)(dp * context.getResources().getDisplayMetrics().density);
    }

    public static boolean isNumeric(String input) {
        try {
            Double.parseDouble(input);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    // Takes the current text from the screen and converts it to what was previously inputted
    public static String getExistingPreferenceField(CharSequence input){
        String preference = input.toString().split(": ")[1];
        if (preference.equals("true")){
            preference = "Y";
        }
        else if (preference.equals("false")) {
            preference = "N";
        }
        return preference;
    }

    void signOut(){
        mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                getActivity().finish();
                startActivity(new Intent(getActivity(), MainActivity.class));
            }
        });
    }
}
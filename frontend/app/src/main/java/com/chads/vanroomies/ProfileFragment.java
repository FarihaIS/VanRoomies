package com.chads.vanroomies;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.gson.Gson;
import org.json.JSONException;
import java.io.IOException;
import java.util.Map;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    final static String TAG = "ProfileFragment";
    private OkHttpClient httpClient;
    final static Gson g = new Gson();
    private TextView profileName;
    private TextView profileEmail;
    private TextView profileDesc;
    private TextView profileBirthday;

    private ImageView profilePicture;
    private Button editDescButton;
    // TODO: Keep track of userId and replace the one below
    private String userId = "653dde0848a54c10b096a65e";
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        httpClient = HTTPSClientFactory.createClient(getActivity().getApplication());

        // For testing connectivity with backend
        String result = GetHelloWorldTest.testGetHelloWorld(httpClient , getActivity());
        if (result != null){
            Log.d(TAG, result);
        }

        // Get profile
        profileName = view.findViewById(R.id.profile_name);
        profileEmail = view.findViewById(R.id.profile_email);
        profileDesc = view.findViewById(R.id.profile_blurb);
        profileBirthday = view.findViewById(R.id.profile_birthday);
        profilePicture = view.findViewById(R.id.profile_picture);
        getProfile(httpClient, view, getActivity(), userId);

        editDescButton = view.findViewById(R.id.edit_desc_button);
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
                                updateUserBio(httpClient, view, getActivity(), userId, et.getText().toString());
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
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
                        profileDesc.setText((CharSequence) result.get("bio"));
                        profileBirthday.setText(String.format("%s %s", "Birthday:", result.get("birthday")));

                        if (result.get("profilePicture") != null) {
                            byte[] decodedString = Base64.decode((String) result.get("profilePicture"), Base64.DEFAULT);
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
}
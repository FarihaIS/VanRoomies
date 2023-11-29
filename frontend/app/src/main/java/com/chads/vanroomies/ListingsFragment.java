package com.chads.vanroomies;

import android.app.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ListingsFragment extends Fragment implements ListingsItemSelectListener{
    final static String TAG = "ListingsFragment";
    private OkHttpClient httpClient;
    public SwitchMaterial toggleButton;
    public Button addListingButton;
    public TextView titleText;
    final static Gson g = new Gson();
    final static int view_cols = 2;
    private RecyclerView recyclerView;
    private ArrayList<ListingsRecyclerData> recyclerDataArrayList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listings, container, false);
        recyclerView = view.findViewById(R.id.idListingsRV);

        SharedPreferences sharedPref = getActivity().getSharedPreferences(Constants.userData, Context.MODE_PRIVATE);
        String userId = sharedPref.getString(Constants.userIdKey, Constants.userDefault);
        Log.d(TAG, sharedPref.getString(Constants.userIdKey, Constants.userDefault));

        httpClient = HTTPSClientFactory.createClient(getActivity().getApplication());
        getRecommendedListings(httpClient, view, getActivity(), userId);

        // Setting up Toggle Button
        toggleButton = view.findViewById(R.id.listings_toggle);
        toggleButton.setText("");

        // Setting up Add Listing Button
        addListingButton = view.findViewById(R.id.createListingButton);
        addListingButton.setEnabled(false);
        addListingButton.setVisibility(View.INVISIBLE);
        addListingButton.setOnClickListener(temp -> {
            // Setting up Add Listing Prompt
            Context context = view.getContext();
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);

            final EditText titleBox = new EditText(context);
            final EditText descriptionBox = new EditText(context);
            final EditText housingType = new EditText(context);
            final EditText rentalPrice = new EditText(context);
            // final EditText moveInDate = new EditText(context);
            final EditText petFriendly = new EditText(context);
            final EditText latitude = new EditText(context);
            final EditText longitude = new EditText(context);


            titleBox.setHint("Listing Title (5 characters minimum)");
            descriptionBox.setHint("Description");
            housingType.setHint("Housing Type (Must be: 'studio', '1-bedroom', '2-bedroom', or 'other')");
            rentalPrice.setHint("Rental Price (Numerical)");
            petFriendly.setHint("Pets Allowed? (Y/N)");
            latitude.setHint("Latitude as a Decimal between -180 and 180 (e.g. -15.2345). Optional.");
            longitude.setHint("Longitude as a Decimal between -180 and 180 (e.g. 123.1536). Optional.");

            layout.addView(titleBox);
            layout.addView(housingType);
            layout.addView(descriptionBox);
            layout.addView(rentalPrice);
            layout.addView(petFriendly);
            layout.addView(latitude);
            layout.addView(longitude);

            alertDialogBuilder.setView(layout); // Again this is a set method, not add
            alertDialogBuilder.setCancelable(true).setPositiveButton("Create", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                    List<String> listingParams = new ArrayList<>();
                    listingParams.add(titleBox.getText().toString());
                    listingParams.add(descriptionBox.getText().toString());
                    listingParams.add(housingType.getText().toString());
                    listingParams.add(rentalPrice.getText().toString());
                    listingParams.add(petFriendly.getText().toString());
                    listingParams.add(latitude.getText().toString().equals("")
                            ? "0.0000" : latitude.getText().toString());
                    listingParams.add(longitude.getText().toString().equals("")
                            ? "0.0000" : longitude.getText().toString());

                    // Ensuring all fields are filled out
                    if (listingParams.contains("")) {
                        Toast.makeText(context, "Please fill in all fields.", Toast.LENGTH_LONG).show();
                    } else if (listingParams.get(0).length() < 5) {
                        Toast.makeText(context, "The title must be at least 5 characters long.", Toast.LENGTH_LONG).show();
                    } else if (!listingParams.get(2).equals("studio") && !listingParams.get(2).equals("1-bedroom")
                            && !listingParams.get(2).equals("2-bedroom") && !listingParams.get(2).equals("other")) {
                        Toast.makeText(context, "Housing Type must be one of: 'studio', " +
                                "'1-bedroom', '2-bedroom', or 'other'", Toast.LENGTH_LONG).show();
                    } else if (!isNumeric(listingParams.get(3))){
                        Toast.makeText(context, "The rental price must be numerical (i.e. '1500').", Toast.LENGTH_LONG).show();
                    } else if (!listingParams.get(4).equals("Y") && !listingParams.get(4).equals("N")) {
                        Toast.makeText(context, "Pet Friendly must be 'Y' or 'N'", Toast.LENGTH_LONG).show();
                    }
                    else {
                        try {
                            if (Double.parseDouble(listingParams.get(5)) > 180 || Double.parseDouble(listingParams.get(5)) < -180) {
                                Toast.makeText(context, "Latitude must be between -180 and 180.", Toast.LENGTH_LONG).show();
                            } else if (Double.parseDouble(listingParams.get(6)) > 180 || Double.parseDouble(listingParams.get(6)) < -180) {
                                Toast.makeText(context, "Longitude must be between -180 and 180.", Toast.LENGTH_LONG).show();
                            }
                        } catch (NumberFormatException e) {
                            Toast.makeText(context, "Latitude and Longitude must be numerical. (i.e. 123.0000)", Toast.LENGTH_LONG).show();
                            return;
                        }

                        try {
                            Log.d(TAG, "Creating Listing.");
                            createListing(httpClient, view, getActivity(), listingParams, userId);
                        } catch (JSONException e) {
                            Log.d(TAG, Log.getStackTraceString(e));
                        }
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


        titleText = (TextView) view.findViewById(R.id.listings_header);
        titleText.setText(getString(R.string.listings_header_recommended));
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if(isChecked){
                    addListingButton.setEnabled(true);
                    addListingButton.setVisibility(View.VISIBLE);
                    titleText.setText(getString(R.string.listings_header_owned));
                    getOwnedListings(httpClient, view, getActivity(), userId);
                } else {
                    addListingButton.setEnabled(false);
                    addListingButton.setVisibility(View.INVISIBLE);
                    titleText.setText(getString(R.string.listings_header_recommended));
                    getRecommendedListings(httpClient, view, getActivity(), userId);
                }
            }
        });
        return view;
    }
    public void getRecommendedListings(OkHttpClient client, View view, Activity act, String userId){
        Request request = new Request.Builder().url(Constants.baseServerURL + Constants.listingByRecommendationsEndpoint(userId)).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d(TAG, e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                act.runOnUiThread(() -> {
                    try {
                        recyclerDataArrayList = new ArrayList<>();
                        ResponseBody responseBody = response.body();
                        String responseData = responseBody.string();

                        if (response.isSuccessful() && responseData.length() > 0){
                            List<Map<String, Object>> responseDataList = g.fromJson(responseData, List.class);
                            for (int index = 0; index < responseDataList.size(); index++){
                                Map<String, Object> listing_json = responseDataList.get(index);
                                JSONObject listing_obj = new JSONObject(listing_json);
                                String listing_title = listing_obj.getString("title");
                                JSONArray listing_photo_array = listing_obj.getJSONArray("images");
                                String listing_photo = "";
                                if (listing_photo_array.length() > 0){
                                    listing_photo = listing_photo_array.get(0).toString();
                                }
                                // Information taken to individual listing

                                String listing_id = listing_obj.getString("_id");
                                HashMap<String, String> additionalInfo = new HashMap<>();
                                additionalInfo.put("owner_id", listing_obj.getString("userId"));
                                additionalInfo.put("description", listing_obj.getString("description"));
                                additionalInfo.put("housingType", listing_obj.getString("housingType"));
                                additionalInfo.put("listingDate", listing_obj.getString("listingDate"));
                                additionalInfo.put("moveInDate", listing_obj.getString("moveInDate"));
                                additionalInfo.put("petFriendly", listing_obj.getString("petFriendly"));

                                recyclerDataArrayList.add(new ListingsRecyclerData(listing_title, listing_photo, listing_id, additionalInfo));
                            }
                        }
                        // added data from arraylist to adapter class.
                        ListingsRecyclerViewAdapter adapter = new ListingsRecyclerViewAdapter(recyclerDataArrayList, ListingsFragment.this, view.getContext());

                        // setting grid layout manager to implement grid view.
                        // in this method '2' represents number of columns to be displayed in grid view.
                        GridLayoutManager layoutManager = new GridLayoutManager(view.getContext(), view_cols);

                        // at last set adapter to recycler view.
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(adapter);
                    } catch (IOException e){
                        e.printStackTrace();
                    } catch (JSONException e) {
                        Log.d(TAG, Log.getStackTraceString(e));
                    }
                });
            }
        });
    }
    public void getOwnedListings(OkHttpClient client, View view, Activity act, String userId){
        Request request = new Request.Builder().url(Constants.baseServerURL + Constants.listingByUserIdEndpoint + userId).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d(TAG, e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                act.runOnUiThread(() -> {
                    try {
                        recyclerDataArrayList = new ArrayList<>();
                        String responseData = response.body().string();
                        List<Map<String, Object>> responseDataList = g.fromJson(responseData, List.class);
                        // We want to sort listings by newest first. The query also sorts similar times in a way opposite
                        // to which we want it, so the BE sorts by oldest first and we reverse that list.
                        Collections.reverse(responseDataList);
                        for (int index = 0; index < responseDataList.size(); index++){
                            Map<String, Object> listingJson = responseDataList.get(index);
                            JSONObject listing_obj = new JSONObject(listingJson);

                            // Information shown in list
                            String listing_title = listing_obj.getString("title");
                            JSONArray listing_photo_array = listing_obj.getJSONArray("images");
                            String listing_photo = "";
                            if (listing_photo_array.length() > 0){
                                listing_photo = listing_photo_array.get(0).toString();
                            }
                            // Information taken to individual listing
                            String listing_id = listing_obj.getString("_id");
                            HashMap<String, String> additionalInfo = new HashMap<>();
                            additionalInfo.put("owner_id", listing_obj.getString("userId"));
                            additionalInfo.put("description", listing_obj.getString("description"));
                            additionalInfo.put("housingType", listing_obj.getString("housingType"));
                            additionalInfo.put("listingDate", listing_obj.getString("listingDate"));
                            additionalInfo.put("moveInDate", listing_obj.getString("moveInDate"));
                            additionalInfo.put("petFriendly", listing_obj.getString("petFriendly"));

                            recyclerDataArrayList.add(new ListingsRecyclerData(listing_title, listing_photo, listing_id, additionalInfo));
                        }

                        // added data from arraylist to adapter class.
                        ListingsRecyclerViewAdapter adapter = new ListingsRecyclerViewAdapter(recyclerDataArrayList, ListingsFragment.this, view.getContext());

                        // setting grid layout manager to implement grid view.
                        // in this method '2' represents number of columns to be displayed in grid view.
                        GridLayoutManager layoutManager = new GridLayoutManager(view.getContext(),view_cols);

                        // at last set adapter to recycler view.
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(adapter);
                    } catch (IOException e){
                        e.printStackTrace();
                    } catch (JSONException e) {
                        Log.d(TAG, Log.getStackTraceString(e));
                    }
                });
            }
        });
    }

    @Override
    public void onItemClicked(ListingsRecyclerData recyclerData) {
        Intent intent = new Intent(getActivity(), ViewListingActivity.class);
        Bundle b = new Bundle();
        b.putString("listing_id", recyclerData.getListingId());
        b.putString("title", recyclerData.getTitle());
        b.putString("photo_string", recyclerData.getImageString());
        b.putSerializable("info", recyclerData.getAdditionalInfo());
        intent.putExtras(b);

        startActivity(intent);
    }

    public void createListing(OkHttpClient client, View view, Activity act, List<String> params, String userId) throws JSONException {
        String petFriendly;
        if (params.get(4).equals("Y")){
            petFriendly = "true";
        }
        else {
            petFriendly = "false";
        }

        JSONObject location = new JSONObject();
        location.put("latitude", Double.valueOf(params.get(5)));
        location.put("longitude", Double.valueOf(params.get(6)));

        // Setting up a POST request
        JSONObject json = new JSONObject();
        json.put("userId", userId);
        json.put("title", params.get(0));
        json.put("description", params.get(1));
        json.put("housingType", params.get(2));
        json.put("rentalPrice", params.get(3));
        json.put("listingDate", getTodayAsString());
        json.put("moveInDate", "2024-03-01");
        json.put("petFriendly", petFriendly);
        json.put("status", "active");
        json.put("location", location);

        RequestBody formBody = RequestBody.create(MediaType.parse("application/json"), json.toString());

        Request request = new Request.Builder().url(Constants.baseServerURL + Constants.listingByListingIdEndpoint)
                .post(formBody) // POST
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d(TAG, e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                act.runOnUiThread(() -> getOwnedListings(client, view, act, userId));
            }
        });
    }
    public static boolean isNumeric(String input) {
        try {
            Double.parseDouble(input);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    public String getTodayAsString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.now().format(formatter);
    }
}
package com.chads.vanroomies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ViewListingActivity extends AppCompatActivity {
    final static String TAG = "ViewListing";
    OkHttpClient client;
    static boolean isOwner = false;
    final static Gson g = new Gson();

    private Button editTitleButton;
    private Button editHousingDescButton;
    private Button editHousingTypeButton;
    private Button togglePetFriendlyButton;
    private Button editMoveInButton;
    private Button reportButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_view_listing);

        // Grabbing parameters from listings view
        Bundle b = getIntent().getExtras();
        client = HTTPSClientFactory.createClient(ViewListingActivity.this.getApplication());
        SharedPreferences sharedPref = ViewListingActivity.this.getSharedPreferences(Constants.userData, Context.MODE_PRIVATE);
        String user_id = sharedPref.getString(Constants.userIdKey, Constants.userDefault);
        Log.d(TAG, sharedPref.getString(Constants.userIdKey, Constants.userDefault));
        if(b != null) {
            String listingId = b.getString("listing_id");
            getListing(client, listingId, user_id);
        }
    }

    public void enableButton(Button button, String attribute, TextView text_field, String listingId){
        button.setEnabled(true);
        button.setVisibility(View.VISIBLE);
        // Set Listener
        button.setOnClickListener(view -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
            // Initialize the edit box
            final EditText et = new EditText(view.getContext());
            et.setText(text_field.getText());
            et.setHeight(pxFromDp(view.getContext(), 250));
            alertDialogBuilder.setView(et);

            alertDialogBuilder.setCancelable(true).setPositiveButton("Save", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                    try {
                        updateEditableText(client, view, ViewListingActivity.this, attribute, listingId, text_field, et.getText().toString());
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
    }

    public void enableToggle(Button button, String attribute, TextView text_field, String listingId){
        button.setEnabled(true);
        button.setVisibility(View.VISIBLE);
        // Set Listener
        button.setOnClickListener(view -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
            alertDialogBuilder
                    .setCancelable(true)
                    .setTitle("Are pets allowed?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();

                            // Setting up a PUT request
                            RequestBody formBody = new FormBody.Builder()
                                    .add(attribute, "true")
                                    .build();
                            Request request = new Request.Builder()
                                    .url(Constants.baseServerURL + Constants.listingByListingIdEndpoint + listingId)
                                    .put(formBody) // PUT
                                    .build();
                            client.newCall(request).enqueue(new Callback() {
                                @Override
                                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                    Log.d(TAG, e.getMessage());
                                }

                                @Override
                                public void onResponse(@NonNull Call call, @NonNull Response response) {
                                    Log.d(TAG, "Listing successfully updated!");
                                }
                            });
                            // Change to allowed
                            text_field.setText(String.format("%s %s", getString(R.string.pets), getString(R.string.allowed)));
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            // Setting up a PUT request
                            RequestBody formBody = new FormBody.Builder()
                                    .add(attribute, "false")
                                    .build();
                            Request request = new Request.Builder()
                                    .url(Constants.baseServerURL + Constants.listingByListingIdEndpoint + listingId)
                                    .put(formBody) // PUT
                                    .build();
                            client.newCall(request).enqueue(new Callback() {
                                @Override
                                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                    Log.d(TAG, e.getMessage());
                                }

                                @Override
                                public void onResponse(@NonNull Call call, @NonNull Response response) {
                                    Log.d(TAG, "Inside onResponse of seNegativeButton for enableToggle");
                                }
                            });
                            text_field.setText(String.format("%s %s", getString(R.string.pets), getString(R.string.not_allowed)));
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        });
    }

    public void disableButton(Button button){
        button.setEnabled(false);
        button.setVisibility(View.INVISIBLE);
    }

    public void updateEditableText(OkHttpClient client, View view, Activity act, String field, String listingId, TextView textview_to_update, String new_text) throws JSONException {
        // Setting up the request
        RequestBody formBody = new FormBody.Builder()
                .add(field, new_text)
                .build();
        Request request = new Request.Builder()
                .url(Constants.baseServerURL + Constants.listingByListingIdEndpoint + listingId)
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
                    textview_to_update.setText(new_text);
                });
            }
        });
    }
    public static int pxFromDp(Context context, float dp) {
        return (int)(dp * context.getResources().getDisplayMetrics().density);
    }
    public void reportListing(OkHttpClient client, String listingId, String userId) {
        // Setting up a POST request
        RequestBody formBody = new FormBody.Builder()
                .add("reporterId", userId)
                .build();
        Request request = new Request.Builder()
                .url(Constants.baseServerURL + Constants.reportListingEndpoint(listingId))
                .post(formBody) // POST
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d(TAG, e.getMessage());
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                Log.d(TAG, "Listing successfully reported!");
            }
        });
    }
    public void setupReportButton(String listingId, String userId){
        reportButton.setOnClickListener(view -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
            alertDialogBuilder.setTitle("Report this listing as a scam?");
            alertDialogBuilder.setCancelable(true).setPositiveButton("Report", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                    reportListing(client, listingId, userId);
                    disableButton(reportButton);
                    Toast.makeText(ViewListingActivity.this, "Listing reported! This listing will no longer be recommended to you.", Toast.LENGTH_LONG).show();
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        });
    }
    public void getListing(OkHttpClient client, String listingId, String userId){
        Request request = new Request.Builder().url(Constants.baseServerURL + Constants.listingByListingIdEndpoint + listingId).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d(TAG, e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                runOnUiThread(() -> {
                    try {
                        String responseData = response.body().string();
                        // Fetched listing information
                        SingleListingResponseResult result = g.fromJson(responseData, SingleListingResponseResult.class);

                        // Checking if Current User is the Owner
                        isOwner = (Objects.equals(result.getUserId(), userId));
                        Log.d(TAG, String.valueOf(isOwner));


                        // Fetching Parameters
                        String photoString = "";
                        List<String> imagesList = result.getImages();
                        if (imagesList.size() > 0 && imagesList.get(0).matches(Constants.base64Regex)){
                            photoString = imagesList.get(0);
                        }
                        String title = result.getTitle();
                        String description = result.getDescription();
                        String housingType = result.getHousingType();
                        String listingDate = result.getListingDate().split(getString(R.string.datetime_regex), 2)[0];
                        String moveInDate = result.getMoveInDate().split(getString(R.string.datetime_regex), 2)[0];
                        String petFriendly = String.valueOf(result.getPetFriendly());

                        // Instantiating TextViews
                        ImageView listing_image = findViewById(R.id.listing_picture);
                        TextView title_textview = findViewById(R.id.listing_name);
                        TextView description_textview = findViewById(R.id.listing_desc);
                        TextView housing_type_textview = findViewById(R.id.housing_type);
                        TextView listing_date_textview = findViewById(R.id.listing_date);
                        TextView move_in_date_textview = findViewById(R.id.move_in_date);
                        TextView pet_textview = findViewById(R.id.pet_friendly);

                        // Setting ImageView. Verification done when setting photoString
                        byte[] decodedString = Base64.decode(photoString, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        listing_image.setImageBitmap(decodedByte);

                        // Setting TextViews
                        title_textview.setText(title);
                        description_textview.setText(description);
                        housing_type_textview.setText(housingType);
                        listing_date_textview.setText(String.format("%s %s", getString(R.string.posted), listingDate));
                        move_in_date_textview.setText(String.format("%s %s", getString(R.string.move), moveInDate));
                        if(petFriendly.equals("true")){
                            pet_textview.setText(String.format("%s %s", getString(R.string.pets), getString(R.string.allowed)));
                        }
                        else {
                            pet_textview.setText(String.format("%s %s", getString(R.string.pets), getString(R.string.not_allowed)));
                        }

                        editTitleButton = findViewById(R.id.edit_title);
                        editHousingDescButton = findViewById(R.id.edit_housing_desc);
                        editHousingTypeButton = findViewById(R.id.edit_housing_type);
                        togglePetFriendlyButton = findViewById(R.id.edit_pet_friendly);
                        editMoveInButton = findViewById(R.id.edit_move_in_button);
                        reportButton = findViewById(R.id.report_button);
                        disableButton(editMoveInButton);
                        if(!isOwner) {
                            disableButton(editTitleButton);
                            disableButton(editHousingDescButton);
                            disableButton(editHousingTypeButton);
                            disableButton(togglePetFriendlyButton);
                            // TODO: In future milestones, implement a way to change Move-In Date and Image
                            // disableButton(editMoveInButton);
                            setupReportButton(listingId, userId);
                        }
                        else {
                            enableButton(editTitleButton, "title", title_textview, listingId);
                            enableButton(editHousingDescButton, "description", description_textview, listingId);
                            enableButton(editHousingTypeButton, "housingType", housing_type_textview, listingId);
                            enableToggle(togglePetFriendlyButton, "petFriendly", pet_textview, listingId);
                            disableButton(reportButton);
                            // enableButton(editMoveInButton, "moveInDate", move_in_date_textview, listingId);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }
}
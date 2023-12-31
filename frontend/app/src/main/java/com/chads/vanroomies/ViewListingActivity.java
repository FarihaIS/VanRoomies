package com.chads.vanroomies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
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
    private Button deleteButton;
    private Button mapButton;
    private Button editLatLongButton;
    private Button editPriceButton;


    // ChatGPT Usage: No
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

    // ChatGPT Usage: No
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
                        if (Objects.equals(attribute, "housingType")) {
                            String text = String.valueOf(et.getText());
                            if (!text.equals("studio") &&
                                !text.equals("1-bedroom") &&
                                !text.equals("2-bedroom") &&
                                !text.equals("other")){
                                Toast.makeText(view.getContext(), "Please enter a valid value for housingType.", Toast.LENGTH_LONG).show();
                            }
                            else {
                                updateEditableText(client, view, ViewListingActivity.this, attribute, listingId, text_field, String.valueOf(et.getText()));
                            }
                        }
                        else if (Objects.equals(attribute, "title")){
                            String text = String.valueOf(et.getText());
                            if (text.length() < 5){
                                Toast.makeText(view.getContext(), "The title must be at least 5 characters long.", Toast.LENGTH_LONG).show();
                            }
                            else {
                                updateEditableText(client, view, ViewListingActivity.this, attribute, listingId, text_field, String.valueOf(et.getText()));
                            }
                        }
                        else {
                            updateEditableText(client, view, ViewListingActivity.this, attribute, listingId, text_field, String.valueOf(et.getText()));
                        }
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

    // ChatGPT Usage: No
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

    // ChatGPT Usage: No
    public void enableLocButton(Button button, String attribute, TextView text_field, String listingId){
        button.setEnabled(true);
        button.setVisibility(View.VISIBLE);
        // Set Listener
        button.setOnClickListener(view -> {
            // Setting up Add Listing Prompt
            Context context = view.getContext();
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);

            final EditText latitude = new EditText(context);
            final EditText longitude = new EditText(context);

            latitude.setHint("Latitude as a Decimal between -180 and 180 (e.g. -15.2345). Optional.");
            longitude.setHint("Longitude as a Decimal between -180 and 180 (e.g. 123.1536). Optional.");
            layout.addView(latitude);
            layout.addView(longitude);
            alertDialogBuilder.setView(layout);
            alertDialogBuilder.setCancelable(true).setPositiveButton("Save", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                    try {
                        if (Double.parseDouble(String.valueOf(latitude.getText())) > 180 || Double.parseDouble(String.valueOf(latitude.getText())) < -180) {
                            Toast.makeText(context, "Latitude must be between -180 and 180.", Toast.LENGTH_LONG).show();
                        } else if (Double.parseDouble(String.valueOf(longitude.getText())) > 180 || Double.parseDouble(String.valueOf(longitude.getText())) < -180) {
                            Toast.makeText(context, "Longitude must be between -180 and 180.", Toast.LENGTH_LONG).show();
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(context, "Latitude and Longitude must be numerical. (i.e. 123.0000)", Toast.LENGTH_LONG).show();
                        return;
                    }

                    try {
                        double parsedLatitude = Double.parseDouble(String.valueOf(latitude.getText()));
                        double parsedLongitude =  Double.parseDouble(String.valueOf(longitude.getText()));
                        updateLocationText(client, view, ViewListingActivity.this, attribute,
                                listingId, text_field, String.format("(%.4f, %.4f)", parsedLatitude ,parsedLongitude),
                                    parsedLatitude, parsedLongitude);
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

    // ChatGPT Usage: No
    public void enablePriceButton(Button button, String attribute, TextView text_field, String listingId) {
        button.setEnabled(true);
        button.setVisibility(View.VISIBLE);
        // Set Listener
        button.setOnClickListener(view -> {
            // Setting up Add Listing Prompt
            Context context = view.getContext();
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);

            final EditText price = new EditText(context);

            price.setHint("Monthly rental price (in CAD). Must be numerical and in whole dollars (e.g. 1500).");
            layout.addView(price);
            alertDialogBuilder.setView(layout);
            alertDialogBuilder.setCancelable(true).setPositiveButton("Save", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                    try {
                        if (ListingsFragment.isNumeric(String.valueOf(price.getText())) && !String.valueOf(price.getText()).contains(".")) {
                            updateEditableText(client, view, ViewListingActivity.this, attribute, listingId, text_field, String.valueOf(price.getText()));
                        } else {
                            Toast.makeText(context, "Rental price must be numerical and in whole dollars (e.g. 1500).", Toast.LENGTH_LONG).show();
                        }
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

    // ChatGPT Usage: No
    public void disableButton(Button button){
        button.setEnabled(false);
        button.setVisibility(View.INVISIBLE);
    }

    // ChatGPT Usage: No
    public void updateEditableText(OkHttpClient client, View view, Activity act, String field,
                                   String listingId, TextView textview_to_update, String new_text) throws JSONException {
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
                    if (field.equals("rentalPrice")){
                        textview_to_update.setText(String.format("$%s/month", new_text));
                    } else {
                        textview_to_update.setText(new_text);
                    }
                    if (field.equals("title")){
                        Toast.makeText(view.getContext(), "Updated title! This change will show in the list of listings after the next refresh.", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    // ChatGPT Usage: No
    public void updateLocationText(OkHttpClient client, View view, Activity act, String field,
                                   String listingId, TextView textview_to_update, String new_text,
                                   double latitude, double longitude) throws JSONException {
        JSONObject location = new JSONObject();
        location.put("latitude", latitude);
        location.put("longitude", longitude);

        // Setting up a PUT request
        JSONObject json = new JSONObject();
        json.put(field, location);

        RequestBody formBody = RequestBody.create(MediaType.parse("application/json"), json.toString());

        Request request = new Request.Builder().url(Constants.baseServerURL + Constants.listingByListingIdEndpoint + listingId)
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

    // ChatGPT Usage: No
    public static int pxFromDp(Context context, float dp) {
        return (int)(dp * context.getResources().getDisplayMetrics().density);
    }

    // ChatGPT Usage: No
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
                Log.d(TAG, "Listing successfully reported! It will not show up next time onwards.");
            }
        });
    }

    // ChatGPT Usage: No
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

    // ChatGPT Usage: No
    public void deleteListing(OkHttpClient client, String listingId) {
        // Setting up a DELETE request
        Request request = new Request.Builder() // localhost:3000/api/listings/:listingId
                .url(Constants.baseServerURL + Constants.listingByListingIdEndpoint + listingId)
                .delete() // DELETE
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d(TAG, e.getMessage());
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                Log.d(TAG, "Listing successfully deleted!");
            }
        });
    }
    public void setupDeleteButton(String listingId) {
        deleteButton.setOnClickListener(view -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
            alertDialogBuilder.setTitle("Are you sure you want to delete this listing permanently?");
            alertDialogBuilder.setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                    disableButton(deleteButton);
                    deleteListing(client, listingId);
                    Toast.makeText(ViewListingActivity.this, "Listing deleted! This listing will no longer be recommended to others or be shown in your owned listings next time.", Toast.LENGTH_LONG).show();
                    finish();
                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        });
    }

    // ChatGPT Usage: No
    public double[] getLatestLocation(TextView location_textview){
        double[] result = new double[2];
        String curr_loc = String.valueOf(location_textview.getText());
        String[] lat_long_array = curr_loc.split(", ", 2);
        result[0] = Double.parseDouble(lat_long_array[0].substring(1));
        result[1] = Double.parseDouble(lat_long_array[1].substring(0, lat_long_array[1].length()-1));
        return result;
    }

    // ChatGPT Usage: No
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
                        String price = String.valueOf(result.getRentalPrice());
                        SingleListingResponseResult.LocationObj location = result.getLocation();
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();

                        // Instantiating TextViews
                        ImageView listing_image = findViewById(R.id.listing_picture);
                        TextView title_textview = findViewById(R.id.listing_name);
                        TextView description_textview = findViewById(R.id.listing_desc);
                        TextView housing_type_textview = findViewById(R.id.housing_type);
                        TextView listing_date_textview = findViewById(R.id.listing_date);
                        TextView move_in_date_textview = findViewById(R.id.move_in_date);
                        TextView pet_textview = findViewById(R.id.pet_friendly);
                        TextView location_textview = findViewById(R.id.location_coords);
                        TextView price_textview = findViewById(R.id.price);

                        // Instantiating Maps Button
                        mapButton = findViewById(R.id.map_button);
                        mapButton.setOnClickListener(view -> {
                            Intent mapsIntent = new Intent(ViewListingActivity.this, MapsActivity.class); // intent for maps
                            double[] curr_loc = getLatestLocation(location_textview);
                            Log.d(TAG, String.valueOf(curr_loc[0]));
                            Log.d(TAG, String.valueOf(curr_loc[1]));
                            Bundle b = new Bundle();
                            b.putString("title", title);
                            b.putDouble("latitude", curr_loc[0]);
                            b.putDouble("longitude", curr_loc[1]);
                            mapsIntent.putExtras(b);
                            startActivity(mapsIntent); // Go to maps
                        });

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
                        location_textview.setText(String.format("(%.4f, %.4f)", latitude, longitude));
                        price_textview.setText(String.format("$%s/month", price));

                        editTitleButton = findViewById(R.id.edit_title);
                        editHousingDescButton = findViewById(R.id.edit_housing_desc);
                        editHousingTypeButton = findViewById(R.id.edit_housing_type);
                        togglePetFriendlyButton = findViewById(R.id.edit_pet_friendly);
                        editMoveInButton = findViewById(R.id.edit_move_in_button);
                        editLatLongButton = findViewById(R.id.edit_lat_long);
                        editPriceButton = findViewById(R.id.edit_price);
                        reportButton = findViewById(R.id.report_button);
                        deleteButton = findViewById(R.id.delete_button);
                        disableButton(editMoveInButton);
                        if(!isOwner) {
                            disableButton(editTitleButton);
                            disableButton(editHousingDescButton);
                            disableButton(editHousingTypeButton);
                            disableButton(togglePetFriendlyButton);
                            disableButton(editLatLongButton);
                            disableButton(editPriceButton);
                            disableButton(deleteButton);
                            setupReportButton(listingId, userId);
                        }
                        else {
                            enableButton(editTitleButton, "title", title_textview, listingId);
                            enableButton(editHousingDescButton, "description", description_textview, listingId);
                            enableButton(editHousingTypeButton, "housingType", housing_type_textview, listingId);
                            enableToggle(togglePetFriendlyButton, "petFriendly", pet_textview, listingId);
                            enableLocButton(editLatLongButton, "location", location_textview, listingId);
                            enablePriceButton(editPriceButton,"rentalPrice", price_textview, listingId);
                            setupDeleteButton(listingId);
                            disableButton(reportButton);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }
}

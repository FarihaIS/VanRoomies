package com.chads.vanroomies;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;

public class ViewListingActivity extends AppCompatActivity {
    final static String TAG = "ViewListing";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_listing);

        Bundle b = getIntent().getExtras();
        if(b != null) {
            // Fetching Parameters
            HashMap<String, String> info = (HashMap<String, String>) b.getSerializable("info");
            String photo_string = b.getString("photo_string");
            String title = b.getString("title");
            String description = info.get("description");
            String housingType = info.get("housingType");
            String listingDate = info.get("listingDate").split(getString(R.string.datetime_regex), 2)[0];;
            String moveInDate = info.get("moveInDate").split(getString(R.string.datetime_regex), 2)[0];
            String petFriendly = info.get("petFriendly");

            // Instantiating TextViews
            ImageView listing_image = findViewById(R.id.listing_picture);
            TextView title_textview = findViewById(R.id.listing_name);
            TextView description_textview = findViewById(R.id.listing_desc);
            TextView housing_type_textview = findViewById(R.id.housing_type);
            TextView listing_date_textview = findViewById(R.id.listing_date);
            TextView move_in_date_textview = findViewById(R.id.move_in_date);
            TextView pet_textview = findViewById(R.id.pet_friendly);

            // Setting ImageView
            byte[] decodedString = Base64.decode(photo_string, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            listing_image.setImageBitmap(decodedByte);

            // Setting TextViews
            title_textview.setText(title);
            description_textview.setText(description);
            housing_type_textview.setText(housingType);
            listing_date_textview.setText(String.format("%s %s", getString(R.string.posted), listingDate));
            move_in_date_textview.setText(String.format("%s %s", getString(R.string.move), moveInDate));

            Log.d(TAG, listingDate);
            Log.d(TAG, moveInDate);
            Log.d(TAG, petFriendly);
            if(petFriendly.equals("true")){
                pet_textview.setText(String.format("%s %s", getString(R.string.pets), getString(R.string.allowed)));
            }
            else {
                pet_textview.setText(String.format("%s %s", getString(R.string.pets), getString(R.string.not_allowed)));
            }
        }
    }
}
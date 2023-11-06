package com.chads.vanroomies;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

public class MatchDeckAdapter extends BaseAdapter {
    private ArrayList<UserProfile> users;
    private Context context;

    public MatchDeckAdapter(Context context, ArrayList<UserProfile> data) {
        this.users = data;
        this.context = context;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            v = LayoutInflater.from(context).inflate(R.layout.matches_layout, parent, false);
        }
        if (users.get(position) == null) {
            Log.d("MatchDeckAdapter", "The user being fetched does not exist.");
        }
        String fullName = users.get(position).getFirstName() + " " + users.get(position).getLastName();
        ((TextView) v.findViewById(R.id.matches_name)).setText(fullName);
        ((TextView) v.findViewById(R.id.matches_bio)).setText(users.get(position).getBio());
        if (users.get(position).getProfilePicture() != null) {
            String imageString = users.get(position).getProfilePicture().toString().matches(Constants.base64Regex)
                    ? users.get(position).getProfilePicture().toString() : "";
            byte[] decodedString = Base64.decode(imageString, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            ((ImageView) v.findViewById(R.id.matches_image)).setImageBitmap(decodedByte);
        }
        else {
            ((ImageView) v.findViewById(R.id.matches_image)).setImageResource(R.drawable.ic_profile);
        }

        return v;
    }
}
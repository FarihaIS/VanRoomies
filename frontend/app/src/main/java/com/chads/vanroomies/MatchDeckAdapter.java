package com.chads.vanroomies;

import android.content.Context;
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

    public MatchDeckAdapter(ArrayList<UserProfile> data, Context context) {
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

        ((TextView) v.findViewById(R.id.matches_name)).setText(users.get(position).getName());
        ((TextView) v.findViewById(R.id.matches_age)).setText(String.valueOf(users.get(position).getAge()));
        ((TextView) v.findViewById(R.id.matches_preferences)).setText(users.get(position).getPreferences());
        // TODO: Figure out how to convert base64 string to image
        ((ImageView) v.findViewById(R.id.matches_image)).setImageResource(users.get(position).getImageString());

        return v;
    }
}
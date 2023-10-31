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
    private ArrayList<UserProfile> matchDeckUsers;
    private Context matchDeckContext;

    public MatchDeckAdapter(ArrayList<UserProfile> data, Context context) {
        this.matchDeckUsers = data;
        this.matchDeckContext = context;
    }

    @Override
    public int getCount() {
        return matchDeckUsers.size();
    }

    @Override
    public Object getItem(int position) {
        return matchDeckUsers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            v = LayoutInflater.from(matchDeckContext).inflate(R.layout.matches_layout, parent, false);
        }

        ((TextView) v.findViewById(R.id.matches_name)).setText(matchDeckUsers.get(position).getUserProfileName());
        ((TextView) v.findViewById(R.id.matches_age)).setText(String.valueOf(matchDeckUsers.get(position).getUserProfileAge()));
        ((TextView) v.findViewById(R.id.matches_preferences)).setText(matchDeckUsers.get(position).getUserProfilePreferences());
        ((ImageView) v.findViewById(R.id.matches_image)).setImageResource(matchDeckUsers.get(position).getUserProfileImageId());

        return v;
    }
}
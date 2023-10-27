package com.chads.vanroomies;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MatchDeckAdapter extends BaseAdapter {
    private ArrayList<MatchModal> matchData;
    private Context context;

    public MatchDeckAdapter(ArrayList<MatchModal> data, Context context) {
        this.matchData = data;
        this.context = context;
    }
    @Override
    public int getCount() {
        return matchData.size();
    }

    @Override
    public Object getItem(int position) {
        return matchData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.matches_layout, parent, false);
        }

        ((TextView) v.findViewById(R.id.matches_name)).setText(matchData.get(position).getMatchName());
        ((TextView) v.findViewById(R.id.matches_age)).setText(String.valueOf(matchData.get(position).getMatchAge()));
        ((TextView) v.findViewById(R.id.matches_preferences)).setText(matchData.get(position).getMatchPreferences());
        ((ImageView) v.findViewById(R.id.matches_image)).setImageResource(matchData.get(position).getMatchImageId());

        return v;
    }
}

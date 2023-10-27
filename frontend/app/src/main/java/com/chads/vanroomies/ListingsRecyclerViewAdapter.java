package com.chads.vanroomies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class ListingsRecyclerViewAdapter extends RecyclerView.Adapter<ListingsRecyclerViewAdapter.RecyclerViewHolder> {

    private ArrayList<ListingsRecyclerData> listingsDataArrayList;
    private Context mcontext;

    public ListingsRecyclerViewAdapter(ArrayList<ListingsRecyclerData> recyclerDataArrayList, Context mcontext) {
        this.listingsDataArrayList = recyclerDataArrayList;
        this.mcontext = mcontext;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate Layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listings_layout, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        // Set the data to textview and imageview.
        ListingsRecyclerData recyclerData = listingsDataArrayList.get(position);
        holder.listingTV.setText(recyclerData.getTitle());
        holder.listingIV.setImageResource(recyclerData.getImgId());
    }

    @Override
    public int getItemCount() {
        return listingsDataArrayList.size();
    }

    // View Holder Class to handle Recycler View.
    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private TextView listingTV;
        private ImageView listingIV;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            listingTV = itemView.findViewById(R.id.idTVListing);
            listingIV = itemView.findViewById(R.id.idIVListing);
        }
    }
}

package com.chads.vanroomies;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

// ChatGPT usage: No
public class ListingsRecyclerViewAdapter extends RecyclerView.Adapter<ListingsRecyclerViewAdapter.RecyclerViewHolder> {

    private ArrayList<ListingsRecyclerData> listingsDataArrayList;
    private ListingsItemSelectListener listener;
    private Context mcontext;

    public ListingsRecyclerViewAdapter(ArrayList<ListingsRecyclerData> recyclerDataArrayList, ListingsItemSelectListener listener, Context mcontext) {
        this.listingsDataArrayList = recyclerDataArrayList;
        this.listener = listener;
        this.mcontext = mcontext;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate Layout
        View view = LayoutInflater.from(mcontext).inflate(R.layout.listings_layout, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        // Set the data to textview and imageview.
        ListingsRecyclerData recyclerData = listingsDataArrayList.get(position);
        holder.listingTV.setText(recyclerData.getTitle());

        String imageString = recyclerData.getImageString().matches(Constants.base64Regex)
                ? recyclerData.getImageString() : "";
        byte[] decodedString = Base64.decode(imageString, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        holder.listingIV.setImageBitmap(decodedByte);

        holder.listingCard.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                listener.onItemClicked(recyclerData);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listingsDataArrayList.size();
    }

    // View Holder Class to handle Recycler View.
    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private TextView listingTV;
        private ImageView listingIV;
        private CardView listingCard;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            listingTV = itemView.findViewById(R.id.idTVListing);
            listingIV = itemView.findViewById(R.id.idIVListing);
            listingCard = itemView.findViewById(R.id.listingCard);
        }
    }
}

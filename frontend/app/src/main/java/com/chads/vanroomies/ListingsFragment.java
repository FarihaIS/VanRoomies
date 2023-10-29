package com.chads.vanroomies;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListingsFragment extends Fragment {
    final static String TAG = "ListingsFragment";
    OkHttpClient client;
    final static Gson g = new Gson();
    final static int num_cols = 2;
    private RecyclerView recyclerView;
    private ArrayList<ListingsRecyclerData> recyclerDataArrayList;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ListingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListingsFragment newInstance(String param1, String param2) {
        ListingsFragment fragment = new ListingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listings, container, false);
        recyclerView = view.findViewById(R.id.idListingsRV);

        client = HTTPSClientFactory.createClient(getActivity().getApplication());
        // TODO: Maintain user_id within the app and use it as an input here

        getRecommendedListings(client, getActivity(), "653db98f48a54c10b096a61f");
        recyclerDataArrayList = new ArrayList<>();

        // To-Do: Populate with data from the backend (Issue #39). Currently using dummy data
        recyclerDataArrayList.add(new ListingsRecyclerData("House", R.drawable.ic_listings_image));
        recyclerDataArrayList.add(new ListingsRecyclerData("Casa", R.drawable.ic_listings_image));
        recyclerDataArrayList.add(new ListingsRecyclerData("Maison", R.drawable.ic_listings_image));
        recyclerDataArrayList.add(new ListingsRecyclerData("Haus", R.drawable.ic_listings_image));
        recyclerDataArrayList.add(new ListingsRecyclerData("家", R.drawable.ic_listings_image));

        // added data from arraylist to adapter class.
        ListingsRecyclerViewAdapter adapter = new ListingsRecyclerViewAdapter(recyclerDataArrayList,view.getContext());

        // setting grid layout manager to implement grid view.
        // in this method '2' represents number of columns to be displayed in grid view.
        GridLayoutManager layoutManager = new GridLayoutManager(view.getContext(),num_cols);

        // at last set adapter to recycler view.
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        return view;
    }

    public static void getRecommendedListings(OkHttpClient client, Activity act, String user_id){
        Request request = new Request.Builder().url(Constants.BaseServerURL + Constants.listingByUserIdEndpoint + user_id).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d(TAG, e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                act.runOnUiThread(() -> {
                    try {
                        String responseData = response.body().string();

                        List<Map<String, Object>> result = g.fromJson(responseData, List.class);

                        for (int index = 0; index < result.size(); index++){
                            Map<String, Object> listing = result.get(index);
                            Log.d(TAG, listing.get("title").toString());
                        }
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                });
            }
        });
    }
}
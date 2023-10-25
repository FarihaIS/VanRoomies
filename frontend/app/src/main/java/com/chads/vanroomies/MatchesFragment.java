package com.chads.vanroomies;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import com.daprlabs.cardstack.SwipeDeck;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MatchesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MatchesFragment extends Fragment {
    final static String TAG = "MatchesFragment";
    private SwipeDeck cardStack;
    private ArrayList<MatchModal> matchModalArrayList;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MatchesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MatchesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MatchesFragment newInstance(String param1, String param2) {
        MatchesFragment fragment = new MatchesFragment();
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

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_matches, container, false);

        matchModalArrayList = new ArrayList<>();
        cardStack = (SwipeDeck) v.findViewById(R.id.matches_swipe_deck);

        // Example matches for now
        matchModalArrayList.add(new MatchModal("Denis", 45, "Late-night owl, messy, smokes regularly", R.drawable.ic_listings));
        matchModalArrayList.add(new MatchModal("Fariha", 22, "Early riser, clean, 2-bedroom house", R.drawable.ic_profile));
        matchModalArrayList.add(new MatchModal("Matt", 30, "Early-riser, drinks regularly", R.drawable.ic_match));
        matchModalArrayList.add(new MatchModal("Max", 83, "Early-riser, clean, no smoking, no drinking", R.drawable.ic_chat));

        final MatchDeckAdapter adapter = new MatchDeckAdapter(matchModalArrayList, v.getContext());
        cardStack.setAdapter(adapter);

        cardStack.setEventCallback(new SwipeDeck.SwipeEventCallback() {
            @Override
            public void cardSwipedLeft(int position) {
                Toast.makeText(v.getContext(), "Match Rejected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void cardSwipedRight(int position) {
                Toast.makeText(v.getContext(), "Match Accepted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void cardsDepleted() {
                Toast.makeText(v.getContext(), "No more matches", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void cardActionDown() {
                Log.d(TAG, "CARDS MOVED DOWN");
            }

            @Override
            public void cardActionUp() {
                Log.d(TAG, "CARDS MOVED UP");
            }
        });

        return v;
    }
}
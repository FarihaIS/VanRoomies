package com.chads.vanroomies;

// Reference: https://www.geeksforgeeks.org/tinder-swipe-view-with-example-in-android/
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private ArrayList<UserProfile> userMatches;
    private MatchDeckAdapter matchDeckAdapter;
    private SwipeDeck cardStack;

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
        View v = inflater.inflate(R.layout.fragment_matches, container, false);
        userMatches = new ArrayList<>();
        cardStack = v.findViewById(R.id.matches_swipe_deck);

        // TODO: do GET request to show updated list of matched users
        userMatches.add(new UserProfile("abc00", "Denis", 45, "Late-night owl, messy, smokes regularly", ""));
        userMatches.add(new UserProfile("def11", "Fariha", 23, "Early riser, clean, 2-bedroom house", ""));
        userMatches.add(new UserProfile("ghi22", "Matt", 30, "Early-riser, drinks regularly", ""));
        userMatches.add(new UserProfile("jkl33", "Max", 83, "Early-riser, clean, no smoking, no drinking", ""));

        updateMatchesFragmentLayout(v);

        return v;
    }

    private void updateMatchesFragmentLayout(View v) {
        matchDeckAdapter = new MatchDeckAdapter(userMatches, v.getContext());
        cardStack.setAdapter(matchDeckAdapter);

        cardStack.setEventCallback(new SwipeDeck.SwipeEventCallback() {
            @Override
            public void cardSwipedLeft(int position) {
                Log.d(TAG, "Match Rejected");
            }

            @Override
            public void cardSwipedRight(int position) {
                // TODO: Opem chat session with accepted match
                Log.d(TAG, "Match accepted");
            }

            @Override
            public void cardsDepleted() {
                Log.d(TAG, "No more matches");
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
    }
}
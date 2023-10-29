package com.chads.vanroomies;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {
    final static String TAG = "ChatFragment";
    private RecyclerView chatListRecycler;
    private ChatListAdapter chatListAdapter;
    private ArrayList<UserProfile> chatList;
    private String userId;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatFragment newInstance(String param1, String param2) {
        ChatFragment fragment = new ChatFragment();
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
        View v = inflater.inflate(R.layout.fragment_chat, container, false);

        // TODO: Get userId from backend
        userId = "bvshdjgf839w479q";

        // TODO: Get chatList from backend
        chatList = new ArrayList<>();
        chatList.add(new UserProfile("hbdf239487", "Mr.Shrek", 1));
        chatList.add(new UserProfile("adjh24738", "Mr.Donkey", 2));
        chatList.add(new UserProfile("poeiruhfdj7475427", "Ms.Fiona", 3));
        chatList.add(new UserProfile("jjkfgdfjk8767839", "Lord Farquaad", 4));

        chatListRecycler = (RecyclerView) v.findViewById(R.id.chatlistrecycle);
        chatListAdapter = new ChatListAdapter(v.getContext(), chatList, userId);
        chatListRecycler.setAdapter((chatListAdapter));

        return v;
    }
}
package com.example.ichat.tabItems;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.ichat.Post_Details.reelAdapter;
import com.example.ichat.Post_Details.reelModel;
import com.example.ichat.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link reel_frage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class reel_frage extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ViewPager2 viewPager2;
    reelAdapter reelAdapter;
    ArrayList<reelModel> list;
    DatabaseReference reference;

    public reel_frage() {
        // Required empty public constructor
    }

    public static reel_frage newInstance(String param1, String param2) {
        reel_frage fragment = new reel_frage();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reel_frage, container, false);
        reference = FirebaseDatabase.getInstance().getReference();
        viewPager2 = view.findViewById(R.id.viewPager_reel);
        list = new ArrayList<>();
        viewPager2.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
        reelAdapter = new reelAdapter(getContext(), list);


        if (list != null) {
            reference.child("User_Post_Reels").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        reelModel model = dataSnapshot.getValue(reelModel.class);
                        list.add(model);
                    }
                    reelAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            Toast.makeText(getContext(), "Empty...", Toast.LENGTH_SHORT).show();
        }
        viewPager2.setAdapter(reelAdapter);
        return view;
    }
}
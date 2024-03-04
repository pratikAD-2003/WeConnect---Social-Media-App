package com.example.ichat.Share_Maintain;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.ichat.Post_Details.postModel;
import com.example.ichat.Post_Maintainence.followingAdapter;
import com.example.ichat.Post_Maintainence.followingModel;
import com.example.ichat.ProfileModel;
import com.example.ichat.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link shareListPost#newInstance} factory method to
 * create an instance of this fragment.
 */
public class shareListPost extends BottomSheetDialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    RecyclerView recyclerView;
    ArrayList<followingModel> list;
    shareListAdapter adapter;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    DatabaseReference reference;
    RecyclerView.LayoutManager layoutManager;
    EditText searchUsers;

    public shareListPost() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment shareListPost.
     */
    // TODO: Rename and change types and number of parameters
    public static shareListPost newInstance(String param1, String param2) {
        shareListPost fragment = new shareListPost();
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
        View view = inflater.inflate(R.layout.fragment_share_list_post, container, false);
        recyclerView = view.findViewById(R.id.share_post_recyclerview);
        searchUsers = view.findViewById(R.id.usersToShare);
        list = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference();

        layoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new shareListAdapter(getContext(), list, getActivity());
        recyclerView.setAdapter(adapter);

        if (list != null) {
            reference.child("Followed").child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot d : snapshot.getChildren()) {
                        followingModel followingModel = d.getValue(followingModel.class);
                        list.add(followingModel);
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        searchUsers.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

        return view;
    }

    void filter(String text) {
        List<followingModel> temp = new ArrayList();
        for (followingModel d : list) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if (d.getUsername().contains(text.toLowerCase())) {
                temp.add(d);
            }
        }
        //update recyclerview
        adapter.updateList(temp);
    }
}
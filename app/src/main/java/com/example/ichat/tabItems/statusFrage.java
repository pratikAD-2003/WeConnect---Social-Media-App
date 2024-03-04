package com.example.ichat.tabItems;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ichat.Post_Details.postAdapter;
import com.example.ichat.Post_Details.postModel;
import com.example.ichat.R;
import com.example.ichat.UsersActivities.search_users_onApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class statusFrage extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    RecyclerView recyclerView;
    postAdapter adapter;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    ArrayList<postModel> list;
    FirebaseFirestore db;
    LinearLayoutManager layoutManager;
    DatabaseReference reference, reference2;
    String profileUri = "";
    ImageView  showMessageBox, inboxIndicator, liveActivities;
    ProgressBar progressBar;

    LinearLayoutManager layoutManager2;
    TextView  post_empty;

    public statusFrage() {
        // Required empty public constructor
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
        View view = inflater.inflate(R.layout.fragment_status_frage, container, false);
        recyclerView = view.findViewById(R.id.post_recyclerview);
        showMessageBox = view.findViewById(R.id.show_message_box);
        liveActivities = view.findViewById(R.id.users_activity_bar);
        inboxIndicator = view.findViewById(R.id.inbox_message_indicator);
        progressBar = view.findViewById(R.id.post_loading_bar);
        post_empty = view.findViewById(R.id.check_posts_list_empty);
        list = new ArrayList<>();

        SharedPreferences sp = getContext().getSharedPreferences("UserProfileData", MODE_PRIVATE);
        profileUri = sp.getString("profileUri", "not");

        reference = FirebaseDatabase.getInstance().getReference();
        reference2 = FirebaseDatabase.getInstance().getReference();

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setHasFixedSize(true);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new postAdapter(getContext(), list, getActivity());
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.custom_divider_2));
        recyclerView.addItemDecoration(dividerItemDecoration);

        if (list != null) {
            reference.child("User_Post_Images").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        progressBar.setVisibility(View.GONE);
                        post_empty.setVisibility(View.VISIBLE);
                    } else {
                        for (DataSnapshot d : snapshot.getChildren()) {
                            progressBar.setVisibility(View.GONE);
                            post_empty.setVisibility(View.GONE);
                            postModel model = d.getValue(postModel.class);
                            list.add(model);
                        }
                        adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Go Online!", Toast.LENGTH_SHORT).show();
                }
            });
        }

        showMessageBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), showChatListUsers.class));
            }
        });

        liveActivities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), search_users_onApp.class));
            }
        });
        return view;
    }
}

package com.example.ichat.tabItems;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ichat.LogoutUser.logout;
import com.example.ichat.OtherUserProfileFollow;
import com.example.ichat.Post_Details.OurPostAdapter;
import com.example.ichat.Post_Details.postModel;
import com.example.ichat.Post_Maintainence.followedList;
import com.example.ichat.Post_Maintainence.followingList;
import com.example.ichat.Post_Maintainence.saved_posts;
import com.example.ichat.R;
import com.example.ichat.userDetails;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class callFrage extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Button editProfile;
    RecyclerView recyclerView;
    OurPostAdapter adapter;
    CircleImageView user_profile_pic;
    TextView User_name, User_description, onDisplayName, post, followers, followings;

    LinearLayoutCompat checkConnectsUsers, checkConnectedUsers;
    DatabaseReference root = FirebaseDatabase.getInstance().getReference();

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    ArrayList<postModel> list;
    ProgressBar progressBar;
    NavigationView navigationView;
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    LinearLayoutManager layoutManager;
    Toolbar toolbar;
    TextView our_post_empty;

    public callFrage() {
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
        View view = inflater.inflate(R.layout.fragment_call_frage, container, false);
        editProfile = view.findViewById(R.id.edit_update_profile);
        user_profile_pic = view.findViewById(R.id.user_profile_pic_profilePage);
        User_name = view.findViewById(R.id.on_display_name);
        User_description = view.findViewById(R.id.about_user_profile);
        progressBar = view.findViewById(R.id.our_post_loading_bar);
        our_post_empty = view.findViewById(R.id.our_post_empty_indicator);
//        onDisplayName = view.findViewById(R.id.user_name_profilePage);
        post = view.findViewById(R.id.our_post_count);
        followers = view.findViewById(R.id.our_follower_count);
        followings = view.findViewById(R.id.our_following_count);
        checkConnectsUsers = view.findViewById(R.id.check_connects_user);
        checkConnectedUsers = view.findViewById(R.id.check_connected_user);
        recyclerView = view.findViewById(R.id.our_post_recyclerview);

        navigationView = view.findViewById(R.id.navi_view);
        toolbar = view.findViewById(R.id.toolbar);
        drawerLayout = view.findViewById(R.id.drawer);

        list = new ArrayList<>();
        layoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setHasFixedSize(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new OurPostAdapter(getContext(), list);
        recyclerView.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        DividerItemDecoration dividerItemDecoration2 = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.HORIZONTAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.custom_divider_3));
        dividerItemDecoration2.setDrawable(getResources().getDrawable(R.drawable.custom_divider_3));
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.addItemDecoration(dividerItemDecoration2);

        if (list != null) {
            root.child("OurPosts").child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        our_post_empty.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                    for (DataSnapshot d : snapshot.getChildren()) {
                        progressBar.setVisibility(View.GONE);
                        our_post_empty.setVisibility(View.GONE);
                        postModel postModel = d.getValue(postModel.class);
                        list.add(postModel);
                        Collections.reverse(list);
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), userDetails.class));
            }
        });

        SharedPreferences sp = getActivity().getSharedPreferences("UserProfileData", MODE_PRIVATE);
        User_name.setText(sp.getString("DisplayName", "Please Set Username."));
//        onDisplayName.setText(sp.getString("username", "Please Set Username."));
        User_description.setText(sp.getString("about", "Please Set About Yourself."));
        Glide.with(getActivity()).load(sp.getString("profileUri", "Invalid.")).into(user_profile_pic);

        getFollowStatus();
        getFollowingStatus();
        getPostCount();

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
//        Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(sp.getString("username", "Please Set Username."));

        actionBarDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.saved_btn) {
                    Intent intent = new Intent(getContext(), saved_posts.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else if (id == R.id.settings_btn) {
                    logout logout = new logout();
                    logout.show(getActivity().getSupportFragmentManager(), getTag());
                }
                drawerLayout.closeDrawer(GravityCompat.END);
                return true;
            }
        });

        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), followingList.class);
                intent.putExtra("RECEIVER_ID", uid);
                startActivity(intent);
            }
        });

        followings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), followedList.class);
                intent.putExtra("RECEIVER_ID", uid);
                startActivity(intent);
            }
        });
        return view;
    }

    public void getFollowStatus() {
        root.child("Followed").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    int followingCount = (int) snapshot.child(uid).getChildrenCount();
                    followings.setText("\t" + followingCount);
                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getFollowingStatus() {
        root.child("Following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    int followingCount = (int) snapshot.child(uid).getChildrenCount();
                    followers.setText("\t" + followingCount);
                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getPostCount() {
        root.child("OurPosts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    int postCount = (int) snapshot.child(uid).getChildrenCount();
                    post.setText("\t" + postCount);
                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
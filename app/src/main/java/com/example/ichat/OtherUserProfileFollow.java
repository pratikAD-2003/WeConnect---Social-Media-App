package com.example.ichat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ichat.Post_Details.OurPostAdapter;
import com.example.ichat.Post_Details.postModel;
import com.example.ichat.Post_Maintainence.followedList;
import com.example.ichat.Post_Maintainence.followingList;
import com.example.ichat.Post_Maintainence.followingModel;
import com.example.ichat.PushNotification.FcmNotificationsSender;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirestoreRegistrar;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class OtherUserProfileFollow extends AppCompatActivity {

    CircleImageView otherProfilePic;
    TextView otherPosts, otherConnected, otherToConnect, otherDisplayName, otherDes, otherUserName;
    Button connects_other_btn, chat_other_user;
    String oppositeAbout = "", oppositeNum = "", oppositeUserName = "", oppositeUri = "", receiverUid = "", oppositeFcm = "", displayName = "";
    String About = "", Num = "", UserName = "", profileUri = "", Fcm = "", dName = "";

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();

    DatabaseReference reference;
    RecyclerView recyclerView;
    ArrayList<postModel> list;
    OurPostAdapter adapter;
    RecyclerView.LayoutManager layoutManager;
    boolean checkF = false;
    ProgressBar progressBar;
    boolean checkOnline = false;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_user_profile_follow);
        otherProfilePic = findViewById(R.id.other_user_pro_pic);
        otherPosts = findViewById(R.id.other_user_posts);
        otherConnected = findViewById(R.id.other_user_connected_count);
        otherToConnect = findViewById(R.id.other_user_toConnects_count);
        otherDisplayName = findViewById(R.id.other_user_name);
        otherDes = findViewById(R.id.other_user_description);
        connects_other_btn = findViewById(R.id.other_user_connect_btn);
        chat_other_user = findViewById(R.id.other_user_chat);
        otherUserName = findViewById(R.id.other_user_username);
        progressBar = findViewById(R.id.other_post_loading_bar);

        list = new ArrayList<>();
        recyclerView = findViewById(R.id.other_user_posts_recyclerview);
        layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new OurPostAdapter(this, list);
        recyclerView.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        DividerItemDecoration dividerItemDecoration2 = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.HORIZONTAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.custom_divider_3));
        dividerItemDecoration2.setDrawable(getResources().getDrawable(R.drawable.custom_divider_3));
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.addItemDecoration(dividerItemDecoration2);

        SharedPreferences sp = getSharedPreferences("UserProfileData", MODE_PRIVATE);
        UserName = sp.getString("username", "Please Set Username.");
        dName = sp.getString("DisplayName", "Default");
        About = sp.getString("about", "Please Set About Yourself.");
        Num = sp.getString("number", "+91XXXXXXXXXX");
        profileUri = sp.getString("profileUri", "Invalid.");
        Fcm = sp.getString("fcmToken", "not");


        oppositeUserName = getIntent().getStringExtra("perUserN");
        oppositeAbout = getIntent().getStringExtra("aboutUser");
        oppositeNum = getIntent().getStringExtra("userNumber");
        oppositeUri = getIntent().getStringExtra("ProfileUri");
        receiverUid = getIntent().getStringExtra("userUid");
        oppositeFcm = getIntent().getStringExtra("FCM_TOKEN");
        displayName = getIntent().getStringExtra("DisplayName");

        reference = FirebaseDatabase.getInstance().getReference();
        db = FirebaseFirestore.getInstance();

        chat_other_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), chat_screen.class);
                intent.putExtra("USER_NAME", oppositeUserName);
                intent.putExtra("D_NAME", displayName);
                intent.putExtra("ABOUT_USER", oppositeAbout);
                intent.putExtra("RECEIVER_UID", receiverUid);
                intent.putExtra("USER_NUMBER", oppositeNum);
                intent.putExtra("PROFILE_URI", oppositeUri);
                intent.putExtra("FCM_TOKEN_FOR_NOTI", oppositeFcm);
                startActivity(intent);
            }
        });

        otherUserName.setText(oppositeUserName);
        otherDisplayName.setText(displayName);
        otherDes.setText(oppositeAbout);
        Glide.with(this).load(oppositeUri).into(otherProfilePic);

        getFollowStatus(uid, receiverUid);
        getFollowingStatus();
        getSelfCheck();
        getOurPosts();
        getPostCount();
        onAppStatus(receiverUid);

        connects_other_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkF = true;
                try {
                    reference.child("Followed").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (checkF) {
                                if (snapshot.child(uid).hasChild(receiverUid)) {
                                    reference.child("Followed").child(uid).child(receiverUid).removeValue();
                                    reference.child("Following").child(receiverUid).child(uid).removeValue();
                                    checkF = false;
                                } else {
                                    followingModel model = new followingModel(oppositeUserName, oppositeAbout, oppositeUri, receiverUid, oppositeFcm, displayName);
                                    followingModel model2 = new followingModel(UserName, About, profileUri, uid, Fcm, dName);
                                    reference.child("Followed").child(uid).child(receiverUid).setValue(model);
                                    reference.child("Following").child(receiverUid).child(uid).setValue(model2);
                                    if (!checkOnline) {
                                        sendNotification(UserName + " send connection request.", oppositeFcm, "WeConnect");
                                    }
                                    checkF = false;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } catch (Exception e) {

                }
            }
        });


        otherConnected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OtherUserProfileFollow.this, followingList.class);
                intent.putExtra("RECEIVER_ID", receiverUid);
                startActivity(intent);
            }
        });

        otherToConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OtherUserProfileFollow.this, followedList.class);
                intent.putExtra("RECEIVER_ID", receiverUid);
                startActivity(intent);
            }
        });

    }

    public void getFollowStatus(String UID, String RECEIVER) {
        reference.child("Followed").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    if (snapshot.child(UID).hasChild(RECEIVER)) {
                        connects_other_btn.setBackgroundResource(R.drawable.followed_btn);
                        connects_other_btn.setText("Connected");
                        int followingCount = (int) snapshot.child(receiverUid).getChildrenCount();
                        otherToConnect.setText("\t" + followingCount);
                    } else {
                        connects_other_btn.setBackgroundResource(R.drawable.unfollow_btn);
                        connects_other_btn.setText("Connect");
                        int followingCount = (int) snapshot.child(receiverUid).getChildrenCount();
                        otherToConnect.setText("\t" + followingCount);
                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getFollowingStatus() {
        reference.child("Following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    if (snapshot.child(uid).hasChild(receiverUid)) {
                        int followingCount = (int) snapshot.child(receiverUid).getChildrenCount();
                        otherConnected.setText("\t" + followingCount);
                    } else {
                        int followingCount = (int) snapshot.child(receiverUid).getChildrenCount();
                        otherConnected.setText("\t" + followingCount);
                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getSelfCheck() {
        reference.child("Following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    if (snapshot.child(uid).hasChild(uid)) {
                        Toast.makeText(OtherUserProfileFollow.this, "Unreachable Statement!", Toast.LENGTH_SHORT).show();
                        reference.child("Followed").child(uid).child(uid).removeValue();
                        reference.child("Following").child(uid).child(uid).removeValue();
                        connects_other_btn.setBackgroundResource(R.drawable.followed_btn);
                        connects_other_btn.setText("Connected");
                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getOurPosts() {
        reference.child("OurPosts").child(receiverUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot d : snapshot.getChildren()) {
                    progressBar.setVisibility(View.GONE);
                    postModel postModel = d.getValue(com.example.ichat.Post_Details.postModel.class);
                    list.add(postModel);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getPostCount() {
        reference.child("OurPosts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    int postCount = (int) snapshot.child(receiverUid).getChildrenCount();
                    otherPosts.setText("\t" + postCount);
                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void sendNotification(String message, String otherFcm, String username) {
        FcmNotificationsSender notificationsSender = new FcmNotificationsSender(otherFcm, username, message, this, OtherUserProfileFollow.this);
        notificationsSender.SendNotifications();
    }

    public void onAppStatus(String id) {
        reference.child("OnAppStatus").child(id).child("active").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String check = snapshot.getValue(String.class);
                if (Objects.equals(check, "true")) {
                    checkOnline = true;
                } else {
                    checkOnline = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
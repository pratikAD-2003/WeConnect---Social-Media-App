package com.example.ichat.SendItemsProcess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ichat.Post_Maintainence.comment;
import com.example.ichat.Post_Maintainence.followingModel;
import com.example.ichat.R;
import com.example.ichat.Share_Maintain.BottomSheetDialog;
import com.example.ichat.Share_Maintain.shareListAdapter;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class Sender_reel_items extends AppCompatActivity {
    ExoPlayer exoPlayer;
    int videoSound = 0;
    boolean checkMute = false;
    DatabaseReference reference;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();

    boolean check_likes = false;
    boolean check_follow = false;
    ImageButton likeReel, CommentReel, ShareReel;
    TextView like_count, reel_user_name, caption;
    Button follow;
    CircleImageView user_profile;
    String username, userPic, reelCaption, userId, reelURI, postKey, display, fcmToken, about, reelTime;

    String About = "", Num = "", UserName = "", profileUri = "", Fcm = "", dName = "";
    StyledPlayerView playerView;
    ImageView soundCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sender_reel_items);
        likeReel = findViewById(R.id.sender_reel_post_likes);
        like_count = findViewById(R.id.sender_reel_post_likes_count);
        follow = findViewById(R.id.sender_reel_post_follow_btn);
        user_profile = findViewById(R.id.sender_reel_user_pic);
        reel_user_name = findViewById(R.id.sender_reel_user_name);
        caption = findViewById(R.id.sender_reel_user_title);
        playerView = findViewById(R.id.sender_reel_player);
        soundCheck = findViewById(R.id.sender_reel_sound_check_anim);
        CommentReel = findViewById(R.id.sender_reel_comment_btn);
        ShareReel = findViewById(R.id.sender_reel_share_btn);

        reference = FirebaseDatabase.getInstance().getReference();

        SharedPreferences sp = getSharedPreferences("UserProfileData", MODE_PRIVATE);
        UserName = sp.getString("username", "Please Set Username.");
        dName = sp.getString("DisplayName", "Default");
        About = sp.getString("about", "Please Set About Yourself.");
        Num = sp.getString("number", "+91XXXXXXXXXX");
        profileUri = sp.getString("profileUri", "Invalid.");
        Fcm = sp.getString("fcmToken", "not");

        username = getIntent().getStringExtra("reelUserName");
        userPic = getIntent().getStringExtra("reelUserPic");
        userId = getIntent().getStringExtra("userId_reel");
        reelCaption = getIntent().getStringExtra("reelTitle");
        reelURI = getIntent().getStringExtra("reelURI");
        postKey = getIntent().getStringExtra("reelPostKey");
        display = getIntent().getStringExtra("displayName");
        fcmToken = getIntent().getStringExtra("reel_FCM");
        about = getIntent().getStringExtra("reel_AboutUser");
        reelTime = getIntent().getStringExtra("reel_Time");

        SharedPreferences sp2 = getSharedPreferences("SendPostData", MODE_PRIVATE);
        SharedPreferences.Editor ed = sp2.edit();


        Glide.with(this).load(userPic).into(user_profile);
        reel_user_name.setText(username);
        caption.setText(reelCaption);

        exoPlayer = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(exoPlayer);
        MediaItem mediaItem = MediaItem.fromUri(reelURI);
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.prepare();
        exoPlayer.setRepeatMode(Player.REPEAT_MODE_ONE);
        exoPlayer.setPlayWhenReady(true);
        videoSound = (int) exoPlayer.getVolume();
        playerView.hideController();


        likeReel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check_likes = true;
                try {
                    reference.child("Post_likes").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (check_likes == true) {

                                if (snapshot.child(postKey).hasChild(uid)) {
                                    reference.child("Post_likes").child(postKey).child(uid).removeValue();
                                    check_likes = false;
                                } else {
                                    reference.child("Post_likes").child(postKey).child(uid).setValue(true);
                                    check_likes = false;
                                }

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } catch (Exception d) {

                }
            }
        });

        playerView.setOnTouchListener(new View.OnTouchListener() {

            GestureDetector gestureDetector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(@NonNull MotionEvent e) {
                    if (!checkMute) {
                        exoPlayer.setVolume(videoSound);
                        checkMute = true;
                        soundCheck.setVisibility(View.VISIBLE);
                        soundCheck.setImageResource(R.drawable.unmute);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                soundCheck.setVisibility(View.GONE);
                            }
                        }, 1000);
                    } else {
                        exoPlayer.setVolume(0f);
                        checkMute = false;
                        soundCheck.setVisibility(View.VISIBLE);
                        soundCheck.setImageResource(R.drawable.mute);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                soundCheck.setVisibility(View.GONE);
                            }
                        }, 1000);
                    }
                    return super.onSingleTapUp(e);
                }

            });

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    // start recording
                    exoPlayer.setPlayWhenReady(false);
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    // stop recording
                    exoPlayer.setPlayWhenReady(true);
                }
                gestureDetector.onTouchEvent(motionEvent);
                return true;

            }
        });

        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check_follow = true;
                try {
                    reference.child("Followed").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (check_follow) {
                                if (snapshot.child(uid).hasChild(userId)) {
                                    reference.child("Followed").child(uid).child(userId).removeValue();
                                    reference.child("Following").child(userId).child(uid).removeValue();
                                    check_follow = false;
                                } else {
                                    followingModel model = new followingModel(username, about, userPic, userId, fcmToken, display);
                                    followingModel model2 = new followingModel(UserName, About, profileUri, uid, Fcm, dName);
                                    reference.child("Followed").child(uid).child(userId).setValue(model);
                                    reference.child("Following").child(userId).child(uid).setValue(model2);
                                    check_follow = false;
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

        CommentReel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ed.putString("share_post_comment", postKey);
                ed.apply();

                comment comment = new comment();
                comment.show(getSupportFragmentManager(), comment.getTag());
            }
        });

        ShareReel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ed.putString("share_post_username", username);
                ed.putString("share_post_display", display);
                ed.putString("share_post_about", about);
                ed.putString("share_post_profile_uri", userPic);
                ed.putString("share_post_fcm", fcmToken);
                ed.putString("share_post_postUri", reelURI);
                ed.putString("share_post_caption", reelCaption);
                ed.putString("share_post_date", reelTime);
                ed.putString("share_post_uid", userId);
                ed.putString("share_post_key", postKey);
                ed.apply();

                Intent intent = new Intent(Sender_reel_items.this, shareListAdapter.class);

                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog();
                bottomSheetDialog.show(getSupportFragmentManager(), bottomSheetDialog.getTag());
            }
        });

        getFollowStatus(uid, userId);
        getSelfCheck(uid);
        getLikesStatus(postKey, uid);
    }

    public void getLikesStatus(String postKey, String UID) {
        try {
            reference.child("Post_likes").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child(postKey).hasChild(UID)) {
                        likeReel.setImageResource(R.drawable.heart_like);
                        int likeCount = (int) snapshot.child(postKey).getChildrenCount();
                        like_count.setText(likeCount + " likes");
                    } else {
                        likeReel.setImageResource(R.drawable.heart);
                        int likeCount = (int) snapshot.child(postKey).getChildrenCount();
                        like_count.setText(likeCount + " likes");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } catch (Exception e) {

        }
    }

    public void getFollowStatus(String UID, String RECEIVER) {
        reference.child("Followed").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    if (snapshot.child(UID).hasChild(RECEIVER)) {
                        follow.setText("Connected");
                    } else {
                        follow.setText("Connect");
                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getSelfCheck(String uid) {
        reference.child("Following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    if (snapshot.child(uid).hasChild(uid)) {
                        Toast.makeText(Sender_reel_items.this, "Unreachable Statement!", Toast.LENGTH_SHORT).show();
                        reference.child("Followed").child(uid).child(uid).removeValue();
                        reference.child("Following").child(uid).child(uid).removeValue();
                        follow.setText("Connected");
                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
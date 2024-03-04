package com.example.ichat.Post_Maintainence;

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
import com.example.ichat.OtherUserProfileFollow;
import com.example.ichat.Post_Details.postModel;
import com.example.ichat.PushNotification.FcmNotificationsSender;
import com.example.ichat.R;
import com.example.ichat.Share_Maintain.BottomSheetDialog;
import com.example.ichat.UsersActivities.ActivityModel;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class TapOnVideo extends AppCompatActivity {

    CircleImageView videoUserPic;
    TextView userNameV, videoCaption, vPostTime, video_like_count;
    ImageView thumbnailVideo, playVideo, sound;
    ImageButton likeVideo, shareVideo, commentVideo, savedVideo;
    String profileURI = "", userN = "", postKey = "", date = "", caption = "", postURI = "", userId = " ", displayName = "", fcmToken = "", aboutUser = "";
    boolean check_like = false, check_save = false;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    int type;
    int videoSound = 0;
    boolean checkMute = false;
    DatabaseReference reference;
    ExoPlayer exoPlayer;
    StyledPlayerView playerView;
    Button follow;
    String About = "", Num = "", UserName = "", profileUri = "", Fcm = "", dName = "";
    boolean check_follow = false;
    boolean checkOnline = false;
    String getTime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tap_on_video);
        videoUserPic = findViewById(R.id.tap_on_video_profilePic);
        userNameV = findViewById(R.id.tap_on_video_username);
        video_like_count = findViewById(R.id.tap_on_video_like_count);
        vPostTime = findViewById(R.id.tap_on_video_time);
        videoCaption = findViewById(R.id.tap_on_video_caption);
        thumbnailVideo = findViewById(R.id.tap_on_video_player_thumbnail);
        playVideo = findViewById(R.id.tap_on_video_play_btn);
        likeVideo = findViewById(R.id.tap_on_video_likes);
        commentVideo = findViewById(R.id.tap_on_video_comment);
        savedVideo = findViewById(R.id.tap_on_video_saved);
        shareVideo = findViewById(R.id.tap_on_video_share);
        playerView = findViewById(R.id.tap_on_video_player);
        sound = findViewById(R.id.video_sound_check_anim);
        follow = findViewById(R.id.tap_video_post_follow_btn);

        reference = FirebaseDatabase.getInstance().getReference();

        SharedPreferences sp = getSharedPreferences("SendPostData", MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();


        postURI = getIntent().getStringExtra("ONTAP_POST_URI");
        profileURI = getIntent().getStringExtra("ONTAP_POST_PROFILE");
        userN = getIntent().getStringExtra("ONTAP_POST_USERNAME");
        caption = getIntent().getStringExtra("ONTAP_POST_CAPTION");
        date = getIntent().getStringExtra("ONTAP_POST_TIME");
        postKey = getIntent().getStringExtra("ONTAP_POST_KEY");
        userId = getIntent().getStringExtra("ONTAP_POST_UID");
        fcmToken = getIntent().getStringExtra("ONTAP_FCM_TOKEN");
        aboutUser = getIntent().getStringExtra("ONTAP_ABOUT_USER");
        displayName = getIntent().getStringExtra("ONTAP_DISPLAY_NAME");

        SharedPreferences sp2 = getSharedPreferences("UserProfileData", MODE_PRIVATE);
        UserName = sp2.getString("username", "Please Set Username.");
        dName = sp2.getString("DisplayName", "Default");
        About = sp2.getString("about", "Please Set About Yourself.");
        Num = sp2.getString("number", "+91XXXXXXXXXX");
        profileUri = sp2.getString("profileUri", "Invalid.");
        Fcm = sp2.getString("fcmToken", "not");

        onAppStatus(userId);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDateTime dd = LocalDateTime.now();
            DateTimeFormatter f = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            getTime = f.format(dd);
        }

        userNameV.setText(userN);
        videoCaption.setText(caption);
        vPostTime.setText(date);
        Picasso.get().load(profileURI).into(videoUserPic);
//        Picasso.get().load(postURI).into(thumbnailVideo);
        Glide.with(this).load(postURI).into(thumbnailVideo);

        try {
            type = Integer.parseInt(getIntent().getStringExtra("ONTAP_POST_TYPE"));
        } catch (NumberFormatException nfe) {
            System.out.println("Could not parse " + nfe);
        }

        playerView.hideController();

        playVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                thumbnailVideo.setVisibility(View.GONE);
                playVideo.setVisibility(View.GONE);
                playerView.setVisibility(View.VISIBLE);
                exoPlayer = new ExoPlayer.Builder(getApplicationContext()).build();
                playerView.setPlayer(exoPlayer);
                MediaItem mediaItem = MediaItem.fromUri(postURI);
                exoPlayer.setMediaItem(mediaItem);
                exoPlayer.prepare();
                exoPlayer.play();
                videoSound = (int) exoPlayer.getVolume();
                exoPlayer.setVolume(0f);
            }
        });

        playerView.setOnTouchListener(new View.OnTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(@NonNull MotionEvent e) {
                    if (!checkMute) {
                        exoPlayer.setVolume(videoSound);
                        sound.setVisibility(View.VISIBLE);
                        sound.setImageResource(R.drawable.unmute);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                sound.setVisibility(View.GONE);
                            }
                        }, 1000);
                        checkMute = true;
                    } else {
                        exoPlayer.setVolume(0f);
                        sound.setVisibility(View.VISIBLE);
                        sound.setImageResource(R.drawable.mute);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                sound.setVisibility(View.GONE);
                            }
                        }, 1000);
                        checkMute = false;
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

        getLikeVideoStatus(postKey, uid);

        likeVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check_like = true;
                try {
                    reference.child("Post_likes").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (check_like == true) {

                                if (snapshot.child(postKey).hasChild(uid)) {
                                    reference.child("Post_likes").child(postKey).child(uid).removeValue();
                                    check_like = false;
                                } else {
                                    reference.child("Post_likes").child(postKey).child(uid).setValue(true);
                                    check_like = false;
                                }
                                ActivityModel model = new ActivityModel(profileUri, UserName + " is liked your post ♥!", getTime);
                                reference.child("UsersActivities").child(userId).push().setValue(model);
                                if (!checkOnline) {
                                    sendNotification(UserName + " is liked your post ♥.", fcmToken, "WeConnect");
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

        // share activity
        shareVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent shareIntent = new Intent(Intent.ACTION_SEND);
//                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                shareIntent.setType("text/plain");
//                shareIntent.putExtra(Intent.EXTRA_TEXT, postURI);
//                startActivity(shareIntent);

                ed.putString("share_post_username", userN);
                ed.putString("share_post_display", displayName);
                ed.putString("share_post_about", aboutUser);
                ed.putString("share_post_profile_uri", profileURI);
                ed.putString("share_post_fcm", fcmToken);
                ed.putString("share_post_postUri", postURI);
                ed.putString("share_post_caption", caption);
                ed.putString("share_post_date", date);
                ed.putString("share_post_uid", uid);
                ed.apply();

                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog();
                bottomSheetDialog.show(getSupportFragmentManager(), bottomSheetDialog.getTag());
            }
        });

        // comments activity
        commentVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(TapOnVideo.this, comment_management.class);
//                intent.putExtra("COMMENT_USER_POST_ID", postKey);
//                startActivity(intent);

                ed.putString("share_post_comment", postKey);
                ed.apply();

                comment comment = new comment();
                comment.show(getSupportFragmentManager(), comment.getTag());
            }
        });

        getSavedStatus(postKey, uid);
        savedVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check_save = true;

                try {
                    reference.child("PersonalSaved").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (check_save) {
                                if (snapshot.child(uid).hasChild(postKey)) {
                                    reference.child("PersonalSaved").child(uid).child(postKey).removeValue();
//                                        myHolder.postImageSaved.setImageResource(R.drawable.save);
                                    check_save = false;
                                } else {
                                    postModel postModel = new postModel(caption, postURI, date, userN, profileURI, userId, postKey, type, displayName, fcmToken, aboutUser);
                                    reference.child("PersonalSaved").child(uid).child(postKey).setValue(postModel);
//                                        myHolder.postImageSaved.setImageResource(R.drawable.saved);
                                    check_save = false;
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
                                    followingModel model = new followingModel(userN, aboutUser, profileURI, userId, fcmToken, displayName);
                                    followingModel model2 = new followingModel(UserName, About, profileUri, uid, Fcm, dName);
                                    reference.child("Followed").child(uid).child(userId).setValue(model);
                                    reference.child("Following").child(userId).child(uid).setValue(model2);
                                    if (!checkOnline) {
                                        sendNotification(userN + " send connection request.", fcmToken, "WeConnect");
                                    }
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

        getSelfCheck(uid);
        getFollowStatus(uid, userId);

    }

    public void getLikeVideoStatus(String postKey, String UID) {
        try {
            reference.child("Post_likes").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child(postKey).hasChild(UID)) {
                        likeVideo.setImageResource(R.drawable.heart_like);
                        int likeCount = (int) snapshot.child(postKey).getChildrenCount();
                        video_like_count.setText(likeCount + " likes");
                    } else {
                        likeVideo.setImageResource(R.drawable.heart);
                        int likeCount = (int) snapshot.child(postKey).getChildrenCount();
                        video_like_count.setText(likeCount + " likes");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } catch (Exception e) {

        }
    }

    public void getSavedStatus(String postKey, String UID) {
        try {
            reference.child("PersonalSaved").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child(UID).hasChild(postKey)) {
                        savedVideo.setImageResource(R.drawable.saved);
                    } else {
                        savedVideo.setImageResource(R.drawable.save);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } catch (Exception e) {

        }
    }

    public void getSelfCheck(String uid) {
        reference.child("Following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    if (snapshot.child(uid).hasChild(uid)) {
                        Toast.makeText(TapOnVideo.this, "Unreachable Statement!", Toast.LENGTH_SHORT).show();
                        reference.child("Followed").child(uid).child(uid).removeValue();
                        reference.child("Following").child(uid).child(uid).removeValue();
                        follow.setBackgroundResource(R.drawable.on_post_followed);
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

    public void getFollowStatus(String UID, String RECEIVER) {
        reference.child("Followed").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    if (snapshot.child(UID).hasChild(RECEIVER)) {
                        follow.setBackgroundResource(R.drawable.on_post_follow);
                        follow.setText("Connected");
                    } else {
                        follow.setBackgroundResource(R.drawable.on_post_followed);
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

    public void sendNotification(String message, String otherFcm, String username) {
        FcmNotificationsSender notificationsSender = new FcmNotificationsSender(otherFcm, username, message, this, TapOnVideo.this);
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
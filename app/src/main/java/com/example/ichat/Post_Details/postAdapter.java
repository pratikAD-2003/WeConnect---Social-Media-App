package com.example.ichat.Post_Details;

import static android.content.Context.MODE_PRIVATE;
import static com.example.ichat.Post_Details.postModel.IMAGE_POST;
import static com.example.ichat.Post_Details.postModel.VIDEO_POST;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ichat.OtherUserProfileFollow;
import com.example.ichat.Post_Maintainence.comment;
import com.example.ichat.Post_Maintainence.followingModel;
import com.example.ichat.PushNotification.FcmNotificationsSender;
import com.example.ichat.R;
import com.example.ichat.Share_Maintain.BottomSheetDialog;
import com.example.ichat.UsersActivities.ActivityModel;
import com.example.ichat.chat_screen;
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
import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class postAdapter extends RecyclerView.Adapter {
    Context context;
    ArrayList<postModel> list;
    ExoPlayer exoPlayer;
    int videoSound = 0;
    boolean checkMute = false;
    static DatabaseReference reference;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    boolean check_like, check_save;
    boolean check_follow = false;
    String pushId = "", test = "";
    Activity activity;
    boolean checkOnline = false;
    String getTime;

    public postAdapter(Context context, ArrayList<postModel> list, Activity activity) {
        this.context = context;
        this.list = list;
        this.activity = activity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0:
                View layoutTwo = LayoutInflater.from(context).inflate(R.layout.post_img_model, parent, false);
                return new postAdapter.MyHolder(layoutTwo);
            case 1:
                View layoutOne = LayoutInflater.from(context).inflate(R.layout.video_post_items, parent, false);
                return new postAdapter.VideoPost(layoutOne);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        SharedPreferences sp = context.getSharedPreferences("SendPostData", MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();

        reference = FirebaseDatabase.getInstance().getReference();

        if (holder.getClass() == MyHolder.class) {

            MyHolder myHolder = (MyHolder) holder;
            myHolder.userName.setText(list.get(position).getUsername());
            myHolder.postCaption.setText(list.get(position).getCaption());
            myHolder.postTime.setText(list.get(position).getPostTime());
//            Glide.with(context).load(list.get(position).getAudioUri()).into(myHolder.postPic);
            Picasso.get().load(list.get(position).getAudioUri()).into(myHolder.postPic);
            Glide.with(context).load(list.get(position).getUserPic()).into(myHolder.userPic);

            // sending data to profile page
            myHolder.userPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, OtherUserProfileFollow.class);
                    intent.putExtra("perUserN", list.get(position).getUsername());
                    intent.putExtra("ProfileUri", list.get(position).getUserPic());
                    intent.putExtra("userUid", list.get(position).getUid());
                    intent.putExtra("FCM_TOKEN", list.get(position).getFcmToken());
                    intent.putExtra("DisplayName", list.get(position).getDisplayName());
                    intent.putExtra("aboutUser", list.get(position).getAboutUser());
                    context.startActivity(intent);
                }
            });
            myHolder.userName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, OtherUserProfileFollow.class);
                    intent.putExtra("perUserN", list.get(position).getUsername());
                    intent.putExtra("ProfileUri", list.get(position).getUserPic());
                    intent.putExtra("userUid", list.get(position).getUid());
                    intent.putExtra("FCM_TOKEN", list.get(position).getFcmToken());
                    intent.putExtra("DisplayName", list.get(position).getDisplayName());
                    intent.putExtra("aboutUser", list.get(position).getAboutUser());
                    context.startActivity(intent);
                }
            });

            myHolder.getLikesStatus(list.get(position).getPostKey(), uid);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                LocalDateTime dd = LocalDateTime.now();
                DateTimeFormatter f = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                getTime = f.format(dd);
            }

            myHolder.postImgLikes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onAppStatus(list.get(position).getUid());
                    check_like = true;
                    try {
                        reference.child("Post_likes").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (check_like == true) {

                                    if (snapshot.child(list.get(position).getPostKey()).hasChild(uid)) {
                                        reference.child("Post_likes").child(list.get(position).getPostKey()).child(uid).removeValue();
                                        check_like = false;
                                    } else {
                                        reference.child("Post_likes").child(list.get(position).getPostKey()).child(uid).setValue(true);
                                        check_like = false;

                                        myHolder.doubleTapLikedImg.setVisibility(View.VISIBLE);
                                        myHolder.doubleTapLikedImg.setImageResource(R.drawable.heart_big_size);
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                myHolder.doubleTapLikedImg.setVisibility(View.GONE);
                                            }
                                        }, 1000);
                                        ActivityModel model = new ActivityModel(myHolder.profileUri, myHolder.UserName + " is liked your post ♥!", getTime);
                                        reference.child("UsersActivities").child(list.get(position).getUid()).push().setValue(model);
                                        if (!checkOnline) {
                                            sendNotification(myHolder.UserName + " is liked your post ♥.", list.get(position).getFcmToken(), "WeConnect");
                                        }
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

            //Double tap to like
            myHolder.postPic.setOnTouchListener(new View.OnTouchListener() {
                GestureDetector detector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDoubleTap(@NonNull MotionEvent e) {

                        check_like = true;
                        try {
                            reference.child("Post_likes").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (check_like == true) {
                                        reference.child("Post_likes").child(list.get(position).getPostKey()).child(uid).setValue(true);
                                        check_like = false;

                                        myHolder.doubleTapLikedImg.setVisibility(View.VISIBLE);
                                        myHolder.doubleTapLikedImg.setImageResource(R.drawable.heart_big_size);
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                myHolder.doubleTapLikedImg.setVisibility(View.GONE);
                                            }
                                        }, 1000);
                                        ActivityModel model = new ActivityModel(myHolder.profileUri, myHolder.UserName + " is liked your post ♥!", getTime);
                                        reference.child("UsersActivities").child(list.get(position).getUid()).push().setValue(model);
                                        if (!checkOnline) {
                                            sendNotification(myHolder.UserName + " is liked your post ♥.", list.get(position).getFcmToken(), "WeConnect");
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        } catch (Exception ee) {

                        }
                        return super.onDoubleTap(e);
                    }
                });

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    detector.onTouchEvent(event);
                    return true;
                }
            });

            // share activity
            myHolder.postImageShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ed.putString("share_post_username", list.get(position).getUsername());
                    ed.putString("share_post_display", list.get(position).getDisplayName());
                    ed.putString("share_post_about", list.get(position).getAboutUser());
                    ed.putString("share_post_profile_uri", list.get(position).getUserPic());
                    ed.putString("share_post_fcm", list.get(position).getFcmToken());
                    ed.putString("share_post_postUri", list.get(position).getAudioUri());
                    ed.putString("share_post_caption", list.get(position).getCaption());
                    ed.putString("share_post_date", list.get(position).getPostTime());
                    ed.putString("share_post_uid", list.get(position).getUid());
                    ed.putString("share_post_type", String.valueOf(1));
                    ed.putString("share_post_key", list.get(position).getPostKey());
                    ed.apply();

                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog();
                    bottomSheetDialog.show(((AppCompatActivity) context).getSupportFragmentManager(), bottomSheetDialog.getTag());
                }
            });

            // comments activity
            myHolder.postImageComments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ed.putString("share_post_comment", list.get(position).getPostKey());
                    ed.apply();

                    comment comment = new comment();
                    comment.show(((AppCompatActivity) context).getSupportFragmentManager(), comment.getTag());
                }
            });


            // saved feature
            myHolder.getSavedStatus(list.get(position).getPostKey(), uid);
            myHolder.postImageSaved.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    check_save = true;

                    try {
                        reference.child("PersonalSaved").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (check_save) {
                                    if (snapshot.child(uid).hasChild(list.get(position).getPostKey())) {
                                        reference.child("PersonalSaved").child(uid).child(list.get(position).getPostKey()).removeValue();
//                                        myHolder.postImageSaved.setImageResource(R.drawable.save);
                                        check_save = false;
                                    } else {
                                        postModel postModel = new postModel(list.get(position).caption, list.get(position).getAudioUri(), list.get(position).getPostTime(), list.get(position).getUsername(), list.get(position).getUserPic(), list.get(position).getUid(), list.get(position).getPostKey(), list.get(position).getType(), list.get(position).getDisplayName(), list.get(position).getFcmToken(), list.get(position).getAboutUser());
                                        reference.child("PersonalSaved").child(uid).child(list.get(position).getPostKey()).setValue(postModel);
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

            myHolder.getFollowStatus(uid, list.get(position).getUid());
            myHolder.getSelfCheck(uid);
            myHolder.follow_by_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onAppStatus(list.get(position).getUid());
                    check_follow = true;
                    try {
                        reference.child("Followed").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (check_follow) {
                                    if (snapshot.child(uid).hasChild(list.get(position).getUid())) {
                                        reference.child("Followed").child(uid).child(list.get(position).getUid()).removeValue();
                                        reference.child("Following").child(list.get(position).getUid()).child(uid).removeValue();
                                        check_follow = false;
                                    } else {
                                        followingModel model = new followingModel(list.get(position).getUsername(), list.get(position).getAboutUser(), list.get(position).getUserPic(), list.get(position).getUid(), list.get(position).getFcmToken(), list.get(position).getDisplayName());
                                        followingModel model2 = new followingModel(myHolder.UserName, myHolder.About, myHolder.profileUri, uid, myHolder.Fcm, myHolder.dName);
                                        reference.child("Followed").child(uid).child(list.get(position).getUid()).setValue(model);
                                        reference.child("Following").child(list.get(position).getUid()).child(uid).setValue(model2);
                                        if (!checkOnline) {
                                            sendNotification(myHolder.UserName + " send connection request.", list.get(position).getFcmToken(), "WeConnect");
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

        } else {
            VideoPost videoPost = (VideoPost) holder;
            Glide.with(context).load(list.get(position).getUserPic()).into(videoPost.videoUserPic);
            Glide.with(context).load(list.get(position).getAudioUri()).into(videoPost.thumbnailVideo);
            videoPost.userNameV.setText(list.get(position).getUsername());
            videoPost.videoCaption.setText(list.get(position).getCaption());
            videoPost.vPostTime.setText(list.get(position).getPostTime());
            videoPost.playerView.hideController();

            // sending data to profile page
            videoPost.videoUserPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, OtherUserProfileFollow.class);
                    intent.putExtra("perUserN", list.get(position).getUsername());
                    intent.putExtra("ProfileUri", list.get(position).getUserPic());
                    intent.putExtra("userUid", list.get(position).getUid());
                    intent.putExtra("FCM_TOKEN", list.get(position).getFcmToken());
                    intent.putExtra("DisplayName", list.get(position).getDisplayName());
                    intent.putExtra("aboutUser", list.get(position).getAboutUser());
                    context.startActivity(intent);
                }
            });
            videoPost.userNameV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, OtherUserProfileFollow.class);
                    intent.putExtra("perUserN", list.get(position).getUsername());
                    intent.putExtra("ProfileUri", list.get(position).getUserPic());
                    intent.putExtra("userUid", list.get(position).getUid());
                    intent.putExtra("FCM_TOKEN", list.get(position).getFcmToken());
                    intent.putExtra("DisplayName", list.get(position).getDisplayName());
                    intent.putExtra("aboutUser", list.get(position).getAboutUser());
                    context.startActivity(intent);
                }
            });

            videoPost.playVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    videoPost.thumbnailVideo.setVisibility(View.GONE);
                    videoPost.playVideo.setVisibility(View.GONE);
                    videoPost.playerView.setVisibility(View.VISIBLE);
                    exoPlayer = new ExoPlayer.Builder(context).build();
                    videoPost.playerView.setPlayer(exoPlayer);
                    MediaItem mediaItem = MediaItem.fromUri(list.get(position).getAudioUri());
                    exoPlayer.setMediaItem(mediaItem);
                    exoPlayer.prepare();
                    exoPlayer.play();
                    videoSound = (int) exoPlayer.getVolume();
                    exoPlayer.setVolume(0f);
                }
            });

            videoPost.playerView.setOnTouchListener(new View.OnTouchListener() {
                GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDoubleTap(@NonNull MotionEvent e) {
                        check_like = true;
                        try {
                            reference.child("Post_likes").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (check_like == true) {
                                        reference.child("Post_likes").child(list.get(position).getPostKey()).child(uid).setValue(true);
                                        check_like = false;

                                        videoPost.doubleTapVideoLike.setVisibility(View.VISIBLE);
                                        videoPost.doubleTapVideoLike.setImageResource(R.drawable.heart_big_size);
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                videoPost.doubleTapVideoLike.setVisibility(View.GONE);
                                            }
                                        }, 1000);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        } catch (Exception ee) {

                        }
                        return super.onDoubleTap(e);
                    }

                    @Override
                    public boolean onSingleTapUp(@NonNull MotionEvent e) {
                        if (!checkMute) {
                            exoPlayer.setVolume(videoSound);
                            checkMute = true;
                            videoPost.soundVideo.setVisibility(View.VISIBLE);
                            videoPost.soundVideo.setImageResource(R.drawable.unmute);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    videoPost.soundVideo.setVisibility(View.GONE);
                                }
                            }, 1000);
                        } else {
                            exoPlayer.setVolume(0f);
                            checkMute = false;
                            videoPost.soundVideo.setVisibility(View.VISIBLE);
                            videoPost.soundVideo.setImageResource(R.drawable.mute);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    videoPost.soundVideo.setVisibility(View.GONE);
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

            videoPost.getLikeVideoStatus(list.get(position).getPostKey(), uid);

            videoPost.likeVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    check_like = true;
                    try {
                        reference.child("Post_likes").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (check_like == true) {

                                    if (snapshot.child(list.get(position).getPostKey()).hasChild(uid)) {
                                        reference.child("Post_likes").child(list.get(position).getPostKey()).child(uid).removeValue();
                                        check_like = false;
                                    } else {
                                        reference.child("Post_likes").child(list.get(position).getPostKey()).child(uid).setValue(true);
                                        check_like = false;

                                        videoPost.doubleTapVideoLike.setVisibility(View.VISIBLE);
                                        videoPost.doubleTapVideoLike.setImageResource(R.drawable.heart_big_size);
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                videoPost.doubleTapVideoLike.setVisibility(View.GONE);
                                            }
                                        }, 1000);
                                        ActivityModel model = new ActivityModel(videoPost.profileUri, videoPost.UserName + " is liked your post ♥!", getTime);
                                        reference.child("UsersActivities").child(list.get(position).getUid()).push().setValue(model);
                                        if (!checkOnline) {
                                            sendNotification(videoPost.UserName + " is liked your post ♥.", list.get(position).getFcmToken(), "WeConnect");
                                        }
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
            videoPost.shareVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ed.putString("share_post_username", list.get(position).getUsername());
                    ed.putString("share_post_display", list.get(position).getDisplayName());
                    ed.putString("share_post_about", list.get(position).getAboutUser());
                    ed.putString("share_post_profile_uri", list.get(position).getUserPic());
                    ed.putString("share_post_fcm", list.get(position).getFcmToken());
                    ed.putString("share_post_postUri", list.get(position).getAudioUri());
                    ed.putString("share_post_caption", list.get(position).getCaption());
                    ed.putString("share_post_date", list.get(position).getPostTime());
                    ed.putString("share_post_uid", list.get(position).getUid());
                    ed.putString("share_post_type", String.valueOf(2));
                    ed.putString("share_post_key", list.get(position).getPostKey());
                    ed.apply();

                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog();
                    bottomSheetDialog.show(((AppCompatActivity) context).getSupportFragmentManager(), bottomSheetDialog.getTag());
                }
            });

            // comments activity
            videoPost.commentVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent intent = new Intent(context, comment_management.class);
//                    intent.putExtra("COMMENT_USER_POST_ID", list.get(position).getPostKey());
//                    context.startActivity(intent);

                    ed.putString("share_post_comment", list.get(position).getPostKey());
                    ed.apply();

                    comment comment = new comment();
                    comment.show(((AppCompatActivity) context).getSupportFragmentManager(), comment.getTag());
                }
            });

            videoPost.getSavedStatus(list.get(position).getPostKey(), uid);
            videoPost.savedVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    check_save = true;

                    try {
                        reference.child("PersonalSaved").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (check_save) {
                                    if (snapshot.child(uid).hasChild(list.get(position).getPostKey())) {
                                        reference.child("PersonalSaved").child(uid).child(list.get(position).getPostKey()).removeValue();
//                                        myHolder.postImageSaved.setImageResource(R.drawable.save);
                                        check_save = false;
                                    } else {
                                        postModel postModel = new postModel(list.get(position).caption, list.get(position).getAudioUri(), list.get(position).getPostTime(), list.get(position).getUsername(), list.get(position).getUserPic(), list.get(position).getUid(), list.get(position).getPostKey(), list.get(position).getType(), list.get(position).getDisplayName(), list.get(position).getFcmToken(), list.get(position).getAboutUser());
                                        reference.child("PersonalSaved").child(uid).child(list.get(position).getPostKey()).setValue(postModel);
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

            videoPost.getFollowStatus(uid, list.get(position).getUid());
            videoPost.getSelfCheck(uid);
            videoPost.follow_by_video.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onAppStatus(list.get(position).getUid());
                    check_follow = true;
                    try {
                        reference.child("Followed").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (check_follow) {
                                    if (snapshot.child(uid).hasChild(list.get(position).getUid())) {
                                        reference.child("Followed").child(uid).child(list.get(position).getUid()).removeValue();
                                        reference.child("Following").child(list.get(position).getUid()).child(uid).removeValue();
                                        check_follow = false;
                                    } else {
                                        followingModel model = new followingModel(list.get(position).getUsername(), list.get(position).getAboutUser(), list.get(position).getUserPic(), list.get(position).getUid(), list.get(position).getFcmToken(), list.get(position).getDisplayName());
                                        followingModel model2 = new followingModel(videoPost.UserName, videoPost.About, videoPost.profileUri, uid, videoPost.Fcm, videoPost.dName);
                                        reference.child("Followed").child(uid).child(list.get(position).getUid()).setValue(model);
                                        reference.child("Following").child(list.get(position).getUid()).child(uid).setValue(model2);
                                        if (!checkOnline) {
                                            sendNotification(videoPost.UserName + " send connection request.", list.get(position).getFcmToken(), "WeConnect");
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
        }
    }

    @Override
    public int getItemViewType(int position) {
        postModel model = list.get(position);
        switch (model.getType()) {
            case 0:
                return IMAGE_POST;
            case 1:
                return VIDEO_POST;
            default:
                return -1;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        ImageView postPic, doubleTapLikedImg;
        ImageButton postImgLikes, postImageShare, postImageComments, postImageSaved;
        CircleImageView userPic;
        TextView userName, postCaption, postTime, likes_count_img;

        Button follow_by_img;

        String About = "", Num = "", UserName = "", profileUri = "", Fcm = "", dName = "";

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            userPic = itemView.findViewById(R.id.per_post_image_userPic);
            postPic = itemView.findViewById(R.id.posted_image_user);
            userName = itemView.findViewById(R.id.per_post_image_userName);
            postCaption = itemView.findViewById(R.id.post_image_caption);
            postTime = itemView.findViewById(R.id.post_image_time);
            postImgLikes = itemView.findViewById(R.id.post_image_likes);
            likes_count_img = itemView.findViewById(R.id.post_image_likes_count);
            postImageShare = itemView.findViewById(R.id.post_image_shares);
            postImageComments = itemView.findViewById(R.id.post_image_comments);
            postImageSaved = itemView.findViewById(R.id.post_save_btn);
            follow_by_img = itemView.findViewById(R.id.image_post_follow_btn);
            doubleTapLikedImg = itemView.findViewById(R.id.double_tap_on_like_image_post);

            SharedPreferences sp = itemView.getContext().getSharedPreferences("UserProfileData", MODE_PRIVATE);
            UserName = sp.getString("username", "Please Set Username.");
            dName = sp.getString("DisplayName", "Default");
            About = sp.getString("about", "Please Set About Yourself.");
            Num = sp.getString("number", "+91XXXXXXXXXX");
            profileUri = sp.getString("profileUri", "Invalid.");
            Fcm = sp.getString("fcmToken", "not");

        }

        public void getLikesStatus(String postKey, String UID) {
            try {
                reference.child("Post_likes").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child(postKey).hasChild(UID)) {
                            postImgLikes.setImageResource(R.drawable.heart_like);
                            int likeCount = (int) snapshot.child(postKey).getChildrenCount();
                            likes_count_img.setText(likeCount + " likes");
                        } else {
                            postImgLikes.setImageResource(R.drawable.heart);
                            int likeCount = (int) snapshot.child(postKey).getChildrenCount();
                            likes_count_img.setText(likeCount + " likes");
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
                            postImageSaved.setImageResource(R.drawable.saved);
                        } else {
                            postImageSaved.setImageResource(R.drawable.save);
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
                            follow_by_img.setBackgroundResource(R.drawable.on_post_follow);
                            follow_by_img.setText("Connected");
                        } else {
                            follow_by_img.setBackgroundResource(R.drawable.on_post_followed);
                            follow_by_img.setText("Connect");
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
                            Toast.makeText(itemView.getContext(), "Unreachable Statement!", Toast.LENGTH_SHORT).show();
                            reference.child("Followed").child(uid).child(uid).removeValue();
                            reference.child("Following").child(uid).child(uid).removeValue();
                            follow_by_img.setBackgroundResource(R.drawable.on_post_followed);
                            follow_by_img.setText("Connected");
                        }
                    } catch (Exception e) {

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    public class VideoPost extends RecyclerView.ViewHolder {
        StyledPlayerView playerView;
        ImageView thumbnailVideo, playVideo, soundVideo, doubleTapVideoLike;
        CircleImageView videoUserPic;
        TextView userNameV, videoCaption, vPostTime, video_like_count;
        ImageButton likeVideo, shareVideo, commentVideo, savedVideo;
        String About = "", Num = "", UserName = "", profileUri = "", Fcm = "", dName = "";

        Button follow_by_video;

        public VideoPost(@NonNull View itemView) {
            super(itemView);
            playerView = itemView.findViewById(R.id.video_post_item);
            thumbnailVideo = itemView.findViewById(R.id.video_post_thumbnail);
            videoUserPic = itemView.findViewById(R.id.per_post_video_userPic);
            userNameV = itemView.findViewById(R.id.per_post_video_userName);
            videoCaption = itemView.findViewById(R.id.post_video_caption);
            vPostTime = itemView.findViewById(R.id.post_video_time);
            playVideo = itemView.findViewById(R.id.playVideoBtn_post);
            video_like_count = itemView.findViewById(R.id.post_video_likes_count);
            likeVideo = itemView.findViewById(R.id.post_video_likes);
            shareVideo = itemView.findViewById(R.id.post_video_shares);
            commentVideo = itemView.findViewById(R.id.post_video_comments);
            savedVideo = itemView.findViewById(R.id.save_video_post);
            soundVideo = itemView.findViewById(R.id.video_main_sound_check_anim);
            follow_by_video = itemView.findViewById(R.id.video_post_follow_btn);
            doubleTapVideoLike = itemView.findViewById(R.id.double_tap_on_like_video_post);

            SharedPreferences sp = itemView.getContext().getSharedPreferences("UserProfileData", MODE_PRIVATE);
            UserName = sp.getString("username", "Please Set Username.");
            dName = sp.getString("DisplayName", "Default");
            About = sp.getString("about", "Please Set About Yourself.");
            Num = sp.getString("number", "+91XXXXXXXXXX");
            profileUri = sp.getString("profileUri", "Invalid.");
            Fcm = sp.getString("fcmToken", "not");

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

        public void getFollowStatus(String UID, String RECEIVER) {
            reference.child("Followed").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        if (snapshot.child(UID).hasChild(RECEIVER)) {
                            follow_by_video.setBackgroundResource(R.drawable.on_post_follow);
                            follow_by_video.setText("Connected");
                        } else {
                            follow_by_video.setBackgroundResource(R.drawable.on_post_followed);
                            follow_by_video.setText("Connect");
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
                            Toast.makeText(itemView.getContext(), "Unreachable Statement!", Toast.LENGTH_SHORT).show();
                            reference.child("Followed").child(uid).child(uid).removeValue();
                            reference.child("Following").child(uid).child(uid).removeValue();
                            follow_by_video.setBackgroundResource(R.drawable.on_post_followed);
                            follow_by_video.setText("Connected");
                        }
                    } catch (Exception e) {

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    public void sendNotification(String message, String otherFcm, String username) {
        FcmNotificationsSender notificationsSender = new FcmNotificationsSender(otherFcm, username, message, context, activity);
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

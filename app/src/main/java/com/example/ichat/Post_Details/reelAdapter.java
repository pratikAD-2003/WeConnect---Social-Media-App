package com.example.ichat.Post_Details;

import static android.content.Context.MODE_PRIVATE;

import android.app.ProgressDialog;
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

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.ichat.MainScreenModel;
import com.example.ichat.Post_Maintainence.comment;
import com.example.ichat.Post_Maintainence.followingModel;
import com.example.ichat.R;
import com.example.ichat.Share_Maintain.BottomSheetDialog;
import com.example.ichat.Share_Maintain.shareListAdapter;
import com.example.ichat.chatModel;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class reelAdapter extends RecyclerView.Adapter<reelAdapter.reelHolder> {
    Context context;
    ArrayList<reelModel> list;
    ExoPlayer exoPlayer;
    int videoSound = 0;
    boolean checkMute = false;
    boolean checkPlay = false;

    DatabaseReference reference;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    String receiverRoom;
    boolean check_likes = false;
    boolean check_follow = false;

    public reelAdapter(Context context, ArrayList<reelModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public reelHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.reel_item, parent, false);
        return new reelHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull reelHolder holder, int position) {

        receiverRoom = uid + list.get(position).getUser_id();

        Date d = new Date();

        SharedPreferences sp = context.getSharedPreferences("SendPostData", MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();


        holder.user_title.setText(list.get(position).getReel_title());
        holder.user_name.setText(list.get(position).getUser_name());
        Glide.with(context).load(list.get(position).getUser_pic()).into(holder.user_pic);
        exoPlayer = new ExoPlayer.Builder(context).build();
        holder.playerView.setPlayer(exoPlayer);
        MediaItem mediaItem = MediaItem.fromUri(list.get(position).getReel_uri());
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.prepare();
        exoPlayer.setRepeatMode(Player.REPEAT_MODE_ONE);
        exoPlayer.setPlayWhenReady(true);
        videoSound = (int) exoPlayer.getVolume();
        holder.playerView.hideController();

        holder.playerView.setOnTouchListener(new View.OnTouchListener() {

            GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(@NonNull MotionEvent e) {
                    if (checkPlay) {
                        exoPlayer.pause();
                        checkPlay = false;
                        holder.soundCheck.setVisibility(View.VISIBLE);
                        holder.soundCheck.setImageResource(R.drawable.baseline_play_circle_24);
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                holder.soundCheck.setVisibility(View.GONE);
//                            }
//                        }, 1000);
                    } else {
                        exoPlayer.play();
                        checkPlay = true;
                        holder.soundCheck.setVisibility(View.GONE);
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

        holder.mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkMute) {
                    exoPlayer.setVolume(videoSound);
                    checkMute = false;
                    holder.mute.setImageResource(R.drawable.unmute_small);
                } else {
                    exoPlayer.setVolume(0f);
                    checkMute = true;
                    holder.mute.setImageResource(R.drawable.mute_small);
                }
            }
        });

        reference = FirebaseDatabase.getInstance().getReference();
        holder.getLikesStatus(list.get(position).getPostKey(), uid);


        holder.likeReel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check_likes = true;
                try {
                    reference.child("Post_likes").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (check_likes == true) {

                                if (snapshot.child(list.get(position).getPostKey()).hasChild(uid)) {
                                    reference.child("Post_likes").child(list.get(position).getPostKey()).child(uid).removeValue();
                                    check_likes = false;
                                } else {
                                    reference.child("Post_likes").child(list.get(position).getPostKey()).child(uid).setValue(true);
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

        holder.getFollowStatus(uid, list.get(position).getUser_id());
        holder.getSelfCheck(uid);
        holder.follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check_follow = true;
                try {
                    reference.child("Followed").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (check_follow) {
                                if (snapshot.child(uid).hasChild(list.get(position).getUser_id())) {
                                    reference.child("Followed").child(uid).child(list.get(position).getUser_id()).removeValue();
                                    reference.child("Following").child(list.get(position).getUser_id()).child(uid).removeValue();
                                    check_follow = false;
                                } else {
                                    followingModel model = new followingModel(list.get(position).getUser_name(), list.get(position).getAboutUser(), list.get(position).getUser_pic(), list.get(position).getUser_id(), list.get(position).getFcmToken(), list.get(position).getDisplayName());
                                    followingModel model2 = new followingModel(holder.UserName, holder.About, holder.profileUri, uid, holder.Fcm, holder.dName);
                                    reference.child("Followed").child(uid).child(list.get(position).getUser_id()).setValue(model);
                                    reference.child("Following").child(list.get(position).getUser_id()).child(uid).setValue(model2);
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

        holder.commentReel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ed.putString("share_post_comment", list.get(position).getPostKey());
                ed.apply();

                comment comment = new comment();
                comment.show(((AppCompatActivity) context).getSupportFragmentManager(), comment.getTag());
            }
        });

        holder.shareReel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ed.putString("share_post_username", list.get(position).getUser_name());
                ed.putString("share_post_display", list.get(position).getDisplayName());
                ed.putString("share_post_about", list.get(position).getAboutUser());
                ed.putString("share_post_profile_uri", list.get(position).getUser_pic());
                ed.putString("share_post_fcm", list.get(position).getFcmToken());
                ed.putString("share_post_postUri", list.get(position).getReel_uri());
                ed.putString("share_post_caption", list.get(position).getReel_title());
                ed.putString("share_post_date", list.get(position).getReel_time());
                ed.putString("share_post_uid", list.get(position).getUser_id());
                ed.putString("share_post_key", list.get(position).getPostKey());
                ed.putString("share_post_type", String.valueOf(0));
                ed.apply();

                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog();
                bottomSheetDialog.show(((AppCompatActivity) context).getSupportFragmentManager(), bottomSheetDialog.getTag());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class reelHolder extends RecyclerView.ViewHolder {
        CircleImageView user_pic;
        TextView user_name, user_title, like_count;
        StyledPlayerView playerView;
        ImageView soundCheck;
        ImageButton likeReel, commentReel, shareReel;
        Button follow;
        ImageButton mute;
        String About = "", Num = "", UserName = "", profileUri = "", Fcm = "", dName = "";

        public reelHolder(@NonNull View itemView) {
            super(itemView);
            playerView = itemView.findViewById(R.id.reel_player);
            user_pic = itemView.findViewById(R.id.reel_user_pic);
            user_name = itemView.findViewById(R.id.reel_user_name);
            user_title = itemView.findViewById(R.id.reel_user_title);
            soundCheck = itemView.findViewById(R.id.reel_sound_check_anim);
            likeReel = itemView.findViewById(R.id.reel_post_likes);
            like_count = itemView.findViewById(R.id.reel_post_likes_count);
            follow = itemView.findViewById(R.id.reel_post_follow_btn);
            commentReel = itemView.findViewById(R.id.reel_post_comment);
            shareReel = itemView.findViewById(R.id.reel_post_shares);
            mute = itemView.findViewById(R.id.mute_unmute_reel_btn);

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
                            Toast.makeText(itemView.getContext(), "Unreachable Statement!", Toast.LENGTH_SHORT).show();
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
    }
}

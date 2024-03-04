package com.example.ichat;

import static com.example.ichat.chatModel.AUDIO_LAYOUT;
import static com.example.ichat.chatModel.CHAT_LAYOUT;
import static com.example.ichat.chatModel.IMAGE_LAYOUT;
import static com.example.ichat.chatModel.POST_SHARE_LAYOUT;
import static com.example.ichat.chatModel.REEL_LAYOUT;
import static com.example.ichat.chatModel.VIDEO_LAYOUT;
import static com.example.ichat.chatModel.VIDEO_POST_SHARE_LAYOUT;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.ichat.Post_Maintainence.TapOnImage;
import com.example.ichat.Post_Maintainence.TapOnVideo;
import com.example.ichat.SendItemsProcess.Sender_reel_items;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<chatModel> list;

    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();

    public ChatAdapter(Context context, ArrayList<chatModel> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getItemViewType(int position) {
        chatModel model = list.get(position);
        switch (model.getType()) {
            case 0:
                return CHAT_LAYOUT;
            case 1:
                return IMAGE_LAYOUT;
            case 2:
                return VIDEO_LAYOUT;
            case 3:
                return AUDIO_LAYOUT;
            case 4:
                return REEL_LAYOUT;
            case 5:
                return POST_SHARE_LAYOUT;
            case 6:
                return VIDEO_POST_SHARE_LAYOUT;
            default:
                return -1;
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0:
                View layoutTwo = LayoutInflater.from(context).inflate(R.layout.sender_chat_type, parent, false);
                return new MyViewHolder(layoutTwo);
            case 1:
                View layoutOne = LayoutInflater.from(context).inflate(R.layout.sender_image, parent, false);
                return new ImageHolder(layoutOne);
            case 2:
                View layoutThree = LayoutInflater.from(context).inflate(R.layout.video_items, parent, false);
                return new VideoHolder(layoutThree);
            case 3:
                View layoutFour = LayoutInflater.from(context).inflate(R.layout.audio_recording_player, parent, false);
                return new AudioHolder(layoutFour);
            case 4:
                View layoutFive = LayoutInflater.from(context).inflate(R.layout.sender_reel_items, parent, false);
                return new ReelHolder(layoutFive);
            case 5:
                View layoutSix = LayoutInflater.from(context).inflate(R.layout.sender_img_post_share, parent, false);
                return new ShareImgHolder(layoutSix);
            case 6:
                View layoutSeven = LayoutInflater.from(context).inflate(R.layout.sender_video_post_share, parent, false);
                return new VideoShareViewholder(layoutSeven);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        chatModel model = list.get(position);
        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(model.getSenderId())) {
            if (holder.getClass() == MyViewHolder.class) {
                MyViewHolder viewHolder = (MyViewHolder) holder;
                viewHolder.send_text.setVisibility(View.VISIBLE);
                viewHolder.send_text_time.setVisibility(View.VISIBLE);
                viewHolder.send_text.setText(model.getMessage());
                viewHolder.send_text_time.setText(model.getTime());
            } else if (holder.getClass() == ImageHolder.class) {
                ImageHolder viewHolder = (ImageHolder) holder;
                viewHolder.send_image.setVisibility(View.VISIBLE);
                viewHolder.l1.setVisibility(View.VISIBLE);
                viewHolder.sender_img_time.setVisibility(View.VISIBLE);
                Glide.with(context).load(model.getImageUri()).into(viewHolder.send_image);
                viewHolder.sender_img_time.setText(model.getTime());
                viewHolder.send_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, fullImageOnMain.class);
                        intent.putExtra("uriFullImg", model.getImageUri());
                        context.startActivity(intent);
                    }
                });
            } else if (holder.getClass() == VideoHolder.class) {
                VideoHolder viewHolder = (VideoHolder) holder;
                viewHolder.l1.setVisibility(View.VISIBLE);
                viewHolder.send_video.setVisibility(View.VISIBLE);
                viewHolder.sender_play_btn.setVisibility(View.VISIBLE);
                viewHolder.sender_video_time.setVisibility(View.VISIBLE);
                Glide.with(context).load(model.getVideoUri()).into(viewHolder.send_video);
                viewHolder.sender_video_time.setText(model.getTime());
                viewHolder.send_video.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, videoPlaySend.class);
                        intent.putExtra("uriPlayVideo", model.getVideoUri());
                        context.startActivity(intent);
                    }
                });
            } else if (holder.getClass() == AudioHolder.class) {
                AudioHolder viewHolder = (AudioHolder) holder;
                viewHolder.l1_audio.setVisibility(View.VISIBLE);
                viewHolder.sender_audio_time.setVisibility(View.VISIBLE);
                viewHolder.sender_audio_time.setText(list.get(position).getTime());

                viewHolder.sender_play_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, AudioPlayer.class);
                        intent.putExtra("AudioUri", list.get(position).getAudioUri());
                        context.startActivity(intent);
                    }
                });
            } else if (holder.getClass() == ReelHolder.class) {
                ReelHolder reelHolder = (ReelHolder) holder;
                reelHolder.r1.setVisibility(View.VISIBLE);
                reelHolder.send_reel.setVisibility(View.VISIBLE);
                reelHolder.send_reel_time.setVisibility(View.VISIBLE);
                Glide.with(context).load(list.get(position).getReel_uri()).into(reelHolder.send_reel);
                reelHolder.send_reel_time.setText(list.get(position).getTime());
                reelHolder.send_reel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, Sender_reel_items.class);
                        intent.putExtra("reelURI", model.getReel_uri());
                        intent.putExtra("reelTitle", model.getReel_title());
                        intent.putExtra("reelUserName", model.getUser_name());
                        intent.putExtra("reelUserPic", model.getUser_pic());
                        intent.putExtra("reelPostKey", model.getPostKey());
                        intent.putExtra("userId_reel", model.getUser_id());
                        intent.putExtra("displayName", model.getDisplay());
                        intent.putExtra("reel_FCM", model.getFcmToken());
                        intent.putExtra("reel_AboutUser", model.getAboutUser());
                        context.startActivity(intent);
                    }
                });
            } else if ((holder.getClass() == ShareImgHolder.class)) {
                ShareImgHolder shareImgHolder = (ShareImgHolder) holder;
                shareImgHolder.l1.setVisibility(View.VISIBLE);
                shareImgHolder.senderPostTime.setVisibility(View.VISIBLE);
                shareImgHolder.senderCaption.setText(list.get(position).getReel_title());
                shareImgHolder.senderName.setText(list.get(position).getUser_name());
                shareImgHolder.senderPostTime.setText(list.get(position).getTime());
                Glide.with(context).load(list.get(position).getUser_pic()).into(shareImgHolder.senderPic);
                Glide.with(context).load(list.get(position).getReel_uri()).into(shareImgHolder.senderPost);

                shareImgHolder.senderPost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, TapOnImage.class);
                        intent.putExtra("ONTAP_POST_URI", list.get(position).getReel_uri());
                        intent.putExtra("ONTAP_POST_TIME", list.get(position).getReel_time());
                        intent.putExtra("ONTAP_POST_CAPTION", list.get(position).getReel_title());
                        intent.putExtra("ONTAP_POST_USERNAME", list.get(position).getUser_name());
                        intent.putExtra("ONTAP_POST_PROFILE", list.get(position).getUser_pic());
                        intent.putExtra("ONTAP_POST_KEY", list.get(position).getPostKey());
                        intent.putExtra("ONTAP_POST_TYPE", list.get(position).getType());
                        intent.putExtra("ONTAP_POST_UID", list.get(position).getUser_id());
                        intent.putExtra("ONTAP_FCM_TOKEN", list.get(position).getFcmToken());
                        intent.putExtra("ONTAP_ABOUT_USER", list.get(position).getAboutUser());
                        intent.putExtra("ONTAP_DISPLAY_NAME", list.get(position).getDisplay());
                        context.startActivity(intent);
                    }
                });
            } else {
                VideoShareViewholder videoShareViewholder = (VideoShareViewholder) holder;
                videoShareViewholder.l1.setVisibility(View.VISIBLE);
                videoShareViewholder.senderPostTime.setVisibility(View.VISIBLE);
                videoShareViewholder.senderCaption.setText(list.get(position).getReel_title());
                videoShareViewholder.senderName.setText(list.get(position).getUser_name());
                videoShareViewholder.senderPostTime.setText(list.get(position).getTime());
                Glide.with(context).load(list.get(position).getUser_pic()).into(videoShareViewholder.senderPic);
                Glide.with(context).load(list.get(position).getReel_uri()).into(videoShareViewholder.senderPost);

                videoShareViewholder.senderPlayBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, TapOnVideo.class);
                        intent.putExtra("ONTAP_POST_URI", list.get(position).getReel_uri());
                        intent.putExtra("ONTAP_POST_TIME", list.get(position).getReel_time());
                        intent.putExtra("ONTAP_POST_CAPTION", list.get(position).getReel_title());
                        intent.putExtra("ONTAP_POST_USERNAME", list.get(position).getUser_name());
                        intent.putExtra("ONTAP_POST_PROFILE", list.get(position).getUser_pic());
                        intent.putExtra("ONTAP_POST_KEY", list.get(position).getPostKey());
                        intent.putExtra("ONTAP_POST_TYPE", list.get(position).getType());
                        intent.putExtra("ONTAP_POST_UID", list.get(position).getUser_id());
                        intent.putExtra("ONTAP_FCM_TOKEN", list.get(position).getFcmToken());
                        intent.putExtra("ONTAP_ABOUT_USER", list.get(position).getAboutUser());
                        intent.putExtra("ONTAP_DISPLAY_NAME", list.get(position).getDisplay());
                        context.startActivity(intent);
                    }
                });
            }
        } else {
            if (holder.getClass() == MyViewHolder.class) {
                MyViewHolder viewHolder = (MyViewHolder) holder;
                viewHolder.receive_text.setVisibility(View.VISIBLE);
                viewHolder.receive_text_time.setVisibility(View.VISIBLE);
                viewHolder.receive_text.setText(model.getMessage());
                viewHolder.receive_text_time.setText(model.getTime());
            } else if (holder.getClass() == ImageHolder.class) {
                ImageHolder viewHolder = (ImageHolder) holder;
                viewHolder.receive_image.setVisibility(View.VISIBLE);
                viewHolder.l2.setVisibility(View.VISIBLE);
                viewHolder.receiver_img_time.setVisibility(View.VISIBLE);
                Glide.with(context).load(model.getImageUri()).into(viewHolder.receive_image);
                viewHolder.receiver_img_time.setText(model.getTime());
                viewHolder.receive_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, fullImageOnMain.class);
                        intent.putExtra("uriFullImg", model.getImageUri());
                        context.startActivity(intent);
                    }
                });
            } else if (holder.getClass() == VideoHolder.class) {
                VideoHolder viewHolder = (VideoHolder) holder;
                viewHolder.receive_video.setVisibility(View.VISIBLE);
                viewHolder.l2.setVisibility(View.VISIBLE);
                viewHolder.receiver_video_time.setVisibility(View.VISIBLE);
                viewHolder.receiver_play_btn.setVisibility(View.VISIBLE);
                Glide.with(context).load(model.getVideoUri()).into(viewHolder.receive_video);
                viewHolder.receiver_video_time.setText(model.getTime());
                viewHolder.receive_video.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, videoPlaySend.class);
                        intent.putExtra("uriPlayVideo", model.getVideoUri());
                        context.startActivity(intent);
                    }
                });
            } else if (holder.getClass() == AudioHolder.class) {
                AudioHolder viewHolder = (AudioHolder) holder;
                viewHolder.l2_audio.setVisibility(View.VISIBLE);
                viewHolder.receiver_audio_time.setVisibility(View.VISIBLE);
                viewHolder.receiver_audio_time.setText(list.get(position).getTime());

                viewHolder.receiver_play_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, AudioPlayer.class);
                        intent.putExtra("AudioUri", list.get(position).getAudioUri());
                        context.startActivity(intent);
                    }
                });
            } else if (holder.getClass() == ReelHolder.class) {
                ReelHolder reelHolder = (ReelHolder) holder;
                reelHolder.r2.setVisibility(View.VISIBLE);
                reelHolder.receiver_reel.setVisibility(View.VISIBLE);
                reelHolder.receiver_reel_time.setVisibility(View.VISIBLE);
                Glide.with(context).load(list.get(position).getReel_uri()).into(reelHolder.receiver_reel);
                reelHolder.receiver_reel_time.setText(list.get(position).getTime());
                reelHolder.receiver_reel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, Sender_reel_items.class);
                        intent.putExtra("reelURI", model.getReel_uri());
                        intent.putExtra("reelTitle", model.getReel_title());
                        intent.putExtra("reelUserName", model.getUser_name());
                        intent.putExtra("reelUserPic", model.getUser_pic());
                        intent.putExtra("reelPostKey", model.getPostKey());
                        intent.putExtra("userId_reel", model.getUser_id());
                        intent.putExtra("displayName", model.getDisplay());
                        intent.putExtra("reel_FCM", model.getFcmToken());
                        intent.putExtra("reel_Time", model.getReel_time());
                        intent.putExtra("reel_AboutUser", model.getAboutUser());
                        context.startActivity(intent);
                    }
                });
            } else if ((holder.getClass() == ShareImgHolder.class)) {
                ShareImgHolder shareImgHolder = (ShareImgHolder) holder;
                shareImgHolder.l2.setVisibility(View.VISIBLE);
                shareImgHolder.receiverPostTime.setVisibility(View.VISIBLE);
                shareImgHolder.receiverCaption.setText(list.get(position).getReel_title());
                shareImgHolder.receiverName.setText(list.get(position).getUser_name());
                shareImgHolder.receiverPostTime.setText(list.get(position).getTime());
                Glide.with(context).load(list.get(position).getUser_pic()).into(shareImgHolder.receiverPic);
                Glide.with(context).load(list.get(position).getReel_uri()).into(shareImgHolder.receiverPost);
                shareImgHolder.receiverPost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, TapOnImage.class);
                        intent.putExtra("ONTAP_POST_URI", list.get(position).getReel_uri());
                        intent.putExtra("ONTAP_POST_TIME", list.get(position).getReel_time());
                        intent.putExtra("ONTAP_POST_CAPTION", list.get(position).getReel_title());
                        intent.putExtra("ONTAP_POST_USERNAME", list.get(position).getUser_name());
                        intent.putExtra("ONTAP_POST_PROFILE", list.get(position).getUser_pic());
                        intent.putExtra("ONTAP_POST_KEY", list.get(position).getPostKey());
                        intent.putExtra("ONTAP_POST_TYPE", list.get(position).getType());
                        intent.putExtra("ONTAP_POST_UID", list.get(position).getUser_id());
                        intent.putExtra("ONTAP_FCM_TOKEN", list.get(position).getFcmToken());
                        intent.putExtra("ONTAP_ABOUT_USER", list.get(position).getAboutUser());
                        intent.putExtra("ONTAP_DISPLAY_NAME", list.get(position).getDisplay());
                        context.startActivity(intent);
                    }
                });
            } else {
                VideoShareViewholder videoShareViewholder = (VideoShareViewholder) holder;
                videoShareViewholder.l2.setVisibility(View.VISIBLE);
                videoShareViewholder.receiverPostTime.setVisibility(View.VISIBLE);
                videoShareViewholder.receiverCaption.setText(list.get(position).getReel_title());
                videoShareViewholder.receiverName.setText(list.get(position).getUser_name());
                videoShareViewholder.receiverPostTime.setText(list.get(position).getTime());
                Glide.with(context).load(list.get(position).getUser_pic()).into(videoShareViewholder.receiverPic);
                Glide.with(context).load(list.get(position).getReel_uri()).into(videoShareViewholder.receiverPost);

                videoShareViewholder.receiverPlayBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, TapOnVideo.class);
                        intent.putExtra("ONTAP_POST_URI", list.get(position).getReel_uri());
                        intent.putExtra("ONTAP_POST_TIME", list.get(position).getReel_time());
                        intent.putExtra("ONTAP_POST_CAPTION", list.get(position).getReel_title());
                        intent.putExtra("ONTAP_POST_USERNAME", list.get(position).getUser_name());
                        intent.putExtra("ONTAP_POST_PROFILE", list.get(position).getUser_pic());
                        intent.putExtra("ONTAP_POST_KEY", list.get(position).getPostKey());
                        intent.putExtra("ONTAP_POST_TYPE", list.get(position).getType());
                        intent.putExtra("ONTAP_POST_UID", list.get(position).getUser_id());
                        intent.putExtra("ONTAP_FCM_TOKEN", list.get(position).getFcmToken());
                        intent.putExtra("ONTAP_ABOUT_USER", list.get(position).getAboutUser());
                        intent.putExtra("ONTAP_DISPLAY_NAME", list.get(position).getDisplay());
                        context.startActivity(intent);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView send_text, receive_text, send_text_time, receive_text_time;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            send_text = itemView.findViewById(R.id.sender_text);
            receive_text = itemView.findViewById(R.id.receiver_text);
            send_text_time = itemView.findViewById(R.id.send_text_time);
            receive_text_time = itemView.findViewById(R.id.receiver_text_time);
        }
    }

    public class ImageHolder extends RecyclerView.ViewHolder {
        ImageView send_image, receive_image;
        TextView sender_img_time, receiver_img_time;

        LinearLayoutCompat l1, l2;

        public ImageHolder(@NonNull View itemView) {
            super(itemView);
            send_image = itemView.findViewById(R.id.sender_image);
            receive_image = itemView.findViewById(R.id.receiver_image);
            sender_img_time = itemView.findViewById(R.id.send_img_time);
            receiver_img_time = itemView.findViewById(R.id.receiver_text_time);
            l1 = itemView.findViewById(R.id.imgL1);
            l2 = itemView.findViewById(R.id.imgL2);
        }
    }

    public class VideoHolder extends RecyclerView.ViewHolder {
        ImageView send_video, receive_video, sender_play_btn, receiver_play_btn;
        TextView sender_video_time, receiver_video_time;
        RelativeLayout l1, l2;

        public VideoHolder(@NonNull View itemView) {
            super(itemView);
            send_video = itemView.findViewById(R.id.sender_video);
            receive_video = itemView.findViewById(R.id.receiver_video);
            sender_video_time = itemView.findViewById(R.id.send_video_time);
            receiver_video_time = itemView.findViewById(R.id.receiver_video_time);
            l1 = itemView.findViewById(R.id.videoL1);
            l2 = itemView.findViewById(R.id.videoL2);
            sender_play_btn = itemView.findViewById(R.id.send_video_play_btn);
            receiver_play_btn = itemView.findViewById(R.id.receive_video_play_btn);
        }
    }

    public class AudioHolder extends RecyclerView.ViewHolder {
        ImageButton sender_play_btn, receiver_play_btn;
        TextView sender_audio_time, receiver_audio_time;
        LinearLayoutCompat l1_audio, l2_audio;

        public AudioHolder(@NonNull View itemView) {
            super(itemView);
            sender_play_btn = itemView.findViewById(R.id.sender_audi_play_btn);
            receiver_play_btn = itemView.findViewById(R.id.receiver_audio_play_btn);
            sender_audio_time = itemView.findViewById(R.id.send_audio_time);
            receiver_audio_time = itemView.findViewById(R.id.receiver_audio_time);
            l1_audio = itemView.findViewById(R.id.audio_l1);
            l2_audio = itemView.findViewById(R.id.audio_l2);
        }
    }

    public class ReelHolder extends RecyclerView.ViewHolder {
        RelativeLayout r1, r2;
        ImageView send_reel, receiver_reel;
        TextView send_reel_time, receiver_reel_time;

        public ReelHolder(@NonNull View itemView) {
            super(itemView);
            r1 = itemView.findViewById(R.id.reelL1);
            r2 = itemView.findViewById(R.id.reelL2);
            send_reel = itemView.findViewById(R.id.sender_reel);
            receiver_reel = itemView.findViewById(R.id.receiver_reel);
            send_reel_time = itemView.findViewById(R.id.send_reel_time);
            receiver_reel_time = itemView.findViewById(R.id.receiver_reel_time);
        }
    }

    public class ShareImgHolder extends RecyclerView.ViewHolder {
        LinearLayout l1, l2;
        CircleImageView senderPic, receiverPic;
        TextView senderName, receiverName, senderCaption, receiverCaption, senderPostTime, receiverPostTime;
        ImageView senderPost, receiverPost;

        public ShareImgHolder(@NonNull View itemView) {
            super(itemView);
            l1 = itemView.findViewById(R.id.SIL);
            l2 = itemView.findViewById(R.id.SIL2);
            senderPic = itemView.findViewById(R.id.sender_post_pic);
            receiverPic = itemView.findViewById(R.id.receiver_post_pic);
            senderName = itemView.findViewById(R.id.sender_post_username);
            receiverName = itemView.findViewById(R.id.receiver_post_username);
            senderCaption = itemView.findViewById(R.id.sender_post_caption);
            receiverCaption = itemView.findViewById(R.id.receiver_post_caption);
            senderPost = itemView.findViewById(R.id.sender_post_image);
            receiverPost = itemView.findViewById(R.id.receiver_post_image);
            senderPostTime = itemView.findViewById(R.id.sender_post_time);
            receiverPostTime = itemView.findViewById(R.id.receiver_post_time);
        }
    }

    public class VideoShareViewholder extends RecyclerView.ViewHolder {
        LinearLayout l1, l2;
        CircleImageView senderPic, receiverPic;
        TextView senderName, receiverName, senderCaption, receiverCaption, senderPostTime, receiverPostTime;
        ImageView senderPost, receiverPost, senderPlayBtn, receiverPlayBtn;

        public VideoShareViewholder(@NonNull View itemView) {
            super(itemView);
            l1 = itemView.findViewById(R.id.SVL);
            l2 = itemView.findViewById(R.id.SVL2);
            senderPic = itemView.findViewById(R.id.sender_video_post_pic);
            receiverPic = itemView.findViewById(R.id.receiver_video_post_pic);
            senderName = itemView.findViewById(R.id.sender_video_post_username);
            receiverName = itemView.findViewById(R.id.receiver_video_post_username);
            senderCaption = itemView.findViewById(R.id.sender_video_post_caption);
            receiverCaption = itemView.findViewById(R.id.receiver_video_post_caption);
            senderPost = itemView.findViewById(R.id.sender_video_post_image);
            receiverPost = itemView.findViewById(R.id.receiver_video_post_image);
            senderPostTime = itemView.findViewById(R.id.sender_video_post_time);
            receiverPostTime = itemView.findViewById(R.id.receiver_video_post_time);
            senderPlayBtn = itemView.findViewById(R.id.sender_video_play_btn);
            receiverPlayBtn = itemView.findViewById(R.id.receiver_video_play_btn);
        }
    }
}

package com.example.ichat.Share_Maintain;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ichat.MainScreenModel;
import com.example.ichat.Post_Maintainence.followingModel;
import com.example.ichat.ProfileModel;
import com.example.ichat.PushNotification.FcmNotificationsSender;
import com.example.ichat.R;
import com.example.ichat.chatModel;
import com.example.ichat.chat_screen;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class shareListAdapter extends RecyclerView.Adapter<shareListAdapter.MyViewHolder> {
    Context context;
    ArrayList<followingModel> list;

    Activity activity;

    DatabaseReference reference;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    String receiverRoom, senderRoom;
    int type;
    FirebaseFirestore db;
    String our_username;

    boolean checkOnline = false;

    public shareListAdapter(Context context, ArrayList<followingModel> list, Activity activity) {
        this.context = context;
        this.list = list;
        this.activity = activity;
    }

    public void updateList(List<followingModel> list2) {
        list = (ArrayList<followingModel>) list2;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.share_post_items, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        receiverRoom = uid + list.get(position).getUserUid();
        senderRoom = list.get(position).getUserUid() + uid;
        Date d = new Date();
        reference = FirebaseDatabase.getInstance().getReference();
        db = FirebaseFirestore.getInstance();

        SharedPreferences sp2 = context.getSharedPreferences("UserProfileData", MODE_PRIVATE);
        our_username = sp2.getString("username", "Please Set Username.");

        SharedPreferences sp = context.getSharedPreferences("SendPostData", MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();

        holder.userName = sp.getString("share_post_username", "not");
        holder.display = sp.getString("share_post_display", "not");
        holder.about = sp.getString("share_post_about", "not");
        holder.profilePic = sp.getString("share_post_profile_uri", "not");
        holder.fcm = sp.getString("share_post_fcm", "not");
        holder.postUri = sp.getString("share_post_postUri", "not");
        holder.caption = sp.getString("share_post_caption", "not");
        holder.postDate = sp.getString("share_post_date", "not");
        holder.userUid = sp.getString("share_post_uid", "not");
        holder.postKey = sp.getString("share_post_key", "not");

        type = Integer.parseInt(sp.getString("share_post_type", "not"));

        Glide.with(context).load(list.get(position).getUserProfile()).into(holder.circleImageView);
        holder.username.setText(list.get(position).getUsername());


        holder.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAppStatus(list.get(position).getUserUid());
                holder.send.setBackgroundResource(R.drawable.followed_btn);
                if (type == 0) {
                    String times = d.getHours() + ":" + d.getMinutes();
                    chatModel chatModel = new chatModel(com.example.ichat.chatModel.REEL_LAYOUT, holder.postDate, holder.postUri, holder.caption, holder.userName, uid, holder.profilePic,
                            holder.postKey, list.get(position).getAboutUser(), holder.fcm, times, holder.userUid, holder.display);
                    MainScreenModel model = new MainScreenModel(list.get(position).getUserProfile(), list.get(position).getUsername(), list.get(position).getAboutUser(), list.get(position).getUserUid(), uid, list.get(position).getUserFcm(), list.get(position).getDisplayName());
                    reference.child("Chats").child(list.get(position).getUserUid() + uid).child("messages").push().setValue(chatModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            reference.child("Chats").child(uid + list.get(position).getUserUid()).child("messages").push().setValue(chatModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    db.collection("OnScreenUser").document(uid + list.get(position).getUserUid()).set(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(context, "Sent!", Toast.LENGTH_SHORT).show();
//                                            holder.send.setBackgroundResource(R.drawable.followed_btn);
                                            if (!checkOnline) {
                                                sendNotification("Sent reel by " + list.get(position).getUsername(), list.get(position).getUserFcm(), our_username);
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    });
                } else if (type == 1) {
                    String times = d.getHours() + ":" + d.getMinutes();
                    chatModel chatModel = new chatModel(com.example.ichat.chatModel.POST_SHARE_LAYOUT, holder.postDate, holder.postUri, holder.caption, holder.userName, uid, holder.profilePic,
                            holder.postKey, list.get(position).getAboutUser(), holder.fcm, times, holder.userUid, holder.display);
                    MainScreenModel model = new MainScreenModel(list.get(position).getUserProfile(), list.get(position).getUsername(), list.get(position).getAboutUser(), list.get(position).getUserUid(), uid, list.get(position).getUserFcm(), list.get(position).getDisplayName());
                    reference.child("Chats").child(list.get(position).getUserUid() + uid).child("messages").push().setValue(chatModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            reference.child("Chats").child(uid + list.get(position).getUserUid()).child("messages").push().setValue(chatModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    db.collection("OnScreenUser").document(uid + list.get(position).getUserUid()).set(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(context, "Sent!", Toast.LENGTH_SHORT).show();
//                                            holder.send.setBackgroundResource(R.drawable.followed_btn);
                                            if (!checkOnline) {
                                                sendNotification("Sent post by " + list.get(position).getUsername(), list.get(position).getUserFcm(), our_username);
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    });
                } else {
                    String times = d.getHours() + ":" + d.getMinutes();
                    chatModel chatModel = new chatModel(com.example.ichat.chatModel.VIDEO_POST_SHARE_LAYOUT, holder.postDate, holder.postUri, holder.caption, holder.userName, uid, holder.profilePic,
                            holder.postKey, list.get(position).getAboutUser(), holder.fcm, times, holder.userUid, holder.display);
                    MainScreenModel model = new MainScreenModel(list.get(position).getUserProfile(), list.get(position).getUsername(), list.get(position).getAboutUser(), list.get(position).getUserUid(), uid, list.get(position).getUserFcm(), list.get(position).getDisplayName());
                    reference.child("Chats").child(list.get(position).getUserUid() + uid).child("messages").push().setValue(chatModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            reference.child("Chats").child(uid + list.get(position).getUserUid()).child("messages").push().setValue(chatModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    db.collection("OnScreenUser").document(uid + list.get(position).getUserUid()).set(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(context, "Sent!", Toast.LENGTH_SHORT).show();
//                                            holder.send.setBackgroundResource(R.drawable.followed_btn);
                                            if (!checkOnline) {
                                                sendNotification("Sent video post by " + list.get(position).getUsername(), list.get(position).getUserFcm(), our_username);
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView username;
        String userName, display, about, profilePic, fcm, postUri, caption, postDate, userUid, postKey;

        Button send;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.share_user_profile);
            username = itemView.findViewById(R.id.share_user_username);
            send = itemView.findViewById(R.id.send_post_btn);
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

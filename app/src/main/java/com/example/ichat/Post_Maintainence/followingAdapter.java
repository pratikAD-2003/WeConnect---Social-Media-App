package com.example.ichat.Post_Maintainence;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ichat.OtherUserProfileFollow;
import com.example.ichat.ProfileModel;
import com.example.ichat.PushNotification.FcmNotificationsSender;
import com.example.ichat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class followingAdapter extends RecyclerView.Adapter<followingAdapter.MyViewHolder> {
    Context context;
    ArrayList<followingModel> list;

    DatabaseReference reference;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    boolean check_follow = false;
    boolean checkOnline = false;
    Activity activity;
    String UserName;

    public followingAdapter(Context context, ArrayList<followingModel> list, Activity activity) {
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
        View view = LayoutInflater.from(context).inflate(R.layout.following_user_items, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        reference = FirebaseDatabase.getInstance().getReference();

        holder.username.setText(list.get(position).getUsername());
        Picasso.get().load(list.get(position).getUserProfile()).into(holder.profilePic);

        SharedPreferences sp = context.getSharedPreferences("UserProfileData", MODE_PRIVATE);
        UserName = sp.getString("username", "Please Set Username.");

        holder.getFollowStatus(uid, list.get(position).getUserUid());
        holder.getSelfCheck(uid);

        holder.profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OtherUserProfileFollow.class);
                intent.putExtra("perUserN", list.get(position).getUsername());
                intent.putExtra("ProfileUri", list.get(position).getUserProfile());
                intent.putExtra("userUid", list.get(position).getUserUid());
                intent.putExtra("FCM_TOKEN", list.get(position).getUserFcm());
                intent.putExtra("DisplayName", list.get(position).getDisplayName());
                intent.putExtra("aboutUser", list.get(position).getAboutUser());
                context.startActivity(intent);
            }
        });

        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OtherUserProfileFollow.class);
                intent.putExtra("perUserN", list.get(position).getUsername());
                intent.putExtra("ProfileUri", list.get(position).getUserProfile());
                intent.putExtra("userUid", list.get(position).getUserUid());
                intent.putExtra("FCM_TOKEN", list.get(position).getUserFcm());
                intent.putExtra("DisplayName", list.get(position).getDisplayName());
                intent.putExtra("aboutUser", list.get(position).getAboutUser());
                context.startActivity(intent);
            }
        });

        holder.follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAppStatus(list.get(position).getUserUid());
                check_follow = true;
                try {
                    reference.child("Followed").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (check_follow) {
                                if (snapshot.child(uid).hasChild(list.get(position).getUserUid())) {
                                    reference.child("Followed").child(uid).child(list.get(position).getUserUid()).removeValue();
                                    reference.child("Following").child(list.get(position).getUserUid()).child(uid).removeValue();
                                    check_follow = false;
                                } else {
                                    followingModel model = new followingModel(list.get(position).getUsername(), list.get(position).getAboutUser(), list.get(position).getUserProfile(), list.get(position).getUserUid(), list.get(position).getUserFcm(), list.get(position).getDisplayName());
                                    followingModel model2 = new followingModel(holder.UserName, holder.About, holder.profileUri, uid, holder.Fcm, holder.dName);
                                    reference.child("Followed").child(uid).child(list.get(position).getUserUid()).setValue(model);
                                    reference.child("Following").child(list.get(position).getUserUid()).child(uid).setValue(model2);
                                    if (!checkOnline) {
                                        sendNotification(UserName + " send connection request.", list.get(position).getUserFcm(), "WeConnect");
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

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profilePic;
        TextView username;
        Button follow;
        String About = "", Num = "", UserName = "", profileUri = "", Fcm = "", dName = "";

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.following_userPic);
            username = itemView.findViewById(R.id.following_username);
            follow = itemView.findViewById(R.id.following_list_btn);
        }

        public void getSelfCheck(String uid) {
            reference.child("Following").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        if (snapshot.child(uid).hasChild(uid)) {
                            Toast.makeText(context, "Unreachable Statement!", Toast.LENGTH_SHORT).show();
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
